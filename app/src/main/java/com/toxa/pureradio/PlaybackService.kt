package com.toxa.pureradio

import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.PlaybackException
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionError
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.Futures
import com.google.common.collect.ImmutableList
import com.toxa.pureradio.data.repository.RadioRepository
import com.toxa.pureradio.data.model.Station
import com.toxa.pureradio.ui.MediaUtils
import androidx.media3.extractor.metadata.icy.IcyInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.guava.future
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.ConcurrentHashMap

enum class PlayerAction { Next, Previous }

class PlaybackService : MediaLibraryService() {

    companion object {
        const val CMD_PLAY_STATION = "play_station"
        const val CMD_ICY_TITLE = "com.toxa.pureradio.ICY_TITLE"
        const val EXTRA_ICY_TITLE = "icy_title"
        const val EXTRA_STATION_UUID = "station_uuid"
    }

    private var mediaLibrarySession: MediaLibrarySession? = null
    private val repository = RadioRepository()
    private val stationCache = ConcurrentHashMap<String, Station>()
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private class BrowseCache {
        private val cache = ConcurrentHashMap<String, Pair<Long, List<MediaItem>>>()
        private val CACHE_TIMEOUT_MS = 60_000L // 1 minute

        fun get(parentId: String): List<MediaItem>? {
            val entry = cache[parentId] ?: return null
            if (System.currentTimeMillis() - entry.first > CACHE_TIMEOUT_MS) {
                cache.remove(parentId)
                return null
            }
            return entry.second
        }

        fun put(parentId: String, items: List<MediaItem>) {
            cache[parentId] = System.currentTimeMillis() to items
        }
    }

    private val browseCache = BrowseCache()

    private var retryCount = 0
    private var lastErrorMediaId: String? = null
    private var retryJob: Job? = null

    private fun cancelRetryJob() {
        retryJob?.cancel()
        retryJob = null
    }
    private var currentAudioFormat: Format? = null

    @UnstableApi
    private class InterceptingPlayer(player: Player, private val service: PlaybackService) : ForwardingPlayer(player) {
        override fun isCommandAvailable(command: Int): Boolean {
            if (command == Player.COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM) return false
            if (command == Player.COMMAND_SEEK_TO_NEXT || 
                command == Player.COMMAND_SEEK_TO_PREVIOUS ||
                command == Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM ||
                command == Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM) return true
            return super.isCommandAvailable(command)
        }
        override fun getAvailableCommands(): Player.Commands {
            return super.getAvailableCommands().buildUpon()
                .remove(Player.COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM)
                .add(Player.COMMAND_SEEK_TO_NEXT)
                .add(Player.COMMAND_SEEK_TO_PREVIOUS)
                .add(Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
                .add(Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)
                .build()
        }

        override fun stop() {
            service.cancelRetryJob()
            super.stop()
        }

        override fun getMediaMetadata(): MediaMetadata {
            val metadata = super.getMediaMetadata()
            var builder = metadata.buildUpon()

            if (metadata.title.isNullOrEmpty() && metadata.displayTitle.isNullOrEmpty()) {
                val stationJson = super.getCurrentMediaItem()?.mediaMetadata?.extras
                    ?.getString("station_full_json")
                if (stationJson != null) {
                    try {
                        val station = com.google.gson.Gson().fromJson(stationJson, Station::class.java)
                        if (!station.name.isNullOrEmpty()) {
                            builder = builder.setTitle(station.name)
                        }
                    } catch (_: Exception) {}
                }
            }

            val format = service.currentAudioFormat
            
            val techInfo = format?.let { f ->
                buildString {
                    if (f.bitrate > 0) append("${f.bitrate / 1000}k")
                    val codec = f.sampleMimeType?.removePrefix("audio/")?.uppercase()
                        ?.replace("MPEG", "MP3")?.replace("MP4A-LATM", "AAC")
                    if (codec != null) {
                        if (isNotEmpty()) append(" ")
                        append(codec)
                    }
                }
            } ?: ""

            if (techInfo.isEmpty()) return builder.build()

            val currentArtist = metadata.artist
            val displayArtist = if (currentArtist.isNullOrEmpty()) {
                techInfo
            } else {
                "$currentArtist \u2022 $techInfo"
            }

            return builder
                .setArtist(displayArtist)
                .setSubtitle(displayArtist)
                .build()
        }
    }

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(15000)
            .setReadTimeoutMs(30000)
            .setDefaultRequestProperties(mapOf("Icy-MetaData" to "1"))
        
        val dataSourceFactory = DefaultDataSource.Factory(this, httpDataSourceFactory)
        
        val mediaSourceFactory = DefaultMediaSourceFactory(this)
            .setDataSourceFactory(dataSourceFactory)

        val prefs = getSharedPreferences("pure_radio_prefs", MODE_PRIVATE)
        val audioPassthrough = prefs.getBoolean("audio_passthrough", false)

        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                /* minBufferMs */ 30_000,
                /* maxBufferMs */ 60_000,
                /* bufferForPlaybackMs */ 5_000,
                /* bufferForPlaybackAfterRebufferMs */ 10_000
            )
            .build()

        val playerBuilder = ExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setLoadControl(loadControl)

        if (audioPassthrough) {
            val renderersFactory = DefaultRenderersFactory(this)
                .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
                .setEnableAudioFloatOutput(true)
            playerBuilder.setRenderersFactory(renderersFactory)
        }

        val player = InterceptingPlayer(playerBuilder.build(), this)
        player.repeatMode = Player.REPEAT_MODE_ALL
        
        player.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                retryJob?.cancel()
                retryJob = null
                if (mediaItem?.mediaId != lastErrorMediaId) {
                    retryCount = 0
                    lastErrorMediaId = null
                }
                currentAudioFormat = null

                val metadata = mediaItem?.mediaMetadata ?: return
                val stationJson = metadata.extras?.getString("station_full_json") ?: return
                val uuid = mediaItem.mediaId
                
                val prefs = getSharedPreferences("pure_radio_prefs", MODE_PRIVATE)
                prefs.edit()
                    .putString("last_station_json", stationJson)
                    .putString("last_station_uuid", uuid)
                    .apply()
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    retryCount = 0
                    lastErrorMediaId = null
                }
            }

            override fun onMediaMetadataChanged(metadata: MediaMetadata) {
                // Primary metadata updates (including ICY) are handled by Media3 session automatically.
                // This listener is here for potential future enhancements.
            }

            override fun onMetadata(metadata: androidx.media3.common.Metadata) {
                // ICY metadata may be masked by static MediaItem metadata in the merged
                // mediaMetadata, so broadcast the raw ICY title directly to controllers.
                for (i in 0 until metadata.length()) {
                    val entry = metadata[i]
                    if (entry is IcyInfo && !entry.title.isNullOrEmpty()) {
                        val currentItem = player.currentMediaItem
                        val uuid = currentItem?.mediaId?.let {
                            if (it.contains("|station:")) it.split("|")[1].removePrefix("station:") else it
                        }
                        val args = Bundle().apply {
                            putString(EXTRA_ICY_TITLE, entry.title.toString())
                            putString(EXTRA_STATION_UUID, uuid)
                        }
                        try {
                            mediaLibrarySession?.broadcastCustomCommand(
                                SessionCommand(CMD_ICY_TITLE, Bundle.EMPTY),
                                args
                            )
                        } catch (_: Exception) {}
                    }
                }
            }

            override fun onTracksChanged(tracks: Tracks) {
                for (group in tracks.groups) {
                    if (group.type == C.TRACK_TYPE_AUDIO && group.isSelected) {
                        for (i in 0 until group.length) {
                            if (group.isTrackSelected(i)) {
                                currentAudioFormat = group.getTrackFormat(i)
                                break
                            }
                        }
                    }
                }
            }

            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                val currentMediaItem = player.currentMediaItem ?: return
                val mediaId = currentMediaItem.mediaId
                
                if (mediaId != lastErrorMediaId) {
                    lastErrorMediaId = mediaId
                    retryCount = 0
                }

                val isNetworkError = error.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED ||
                                     error.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT ||
                                     error.errorCode == PlaybackException.ERROR_CODE_IO_UNSPECIFIED
                
                val maxRetries = if (isNetworkError) Int.MAX_VALUE else 3
                
                if (retryCount < maxRetries) {
                    // Exponential backoff: 2s, 4s, 8s, 16s... cap at 30s
                    val backoffMs = (Math.pow(2.0, retryCount.toDouble() + 1) * 1000).toLong().coerceAtMost(30000)
                    retryCount++
                    
                    retryJob?.cancel()
                    retryJob = serviceScope.launch {
                        delay(backoffMs)
                        if (player.currentMediaItem?.mediaId != mediaId) return@launch
                        player.prepare()
                        player.play()
                    }
                } else if (player.hasNextMediaItem()) {
                    // Exhausted retries for a specific stream error, move to next
                    retryCount = 0
                    lastErrorMediaId = null
                    retryJob?.cancel()
                    retryJob = serviceScope.launch {
                        delay(2000)
                        if (player.currentMediaItem?.mediaId != mediaId) return@launch
                        player.seekToNextMediaItem()
                        player.prepare()
                        player.play()
                    }
                }
            }
        })

        mediaLibrarySession = MediaLibrarySession.Builder(this, player, object : MediaLibrarySession.Callback {

            override fun onPostConnect(
                session: MediaSession,
                controller: MediaSession.ControllerInfo
            ) {
                val pkg = controller.packageName
                val isAutomotive = pkg == "com.android.car.media" || pkg == "com.android.car.carlauncher"
                    || controller.connectionHints.getBoolean("android.media.extra.IS_CAR_UI", false)
                if (isAutomotive && player.mediaItemCount == 0) {
                    serviceScope.launch {
                        delay(500)
                        val prefs = getSharedPreferences("pure_radio_prefs", MODE_PRIVATE)
                        
                        // Try to load favorites first to provide a playlist
                        val favoritesJson = prefs.getString("favorite_stations_json", null)
                        val lastUuid = prefs.getString("last_station_uuid", null)
                        
                        if (favoritesJson != null) {
                            try {
                                val stations = com.google.gson.Gson().fromJson<List<Station>>(
                                    favoritesJson, 
                                    object : com.google.gson.reflect.TypeToken<List<Station>>() {}.type
                                )
                                if (stations.isNotEmpty()) {
                                    val mediaItems = stations.map { createPlayableItem(it, forPlayback = true) }
                                    val startIndex = stations.indexOfFirst { it.stationUuid == lastUuid }.coerceAtLeast(0)
                                    player.setMediaItems(mediaItems, startIndex, 0L)
                                    player.prepare()
                                    // Don't auto-play on connect, just prepare
                                    return@launch
                                }
                            } catch (_: Exception) {}
                        }

                        // Fallback to single last station if favorites not available
                        val lastJson = prefs.getString("last_station_json", null)
                        if (lastJson != null) {
                            try {
                                val station = com.google.gson.Gson().fromJson(lastJson, Station::class.java)
                                val mediaItem = createPlayableItem(station, forPlayback = true)
                                player.setMediaItem(mediaItem)
                                player.prepare()
                            } catch (_: Exception) {}
                        }
                    }
                }
            }

            override fun onCustomCommand(
                session: MediaSession,
                controller: MediaSession.ControllerInfo,
                customCommand: SessionCommand,
                args: Bundle
            ): ListenableFuture<SessionResult> {
                if (customCommand.customAction == CMD_PLAY_STATION) {
                    val extras = args
                    val station = Station(
                        stationUuid = extras.getString("station_uuid", ""),
                        name = extras.getString("station_name", ""),
                        url = extras.getString("station_url", ""),
                        favicon = extras.getString("station_favicon", ""),
                        tags = extras.getString("station_tags", ""),
                        codec = extras.getString("station_codec", ""),
                        country = extras.getString("station_country", ""),
                        countryCode = extras.getString("station_country_code", null),
                        language = extras.getString("station_language", ""),
                        votes = extras.getInt("station_votes", 0),
                        bitrate = extras.getInt("station_bitrate", 0)
                    )
                    val isHls = extras.getBoolean("is_hls", false)
                    val mediaItem = createPlayableItem(station, isHls, forPlayback = true)
                    player.stop()
                    player.clearMediaItems()
                    player.setMediaItem(mediaItem)
                    player.prepare()
                    player.play()
                    val lastStationJson = com.google.gson.Gson().toJson(station)
                    getSharedPreferences("pure_radio_prefs", MODE_PRIVATE).edit().putString("last_station_json", lastStationJson).apply()
                    return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
                }
                return super.onCustomCommand(session, controller, customCommand, args)
            }

            override fun onGetLibraryRoot(
                session: MediaLibrarySession,
                browser: MediaSession.ControllerInfo,
                params: LibraryParams?
            ): ListenableFuture<LibraryResult<MediaItem>> {
                val rootItem = MediaItem.Builder()
                    .setMediaId("root")
                    .setMediaMetadata(MediaMetadata.Builder()
                        .setIsBrowsable(true)
                        .setIsPlayable(false)
                        .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_MIXED)
                        .setTitle("Pure Radio")
                        .build())
                    .build()
                
                val rootParams = LibraryParams.Builder()
                    .setExtras(Bundle().apply {
                        putBoolean("android.media.browse.SEARCH_SUPPORTED", true)
                        putBoolean("android.media.browse.CONTENT_STYLE_SUPPORTED", true)
                    })
                    .build()

                return Futures.immediateFuture(LibraryResult.ofItem(rootItem, rootParams))
            }

            override fun onGetChildren(
                session: MediaLibrarySession,
                browser: MediaSession.ControllerInfo,
                parentId: String,
                page: Int,
                pageSize: Int,
                params: LibraryParams?
            ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
                val prefs = getSharedPreferences("pure_radio_prefs", MODE_PRIVATE)
                return when (parentId) {
                    "root" -> {
                        val items = listOf(
                            createBrowsableItem("home_screen", getString(R.string.nav_home), Uri.parse(MediaUtils.getCategoryImageUrl("home_screen"))),
                            createBrowsableItem("popular", getString(R.string.nav_popular), Uri.parse(MediaUtils.getCategoryImageUrl("popular"))),
                            createBrowsableItem("favourites", getString(R.string.nav_favourites), Uri.parse(MediaUtils.getCategoryImageUrl("favourites"))),
                            createBrowsableItem("recent", getString(R.string.nav_recent), Uri.parse(MediaUtils.getCategoryImageUrl("recent"))),
                            createBrowsableItem("genres", getString(R.string.nav_genres), Uri.parse(MediaUtils.getCategoryImageUrl("genres"))),
                            createBrowsableItem("countries", getString(R.string.nav_countries), Uri.parse(MediaUtils.getCategoryImageUrl("countries")))
                        )
                        Futures.immediateFuture(LibraryResult.ofItemList(ImmutableList.copyOf(items), params))
                    }
                    "home_screen" -> {
                        val visibleGenres = prefs.getStringSet("visible_genres", emptySet()) ?: emptySet()
                        val visibleCountries = prefs.getStringSet("visible_countries", emptySet()) ?: emptySet()
                        val items = mutableListOf<MediaItem>()
                        visibleGenres.forEach { 
                            items.add(createBrowsableItem("genre_$it", it, Uri.parse(MediaUtils.getGenreImageUrl(it)))) 
                        }
                        visibleCountries.forEach { countryName ->
                            items.add(createBrowsableItem("country_$countryName", countryName, Uri.parse(MediaUtils.getCategoryImageUrl("countries")))) 
                        }
                        Futures.immediateFuture(LibraryResult.ofItemList(ImmutableList.copyOf(items), params))
                    }
                    "popular" -> serviceScope.future {
                        try {
                            val cached = browseCache.get("popular")
                            val items = cached ?: withTimeoutOrNull(10_000L) {
                                val stations = repository.getTopStations(limit = 100)
                                val newItems = stations.map { createPlayableItem(it, parentId = "popular") }
                                browseCache.put("popular", newItems)
                                newItems
                            } ?: emptyList()
                            
                            val pagedItems = if (page >= 0 && pageSize >= 1) {
                                val start = (page * pageSize).coerceAtMost(items.size)
                                val end = ((page + 1) * pageSize).coerceAtMost(items.size)
                                items.subList(start, end)
                            } else items
                            LibraryResult.ofItemList(ImmutableList.copyOf(pagedItems), params)
                        } catch (e: Exception) {
                            LibraryResult.ofItemList(ImmutableList.of(), params)
                        }
                    }
                    "genres" -> serviceScope.future {
                        try {
                            val cached = browseCache.get("genres")
                            val items = cached ?: withTimeoutOrNull(10_000L) {
                                val tags = repository.getTags(limit = 500)
                                val newItems = tags.map { tag ->
                                    createBrowsableItem("genre_${tag.name}", tag.name, Uri.parse(MediaUtils.getGenreImageUrl(tag.name)))
                                }
                                browseCache.put("genres", newItems)
                                newItems
                            } ?: emptyList()
                            
                            val pagedItems = if (page >= 0 && pageSize >= 1) {
                                val start = (page * pageSize).coerceAtMost(items.size)
                                val end = ((page + 1) * pageSize).coerceAtMost(items.size)
                                items.subList(start, end)
                            } else items
                            LibraryResult.ofItemList(ImmutableList.copyOf(pagedItems), params)
                        } catch (e: Exception) {
                            LibraryResult.ofItemList(ImmutableList.of(), params)
                        }
                    }
                    "countries" -> serviceScope.future {
                        try {
                            val cached = browseCache.get("countries")
                            val items = cached ?: withTimeoutOrNull(10_000L) {
                                val countries = repository.getCountries()
                                val newItems = countries.map { country ->
                                    val flagUrl = MediaUtils.getCountryFlagUrl(country.iso_3166_1)
                                    createBrowsableItem("country_${country.name}", country.name, flagUrl?.let { Uri.parse(it) })
                                }
                                browseCache.put("countries", newItems)
                                newItems
                            } ?: emptyList()
                            
                            val pagedItems = if (page >= 0 && pageSize >= 1) {
                                val start = (page * pageSize).coerceAtMost(items.size)
                                val end = ((page + 1) * pageSize).coerceAtMost(items.size)
                                items.subList(start, end)
                            } else items
                            LibraryResult.ofItemList(ImmutableList.copyOf(pagedItems), params)
                        } catch (e: Exception) {
                            LibraryResult.ofItemList(ImmutableList.of(), params)
                        }
                    }
                    "favourites" -> serviceScope.future {
                        try {
                            val json = prefs.getString("favorite_stations_json", null)
                            val items = if (json != null) {
                                val stations = try {
                                    com.google.gson.Gson().fromJson<List<Station>>(json, object : com.google.gson.reflect.TypeToken<List<Station>>() {}.type)
                                } catch (e: Exception) { emptyList() }
                                stations.map { createPlayableItem(it, parentId = "favourites") }
                            } else emptyList()
                            val pagedItems = if (page >= 0 && pageSize >= 1) {
                                val start = (page * pageSize).coerceAtMost(items.size)
                                val end = ((page + 1) * pageSize).coerceAtMost(items.size)
                                items.subList(start, end)
                            } else items
                            LibraryResult.ofItemList(ImmutableList.copyOf(pagedItems), params)
                        } catch (e: Exception) {
                            LibraryResult.ofItemList(ImmutableList.of(), params)
                        }
                    }
                    "recent" -> serviceScope.future {
                        try {
                            val json = prefs.getString("recent_stations_json", null)
                            val items = if (json != null) {
                                val stations = try {
                                    com.google.gson.Gson().fromJson<List<Station>>(json, object : com.google.gson.reflect.TypeToken<List<Station>>() {}.type)
                                } catch (e: Exception) { emptyList() }
                                stations.map { createPlayableItem(it, parentId = "recent") }
                            } else emptyList()
                            val pagedItems = if (page >= 0 && pageSize >= 1) {
                                val start = (page * pageSize).coerceAtMost(items.size)
                                val end = ((page + 1) * pageSize).coerceAtMost(items.size)
                                items.subList(start, end)
                            } else items
                            LibraryResult.ofItemList(ImmutableList.copyOf(pagedItems), params)
                        } catch (e: Exception) {
                            LibraryResult.ofItemList(ImmutableList.of(), params)
                        }
                    }
                    else -> if (parentId.startsWith("genre_")) serviceScope.future {
                        try {
                            val genre = parentId.removePrefix("genre_")
                            val cached = browseCache.get(parentId)
                            val items = cached ?: withTimeoutOrNull(10_000L) {
                                val stations = repository.searchStations(tag = genre, limit = 100)
                                val newItems = stations.map { createPlayableItem(it, parentId = parentId) }
                                browseCache.put(parentId, newItems)
                                newItems
                            } ?: emptyList()

                            val pagedItems = if (page >= 0 && pageSize >= 1) {
                                val start = (page * pageSize).coerceAtMost(items.size)
                                val end = ((page + 1) * pageSize).coerceAtMost(items.size)
                                items.subList(start, end)
                            } else items
                            LibraryResult.ofItemList(ImmutableList.copyOf(pagedItems), params)
                        } catch (e: Exception) {
                            LibraryResult.ofItemList(ImmutableList.of(), params)
                        }
                    } else if (parentId.startsWith("country_")) serviceScope.future {
                        try {
                            val country = parentId.removePrefix("country_")
                            val cached = browseCache.get(parentId)
                            val items = cached ?: withTimeoutOrNull(10_000L) {
                                val stations = repository.searchStations(country = country, limit = 100)
                                val newItems = stations.map { createPlayableItem(it, parentId = parentId) }
                                browseCache.put(parentId, newItems)
                                newItems
                            } ?: emptyList()
                            
                            val pagedItems = if (page >= 0 && pageSize >= 1) {
                                val start = (page * pageSize).coerceAtMost(items.size)
                                val end = ((page + 1) * pageSize).coerceAtMost(items.size)
                                items.subList(start, end)
                            } else items
                            LibraryResult.ofItemList(ImmutableList.copyOf(pagedItems), params)
                        } catch (e: Exception) {
                            LibraryResult.ofItemList(ImmutableList.of(), params)
                        }
                    } else {
                        serviceScope.future {
                            try {
                                val station = withTimeoutOrNull(10_000L) { repository.getStation(parentId) }
                                if (station != null) {
                                    LibraryResult.ofItemList(ImmutableList.of(createPlayableItem(station)), params)
                                } else {
                                    LibraryResult.ofItemList(ImmutableList.of<MediaItem>(), params)
                                }
                            } catch (e: Exception) {
                                LibraryResult.ofItemList(ImmutableList.of(), params)
                            }
                        }
                    }
                }
            }

            override fun onGetItem(
                session: MediaLibrarySession,
                browser: MediaSession.ControllerInfo,
                mediaId: String
            ): ListenableFuture<LibraryResult<MediaItem>> {
                val realId = if (mediaId.contains("|station:")) mediaId.split("|")[1].removePrefix("station:") else mediaId

                // Resolve locally first to avoid network calls while the car displays items.
                findItemInPlayer(mediaId)?.let { item ->
                    return Futures.immediateFuture(LibraryResult.ofItem(item, null))
                }
                val cachedStation = stationCache[realId]
                if (cachedStation != null) {
                    return Futures.immediateFuture(LibraryResult.ofItem(createPlayableItem(cachedStation), null))
                }
                findStationInPrefs(realId)?.let { station ->
                    cacheStation(station)
                    return Futures.immediateFuture(LibraryResult.ofItem(createPlayableItem(station), null))
                }

                return serviceScope.future {
                    try {
                        val station = withTimeoutOrNull(10_000L) { repository.getStation(realId) }
                        if (station != null) {
                            stationCache.put(realId, station)
                            LibraryResult.ofItem(createPlayableItem(station), null)
                        } else {
                            val item = when (mediaId) {
                                "root" -> MediaItem.Builder()
                                    .setMediaId("root")
                                    .setMediaMetadata(MediaMetadata.Builder()
                                        .setIsBrowsable(true)
                                        .setIsPlayable(false)
                                        .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_MIXED)
                                        .setTitle(getString(R.string.app_name))
                                        .build())
                                    .build()
                                "home_screen" -> createBrowsableItem("home_screen", getString(R.string.nav_home), Uri.parse(MediaUtils.getCategoryImageUrl("home_screen")))
                                "popular" -> createBrowsableItem("popular", getString(R.string.nav_popular), Uri.parse(MediaUtils.getCategoryImageUrl("popular")))
                                "favourites" -> createBrowsableItem("favourites", getString(R.string.nav_favourites), Uri.parse(MediaUtils.getCategoryImageUrl("favourites")))
                                "recent" -> createBrowsableItem("recent", getString(R.string.nav_recent), Uri.parse(MediaUtils.getCategoryImageUrl("recent")))
                                "genres" -> createBrowsableItem("genres", getString(R.string.nav_genres), Uri.parse(MediaUtils.getCategoryImageUrl("genres")))
                                "countries" -> createBrowsableItem("countries", getString(R.string.nav_countries), Uri.parse(MediaUtils.getCategoryImageUrl("countries")))
                                else -> null
                            }
                            if (item != null) {
                                LibraryResult.ofItem(item, null)
                            } else {
                                LibraryResult.ofError(SessionError.ERROR_BAD_VALUE)
                            }
                        }
                    } catch (e: Exception) {
                        LibraryResult.ofError(SessionError.ERROR_NOT_SUPPORTED)
                    }
                }
            }

            override fun onSearch(
                session: MediaLibrarySession,
                browser: MediaSession.ControllerInfo,
                query: String,
                params: LibraryParams?
            ): ListenableFuture<LibraryResult<Void>> {
                serviceScope.launch {
                    try {
                        val stations = repository.searchStations(query = query, limit = 50)
                        session.notifySearchResultChanged(browser, query, stations.size, params)
                    } catch (_: Exception) {}
                }
                return Futures.immediateFuture(LibraryResult.ofVoid())
            }

            override fun onGetSearchResult(
                session: MediaLibrarySession,
                browser: MediaSession.ControllerInfo,
                query: String,
                page: Int,
                pageSize: Int,
                params: LibraryParams?
            ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
                val safePage = page.coerceAtLeast(0)
                val safePageSize = pageSize.coerceIn(1, 200)
                return serviceScope.future {
                    try {
                        val stations = withTimeoutOrNull(10_000L) {
                            repository.searchStations(query = query, limit = safePageSize, offset = safePage * safePageSize)
                        } ?: emptyList()
                        val items = stations.map { createPlayableItem(it, parentId = "search_$query") }
                        LibraryResult.ofItemList(ImmutableList.copyOf(items), params)
                    } catch (e: Exception) {
                        LibraryResult.ofItemList(ImmutableList.of(), params)
                    }
                }
            }

            override fun onAddMediaItems(
                mediaSession: MediaSession,
                controller: MediaSession.ControllerInfo,
                mediaItems: List<MediaItem>
            ): ListenableFuture<List<MediaItem>> {
                return serviceScope.future {
                    if (mediaItems.size == 1) {
                        val item = mediaItems[0]
                        if (item.mediaId.contains("|station:")) {
                            val parts = item.mediaId.split("|")
                            val parentId = parts[0]
                            val stationUuid = parts[1].removePrefix("station:")
                            
                            // Resolve siblings for playlist support on car display
                            val cachedItems = browseCache.get(parentId)
                            if (cachedItems != null) {
                                val index = cachedItems.indexOfFirst { 
                                    it.mediaId.split("|").lastOrNull()?.removePrefix("station:") == stationUuid 
                                }
                                if (index != -1) {
                                    serviceScope.launch(Dispatchers.Main) {
                                        mediaSession.player.stop()
                                        mediaSession.player.clearMediaItems()
                                        mediaSession.player.setMediaItems(cachedItems.map { createPlaybackItemFromBrowseItem(it) }, index, 0L)
                                        mediaSession.player.prepare()
                                        mediaSession.player.play()
                                    }
                                    return@future emptyList()
                                }
                            }

                            val siblings = when {
                                parentId == "popular" -> repository.getTopStations(limit = 100)
                                parentId == "favourites" -> {
                                    val prefs = getSharedPreferences("pure_radio_prefs", MODE_PRIVATE)
                                    val json = prefs.getString("favorite_stations_json", null)
                                    if (json != null) {
                                        try {
                                            com.google.gson.Gson().fromJson<List<Station>>(json, object : com.google.gson.reflect.TypeToken<List<Station>>() {}.type)
                                        } catch (e: Exception) { emptyList() }
                                    } else emptyList()
                                }
                                parentId == "recent" -> {
                                    val prefs = getSharedPreferences("pure_radio_prefs", MODE_PRIVATE)
                                    val json = prefs.getString("recent_stations_json", null)
                                    if (json != null) {
                                        try {
                                            com.google.gson.Gson().fromJson<List<Station>>(json, object : com.google.gson.reflect.TypeToken<List<Station>>() {}.type)
                                        } catch (e: Exception) { emptyList() }
                                    } else emptyList()
                                }
                                parentId.startsWith("genre_") -> {
                                    val genre = parentId.removePrefix("genre_")
                                    repository.searchStations(tag = genre, limit = 100)
                                }
                                parentId.startsWith("country_") -> {
                                    val country = parentId.removePrefix("country_")
                                    repository.searchStations(country = country, limit = 100)
                                }
                                parentId.startsWith("search_") -> {
                                    val query = parentId.removePrefix("search_")
                                    repository.searchStations(query = query, limit = 50)
                                }
                                else -> emptyList()
                            }
                            
                            if (siblings.isNotEmpty()) {
                                // Create items for siblings, preserving the contextual ID
                                val items = siblings.map { createPlayableItem(it, parentId = parentId, forPlayback = true) }
                                
                                // Find the index of the requested item
                                val index = siblings.indexOfFirst { it.stationUuid == stationUuid }
                                if (index != -1) {
                                    // Set the index in the player session
                                    serviceScope.launch(Dispatchers.Main) {
                                        mediaSession.player.stop()
                                        mediaSession.player.clearMediaItems()
                                        mediaSession.player.setMediaItems(items, index, 0L)
                                        mediaSession.player.prepare()
                                        mediaSession.player.play()
                                    }
                                    // Return empty list to prevent Media3 from adding the item again
                                    return@future emptyList()
                                }
                                return@future items
                            }
                        }
                    }

                    mediaItems.map { item ->
                        if (item.requestMetadata.mediaUri != null || item.localConfiguration?.uri != null) {
                            createPlaybackItemFromBrowseItem(item)
                        } else {
                            val realId = if (item.mediaId.contains("|station:")) 
                                item.mediaId.split("|")[1].removePrefix("station:") 
                                else item.mediaId
                            val station = stationCache[realId] ?: repository.getStation(realId)
                            if (station != null) createPlayableItem(station, forPlayback = true) else item
                        }
                    }
                }
            }
        }).setPeriodicPositionUpdateEnabled(false).build()
    }

    private fun createBrowsableItem(id: String, title: String, artworkUri: Uri? = null): MediaItem {
        val metadata = MediaMetadata.Builder()
            .setIsBrowsable(true)
            .setIsPlayable(false)
            .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_MIXED)
            .setTitle(title)
        
        if (artworkUri != null) {
            metadata.setArtworkUri(artworkUri)
        }
        
        val extras = Bundle()
        extras.putInt("android.media.browse.CONTENT_STYLE_BROWSABLE_HINT", 2) // Grid
        extras.putInt("android.media.browse.CONTENT_STYLE_PLAYABLE_HINT", 2)  // Grid
        if (artworkUri != null) {
            extras.putString("android.media.metadata.DISPLAY_ICON_URI", artworkUri.toString())
        }
        metadata.setExtras(extras)

        return MediaItem.Builder()
            .setMediaId(id)
            .setMediaMetadata(metadata.build())
            .build()
    }

    private fun getAppIconUri(): Uri {
        val resourceId = R.drawable.ic_radio_logo
        return Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(resources.getResourcePackageName(resourceId))
            .appendPath(resources.getResourceTypeName(resourceId))
            .appendPath(resources.getResourceEntryName(resourceId))
            .build()
    }

    /**
     * Raw PNG bytes of the app icon, so car displays can show it even though they
     * cannot resolve our android.resource:// artwork URI directly.
     */
    @android.annotation.SuppressLint("ResourceType")
    private fun openAppIconStream() = resources.openRawResource(R.drawable.ic_radio_logo)

    private val appIconBytes: ByteArray? by lazy {
        try {
            openAppIconStream().use { it.readBytes() }
        } catch (_: Exception) {
            null
        }
    }

    private fun cacheStation(station: Station) {
        if (stationCache.size >= 500) {
            stationCache.keys.take(100).forEach { stationCache.remove(it) }
        }
        stationCache[station.stationUuid] = station
    }

    private fun findItemInPlayer(mediaId: String): MediaItem? {
        val p = mediaLibrarySession?.player ?: return null
        val current = p.currentMediaItem
        if (current != null && current.mediaId == mediaId) return current
        for (i in 0 until p.mediaItemCount) {
            val item = p.getMediaItemAt(i)
            if (item.mediaId == mediaId) return item
        }
        return null
    }

    private fun findStationInPrefs(mediaId: String): Station? {
        val prefs = getSharedPreferences("pure_radio_prefs", MODE_PRIVATE)
        val jsonKeys = listOf("favorite_stations_json", "recent_stations_json")
        for (key in jsonKeys) {
            val json = prefs.getString(key, null) ?: continue
            try {
                val stations = com.google.gson.Gson().fromJson<List<Station>>(
                    json, object : com.google.gson.reflect.TypeToken<List<Station>>() {}.type
                )
                stations.find { it.stationUuid == mediaId }?.let { return it }
            } catch (_: Exception) {}
        }
        val lastJson = prefs.getString("last_station_json", null) ?: return null
        return try {
            val station = com.google.gson.Gson().fromJson(lastJson, Station::class.java)
            if (station.stationUuid == mediaId) station else null
        } catch (_: Exception) { null }
    }

    private fun createPlayableItem(
        station: Station,
        isHls: Boolean = false,
        parentId: String? = null,
        forPlayback: Boolean = false
    ): MediaItem {
        cacheStation(station)
        
        val stationArtworkUrl = MediaUtils.getStationArtworkUrl(station.favicon, station.countryCode)
        // Car hosts may not be able to resolve android.resource:// URIs. Leave the
        // fallback URI unset so they use the embedded artwork data below.
        val artworkUri = stationArtworkUrl?.let { Uri.parse(it) }
        val artworkData = if (stationArtworkUrl == null) appIconBytes else null

        val isHlsStream = isHls
                || station.url.lowercase().let { it.endsWith(".m3u8") || it.endsWith(".m3u") }
                || (station.codec?.contains("hls", ignoreCase = true) == true)
        
        val stationJson = com.google.gson.Gson().toJson(station)
        val extras = Bundle().apply {
            putString("station_full_json", stationJson)
            putInt("android.media.browse.CONTENT_STYLE_PLAYABLE_HINT", 2) // Grid
            artworkUri?.let {
                putString("android.media.metadata.DISPLAY_ICON_URI", it.toString())
                putString("android.media.metadata.ALBUM_ART_URI", it.toString())
            }
        }

        val mediaId = if (parentId != null) "$parentId|station:${station.stationUuid}" else station.stationUuid

        val metadataBuilder = MediaMetadata.Builder()
            .setIsBrowsable(false)
            .setIsPlayable(true)
            .setMediaType(MediaMetadata.MEDIA_TYPE_RADIO_STATION)
            .setExtras(extras)
            .apply { artworkUri?.let { setArtworkUri(it) } }
        if (artworkData != null) {
            metadataBuilder.setArtworkData(artworkData, MediaMetadata.PICTURE_TYPE_OTHER)
        }
        if (!forPlayback) {
            metadataBuilder.setTitle(station.name)
        }
        metadataBuilder.setArtist(station.tags)

        val builder = MediaItem.Builder()
            .setMediaId(mediaId)
            .setUri(station.url)
            .setLiveConfiguration(MediaItem.LiveConfiguration.Builder()
                .setMaxPlaybackSpeed(1.02f)
                .setMinPlaybackSpeed(0.98f)
                .build())
            .setMediaMetadata(metadataBuilder.build())
        if (isHlsStream) {
            builder.setMimeType(MimeTypes.APPLICATION_M3U8)
        }
        return builder.build()
    }

    private fun createPlaybackItemFromBrowseItem(item: MediaItem): MediaItem {
        return item.buildUpon()
            .setMediaMetadata(item.mediaMetadata.buildUpon().setTitle(null).build())
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaLibrarySession
    }

    override fun onTaskRemoved(rootIntent: android.content.Intent?) {
        val p = mediaLibrarySession?.player
        if (p != null && p.playWhenReady && p.mediaItemCount > 0) {
            return
        }
        stopSelf()
    }

    override fun onDestroy() {
        retryJob?.cancel()
        retryJob = null
        mediaLibrarySession?.run {
            release()
            mediaLibrarySession = null
        }
        serviceJob.cancel()
        super.onDestroy()
    }
}
