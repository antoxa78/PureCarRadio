package com.toxa.pureradio

import android.net.Uri
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
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
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.Futures
import com.google.common.collect.ImmutableList
import com.toxa.pureradio.data.repository.RadioRepository
import com.toxa.pureradio.data.model.Station
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.guava.future
import java.util.concurrent.ConcurrentHashMap

enum class PlayerAction { Next, Previous }

class PlaybackService : MediaLibraryService() {

    companion object {
        private val _playerAction = MutableSharedFlow<PlayerAction>(replay = 1, extraBufferCapacity = 1)
        val playerAction: SharedFlow<PlayerAction> = _playerAction.asSharedFlow()
        fun sendPlayerAction(action: PlayerAction) { _playerAction.tryEmit(action) }
        const val CMD_PLAY_STATION = "play_station"
    }

    private var mediaLibrarySession: MediaLibrarySession? = null
    private val repository = RadioRepository()
    private val stationCache = ConcurrentHashMap<String, Station>()
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private class InterceptingPlayer(player: Player) : ForwardingPlayer(player) {
        override fun seekToNext() { sendPlayerAction(PlayerAction.Next) }
        override fun seekToPrevious() { sendPlayerAction(PlayerAction.Previous) }
        override fun seekToNextMediaItem() { sendPlayerAction(PlayerAction.Next) }
        override fun seekToPreviousMediaItem() { sendPlayerAction(PlayerAction.Previous) }
        override fun isCommandAvailable(command: Int): Boolean {
            if (command == Player.COMMAND_SEEK_TO_NEXT || command == Player.COMMAND_SEEK_TO_PREVIOUS) return true
            return super.isCommandAvailable(command)
        }
        override fun getAvailableCommands(): Player.Commands {
            return super.getAvailableCommands().buildUpon()
                .add(Player.COMMAND_SEEK_TO_NEXT)
                .add(Player.COMMAND_SEEK_TO_PREVIOUS)
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

        val player = InterceptingPlayer(playerBuilder.build())

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
                        val json = prefs.getString("last_station_json", null)
                        if (json != null) {
                            try {
                                val station = com.google.gson.Gson().fromJson(json, Station::class.java)
                                val mediaItem = createPlayableItem(station)
                                player.stop()
                                player.clearMediaItems()
                                player.setMediaItem(mediaItem)
                                player.prepare()
                                player.play()
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
                    val mediaItem = createPlayableItem(station, isHls)
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
                return Futures.immediateFuture(LibraryResult.ofItem(rootItem, params))
            }

            override fun onGetChildren(
                session: MediaLibrarySession,
                browser: MediaSession.ControllerInfo,
                parentId: String,
                page: Int,
                pageSize: Int,
                params: LibraryParams?
            ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
                return when (parentId) {
                    "root" -> {
                        val items = listOf(
                            createBrowsableItem("popular", "Popular Stations"),
                            createBrowsableItem("favourites", "Favourites"),
                            createBrowsableItem("genres", "Genres")
                        )
                        Futures.immediateFuture(LibraryResult.ofItemList(ImmutableList.copyOf(items), params))
                    }
                    "popular" -> serviceScope.future {
                        try {
                            val stations = repository.getTopStations(limit = 20)
                            val items = stations.map { createPlayableItem(it) }
                            LibraryResult.ofItemList(ImmutableList.copyOf(items), params)
                        } catch (e: Exception) {
                            LibraryResult.ofError(LibraryResult.RESULT_ERROR_NOT_SUPPORTED)
                        }
                    }
                    "genres" -> serviceScope.future {
                        try {
                            val tags = repository.getTags(limit = 30)
                            val items = tags.map { tag ->
                                createBrowsableItem("genre_${tag.name}", tag.name)
                            }
                            LibraryResult.ofItemList(ImmutableList.copyOf(items), params)
                        } catch (e: Exception) {
                            LibraryResult.ofError(LibraryResult.RESULT_ERROR_NOT_SUPPORTED)
                        }
                    }
                    "favourites" -> serviceScope.future {
                        try {
                            val prefs = getSharedPreferences("pure_radio_prefs", MODE_PRIVATE)
                            val json = prefs.getString("favorite_stations_json", null)
                            val items = if (json != null) {
                                val stations = try {
                                    com.google.gson.Gson().fromJson<List<Station>>(json, object : com.google.gson.reflect.TypeToken<List<Station>>() {}.type)
                                } catch (e: Exception) { emptyList() }
                                stations.map { createPlayableItem(it) }
                            } else emptyList()
                            LibraryResult.ofItemList(ImmutableList.copyOf(items), params)
                        } catch (e: Exception) {
                            LibraryResult.ofError(LibraryResult.RESULT_ERROR_NOT_SUPPORTED)
                        }
                    }
                    else -> if (parentId.startsWith("genre_")) serviceScope.future {
                        try {
                            val genre = parentId.removePrefix("genre_")
                            val stations = repository.searchStations(tag = genre, limit = 50)
                            val items = stations.map { createPlayableItem(it) }
                            LibraryResult.ofItemList(ImmutableList.copyOf(items), params)
                        } catch (e: Exception) {
                            LibraryResult.ofError(LibraryResult.RESULT_ERROR_NOT_SUPPORTED)
                        }
                    } else {
                        serviceScope.future {
                            try {
                                val station = repository.getStation(parentId)
                                if (station != null) {
                                    LibraryResult.ofItemList(ImmutableList.of(createPlayableItem(station)), params)
                                } else {
                                    LibraryResult.ofItemList(ImmutableList.of<MediaItem>(), params)
                                }
                            } catch (e: Exception) {
                                LibraryResult.ofError(LibraryResult.RESULT_ERROR_NOT_SUPPORTED)
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
                val cachedStation = stationCache[mediaId]
                if (cachedStation != null) {
                    return Futures.immediateFuture(LibraryResult.ofItem(createPlayableItem(cachedStation), null))
                }
                return serviceScope.future {
                    try {
                        val station = repository.getStation(mediaId)
                        if (station != null) {
                            stationCache.put(mediaId, station)
                            LibraryResult.ofItem(createPlayableItem(station), null)
                        } else {
                            val item = when (mediaId) {
                                "root" -> MediaItem.Builder()
                                    .setMediaId("root")
                                    .setMediaMetadata(MediaMetadata.Builder()
                                        .setIsBrowsable(true)
                                        .setIsPlayable(false)
                                        .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_MIXED)
                                        .setTitle("Pure Radio")
                                        .build())
                                    .build()
                                "popular" -> createBrowsableItem("popular", "Popular Stations")
                                "favourites" -> createBrowsableItem("favourites", "Favourites")
                                "genres" -> createBrowsableItem("genres", "Genres")
                                else -> null
                            }
                            if (item != null) {
                                LibraryResult.ofItem(item, null)
                            } else {
                                LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
                            }
                        }
                    } catch (e: Exception) {
                        LibraryResult.ofError(LibraryResult.RESULT_ERROR_NOT_SUPPORTED)
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
                        val stations = repository.searchStations(query = query, limit = safePageSize, offset = safePage * safePageSize)
                        val items = stations.map { createPlayableItem(it) }
                        LibraryResult.ofItemList(ImmutableList.copyOf(items), params)
                    } catch (e: Exception) {
                        LibraryResult.ofError(LibraryResult.RESULT_ERROR_NOT_SUPPORTED)
                    }
                }
            }
        }).build()
    }

    private fun createBrowsableItem(id: String, title: String): MediaItem {
        return MediaItem.Builder()
            .setMediaId(id)
            .setMediaMetadata(MediaMetadata.Builder()
                .setIsBrowsable(true)
                .setIsPlayable(false)
                .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_MIXED)
                .setTitle(title)
                .build())
            .build()
    }

    private fun cacheStation(station: Station) {
        if (stationCache.size >= 500) {
            stationCache.keys.take(100).forEach { stationCache.remove(it) }
        }
        stationCache[station.stationUuid] = station
    }

    private fun createPlayableItem(station: Station, isHls: Boolean = false): MediaItem {
        cacheStation(station)
        val artworkUri = if (station.favicon.isNotEmpty()) {
            android.net.Uri.parse(station.favicon)
        } else {
            android.net.Uri.Builder()
                .scheme(android.content.ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(packageName ?: "com.toxa.pureradio")
                .appendPath(com.toxa.pureradio.R.drawable.ic_radio_logo.toString())
                .build()
        }
        val isHlsStream = isHls
                || station.url.lowercase().let { it.endsWith(".m3u8") || it.endsWith(".m3u") }
                || (station.codec?.contains("hls", ignoreCase = true) == true)
        val builder = MediaItem.Builder()
            .setMediaId(station.stationUuid)
            .setUri(station.url)
            .setMediaMetadata(MediaMetadata.Builder()
                .setIsBrowsable(false)
                .setIsPlayable(true)
                .setMediaType(MediaMetadata.MEDIA_TYPE_RADIO_STATION)
                .setTitle(station.name)
                .setArtist(station.tags)
                .setArtworkUri(artworkUri)
                .build())
        if (isHlsStream) {
            builder.setMimeType(MimeTypes.APPLICATION_M3U8)
        }
        return builder.build()
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
        mediaLibrarySession?.run {
            release()
            mediaLibrarySession = null
        }
        serviceJob.cancel()
        super.onDestroy()
    }
}
