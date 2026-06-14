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
import kotlinx.coroutines.launch
import kotlinx.coroutines.guava.future

enum class PlayerAction { Next, Previous }
<<<<<<< HEAD
const val CMD_PLAY_STATION = "play_station"
=======
>>>>>>> 1162dbf (Restore project)

class PlaybackService : MediaLibraryService() {

    companion object {
        private val _playerAction = MutableSharedFlow<PlayerAction>(replay = 1, extraBufferCapacity = 1)
        val playerAction: SharedFlow<PlayerAction> = _playerAction.asSharedFlow()
        fun sendPlayerAction(action: PlayerAction) { _playerAction.tryEmit(action) }
<<<<<<< HEAD
        private const val CMD_PLAY_STATION = "play_station"
=======
        const val CMD_PLAY_STATION = "play_station"
>>>>>>> 1162dbf (Restore project)
    }

    private var mediaLibrarySession: MediaLibrarySession? = null
    private val repository = RadioRepository()
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
            .setDefaultRequestProperties(mapOf("Icy-MetaData" to "1"))
        
        val dataSourceFactory = DefaultDataSource.Factory(this, httpDataSourceFactory)
        
        val mediaSourceFactory = DefaultMediaSourceFactory(this)
            .setDataSourceFactory(dataSourceFactory)

        val prefs = getSharedPreferences("pure_radio_prefs", MODE_PRIVATE)
        val audioPassthrough = prefs.getBoolean("audio_passthrough", false)

        val playerBuilder = ExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)

        if (audioPassthrough) {
            val renderersFactory = DefaultRenderersFactory(this)
                .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
                .setEnableAudioFloatOutput(true)
            playerBuilder.setRenderersFactory(renderersFactory)
        }

        val player = InterceptingPlayer(playerBuilder.build())

        mediaLibrarySession = MediaLibrarySession.Builder(this, player, object : MediaLibrarySession.Callback {
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
                        val stations = repository.getTopStations(limit = 20)
                        val items = stations.map { createPlayableItem(it) }
                        LibraryResult.ofItemList(ImmutableList.copyOf(items), params)
                    }
                    "genres" -> serviceScope.future {
                        val tags = repository.getTags(limit = 30)
                        val items = tags.map { tag ->
                            createBrowsableItem("genre_${tag.name}", tag.name)
                        }
                        LibraryResult.ofItemList(ImmutableList.copyOf(items), params)
                    }
                    "favourites" -> serviceScope.future {
                        val prefs = getSharedPreferences("pure_radio_prefs", MODE_PRIVATE)
                        val json = prefs.getString("favorite_stations_json", null)
                        val items = if (json != null) {
                            val stations = com.google.gson.Gson().fromJson<List<Station>>(json, object : com.google.gson.reflect.TypeToken<List<Station>>() {}.type)
                            stations.map { createPlayableItem(it) }
                        } else emptyList()
                        LibraryResult.ofItemList(ImmutableList.copyOf(items), params)
                    }
                    else -> if (parentId.startsWith("genre_")) serviceScope.future {
                        val genre = parentId.removePrefix("genre_")
                        val stations = repository.searchStations(tag = genre, limit = 50)
                        val items = stations.map { createPlayableItem(it) }
                        LibraryResult.ofItemList(ImmutableList.copyOf(items), params)
                    } else {
                        Futures.immediateFuture(LibraryResult.ofItemList(ImmutableList.of<MediaItem>(), params))
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
                .setTitle(title)
                .build())
            .build()
    }

    private fun createPlayableItem(station: Station, isHls: Boolean = false): MediaItem {
        val artworkUri = if (station.favicon.isNotEmpty()) {
            android.net.Uri.parse(station.favicon)
        } else {
            android.net.Uri.parse("android.resource://${getPackageName()}/${com.toxa.pureradio.R.drawable.ic_radio_logo}")
        }
        val builder = MediaItem.Builder()
            .setMediaId(station.stationUuid)
            .setUri(station.url)
            .setMediaMetadata(MediaMetadata.Builder()
                .setIsBrowsable(false)
                .setIsPlayable(true)
                .setTitle(station.name)
                .setArtist(station.tags)
                .setArtworkUri(artworkUri)
                .build())
        if (isHls) {
            builder.setMimeType(MimeTypes.APPLICATION_M3U8)
        }
        return builder.build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaLibrarySession
    }

    override fun onTaskRemoved(rootIntent: android.content.Intent?) {
        val player = mediaLibrarySession?.player
        if (player?.playWhenReady == true && player.mediaItemCount > 0) {
            return
        }
        stopSelf()
    }

    override fun onDestroy() {
        serviceJob.cancel()
        mediaLibrarySession?.run {
            player.release()
            release()
            mediaLibrarySession = null
        }
        super.onDestroy()
    }
}
