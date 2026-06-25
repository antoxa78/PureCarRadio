package com.toxa.pureradio.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.ComponentName
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import java.io.File
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.C
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.toxa.pureradio.PlaybackService
import com.toxa.pureradio.R
import com.toxa.pureradio.data.model.Station
import com.toxa.pureradio.data.repository.RadioRepository
import com.toxa.pureradio.network.Country
import com.toxa.pureradio.network.Tag
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.toxa.pureradio.PlayerAction
import java.util.concurrent.ConcurrentHashMap

enum class NavigationItem(val labelRes: Int) {
    Home(R.string.nav_home),
    Popular(R.string.nav_popular),
    Recent(R.string.nav_recent),
    Search(R.string.nav_search),
    Genres(R.string.nav_genres),
    Countries(R.string.nav_countries),
    Favourites(R.string.nav_favourites),
    Settings(R.string.nav_settings),
    Exit(R.string.nav_exit)
}

enum class AppLanguage(val code: String, val displayName: String) {
    English("en", "English"),
    Russian("ru", "Русский"),
    Ukrainian("uk", "Українська"),
    Hebrew("iw", "עברית")
}

enum class BitrateFilter {
    Low, High, FLAC
}

data class GenreGroup(
    val genreName: String,
    val stations: List<Station>,
    val totalStations: Int = 0,
    val filteredCount: Int = 0,
    val isCountry: Boolean = false
)

enum class ScreensaverMode {
    StationInfo, BlackScreen
}

enum class GenreSortMode {
    Name, Count
}

enum class SearchMode {
    Name, Tag
}

enum class AppTheme {
    RetroGold, BlueNeon, Violet, Monochrome, Forest, Contrast, Black
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RadioRepository()
    private var player: Player? = null
    private val prefs = application.getSharedPreferences("pure_radio_prefs", Context.MODE_PRIVATE)
    private val faviconCache = ConcurrentHashMap<String, ByteArray>()

    /** Convenience helper to access localized strings from the ViewModel. */
    private fun str(resId: Int, vararg args: Any): String =
        getApplication<Application>().getString(resId, *args)

    private val _allStations = MutableStateFlow<List<Station>>(emptyList())
    private val _stations = MutableStateFlow<List<Station>>(emptyList())
    val stations: StateFlow<List<Station>> = _stations

    private val _genreGroups = MutableStateFlow<List<GenreGroup>>(emptyList())
    val genreGroups: StateFlow<List<GenreGroup>> = _genreGroups

    private val _selectedBitrates = MutableStateFlow<Set<BitrateFilter>>(loadBitrateFilters())
    val selectedBitrates: StateFlow<Set<BitrateFilter>> = _selectedBitrates

    private val _currentStation = MutableStateFlow<Station?>(null)
    val currentStation: StateFlow<Station?> = _currentStation

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _playbackTime = MutableStateFlow(0L)
    val playbackTime: StateFlow<Long> = _playbackTime

    private val _playbackDuration = MutableStateFlow(0L)
    val playbackDuration: StateFlow<Long> = _playbackDuration

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val defaultStartupCategory = NavigationItem.entries.firstOrNull {
        it.name == prefs.getString("default_startup_category", NavigationItem.Home.name)
    } ?: NavigationItem.Home
    private val _selectedNavItem = MutableStateFlow(defaultStartupCategory)
    val selectedNavItem: StateFlow<NavigationItem> = _selectedNavItem
    private val _defaultStartupCategory = MutableStateFlow(defaultStartupCategory)
    val defaultStartupCategoryFlow: StateFlow<NavigationItem> = _defaultStartupCategory

    private val _genreSortMode = MutableStateFlow(GenreSortMode.Count)
    val genreSortMode: StateFlow<GenreSortMode> = _genreSortMode

    private val _minTagFilter = MutableStateFlow(prefs.getBoolean("min_tag_filter", true))
    val minTagFilter: StateFlow<Boolean> = _minTagFilter

    private val _tags = MutableStateFlow<List<Tag>>(emptyList())
    val tags: StateFlow<List<Tag>> = combine(_tags, _genreSortMode) { tags, mode ->
        when (mode) {
            GenreSortMode.Name -> tags.sortedBy { it.name.lowercase() }
            GenreSortMode.Count -> tags.sortedByDescending { it.stationcount }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _countries = MutableStateFlow<List<Country>>(emptyList())
    val countries: StateFlow<List<Country>> = _countries

    private val _selectedTag = MutableStateFlow<Tag?>(null)
    val selectedTag: StateFlow<Tag?> = _selectedTag

    private val _selectedCountry = MutableStateFlow<Country?>(null)
    val selectedCountry: StateFlow<Country?> = _selectedCountry

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchMode = MutableStateFlow(SearchMode.Name)
    val searchMode: StateFlow<SearchMode> = _searchMode

    private val _tagSearchGroups = MutableStateFlow<List<GenreGroup>>(emptyList())
    val tagSearchGroups: StateFlow<List<GenreGroup>> = _tagSearchGroups

    private val _selectedSearchTag = MutableStateFlow<String?>(null)
    val selectedSearchTag: StateFlow<String?> = _selectedSearchTag

    private val _recentSearches = MutableStateFlow<List<String>>(loadRecentSearches())
    val recentSearches: StateFlow<List<String>> = _recentSearches

    private val _tagSearchQuery = MutableStateFlow("")
    val tagSearchQuery: StateFlow<String> = _tagSearchQuery

    val filteredTags: StateFlow<List<Tag>> = combine(_tags, _tagSearchQuery, _genreSortMode, _selectedBitrates, _minTagFilter) { tags, query, mode, bitrates, minFilter ->
        val sorted = when (mode) {
            GenreSortMode.Name -> tags.sortedBy { it.name.lowercase() }
            GenreSortMode.Count -> tags.sortedByDescending { it.stationcount }
        }
        val filteredByQuery = if (query.isBlank()) sorted
        else sorted.filter { it.name.contains(query, ignoreCase = true) }
        
        val filteredByMin = if (minFilter) filteredByQuery.filter { it.stationcount >= 101 }
        else filteredByQuery

        filteredByMin
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _favorites = MutableStateFlow<Set<String>>(loadFavoritesFromPrefs())
    val favorites: StateFlow<Set<String>> = _favorites

    private val _favoriteStations = MutableStateFlow<List<Station>>(loadFavoriteStationsFromPrefs())

    private val _recentStations = MutableStateFlow<List<Station>>(loadRecentsFromPrefs())
    val recentStations: StateFlow<List<Station>> = _recentStations

    private val _visibleGenres = MutableStateFlow<Set<String>>(loadVisibleGenres())
    val visibleGenres: StateFlow<Set<String>> = _visibleGenres

    private val _visibleCountries = MutableStateFlow<Set<String>>(loadVisibleCountries())
    val visibleCountries: StateFlow<Set<String>> = _visibleCountries

    private val _hideBrokenStations = MutableStateFlow(loadHideBroken())
    val hideBrokenStations: StateFlow<Boolean> = _hideBrokenStations

    private val _stationOffset = MutableStateFlow(0)
    private val _hasMoreStations = MutableStateFlow(true)
    val hasMoreStations: StateFlow<Boolean> = _hasMoreStations

    private val _settingsSubMenu = MutableStateFlow<String?>(null)
    val settingsSubMenu: StateFlow<String?> = _settingsSubMenu

    private val _serverStats = MutableStateFlow<com.toxa.pureradio.network.ServerStats?>(null)
    val serverStats: StateFlow<com.toxa.pureradio.network.ServerStats?> = _serverStats

    private val _lastDbUpdate = MutableStateFlow<Long>(prefs.getLong("last_db_update", 0))
    val lastDbUpdate: StateFlow<Long> = _lastDbUpdate

    private val _autoUpdateInterval = MutableStateFlow(prefs.getInt("auto_update_interval", 24))
    val autoUpdateInterval: StateFlow<Int> = _autoUpdateInterval

    private val _screensaverEnabled = MutableStateFlow(prefs.getBoolean("screensaver_enabled", true))
    val screensaverEnabled: StateFlow<Boolean> = _screensaverEnabled

    private val _screensaverTimeout = MutableStateFlow(prefs.getInt("screensaver_timeout", 1))
    val screensaverTimeout: StateFlow<Int> = _screensaverTimeout

    private val _screensaverMode = MutableStateFlow(prefs.getString("screensaver_mode", ScreensaverMode.StationInfo.name)?.let {
        try { ScreensaverMode.valueOf(it) } catch (e: Exception) { ScreensaverMode.StationInfo }
    } ?: ScreensaverMode.StationInfo)
    val screensaverMode: StateFlow<ScreensaverMode> = _screensaverMode

    private val _showGenreDialog = MutableStateFlow<String?>(null)
    val showGenreDialog: StateFlow<String?> = _showGenreDialog

    private val _isScreensaverShowing = MutableStateFlow(false)
    val isScreensaverShowing: StateFlow<Boolean> = _isScreensaverShowing

    private val _pendingImportStations = MutableStateFlow<List<Station>?>(null)
    val pendingImportStations: StateFlow<List<Station>?> = _pendingImportStations

    private val _filePickerState = MutableStateFlow<FilePickerState?>(null)
    val filePickerState: StateFlow<FilePickerState?> = _filePickerState

    private val _mediaMetadata = MutableStateFlow<androidx.media3.common.MediaMetadata?>(null)
    val mediaMetadata: StateFlow<androidx.media3.common.MediaMetadata?> = _mediaMetadata

    private val _audioFormat = MutableStateFlow<androidx.media3.common.Format?>(null)
    val audioFormat: StateFlow<androidx.media3.common.Format?> = _audioFormat

    private val _audioPassthrough = MutableStateFlow(prefs.getBoolean("audio_passthrough", false))
    val audioPassthrough: StateFlow<Boolean> = _audioPassthrough

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized

    private val _appTheme = MutableStateFlow(loadAppTheme())
    val appTheme: StateFlow<AppTheme> = _appTheme

    private val _appLanguage = MutableStateFlow(loadAppLanguage())
    val appLanguage: StateFlow<AppLanguage> = _appLanguage
    private val _languageChangeEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val languageChangeEvent: SharedFlow<Unit> = _languageChangeEvent

    private val _quitConfirmationEnabled = MutableStateFlow(prefs.getBoolean("quit_confirmation", true))
    val quitConfirmationEnabled: StateFlow<Boolean> = _quitConfirmationEnabled

    private val _resumeLastStation = MutableStateFlow(prefs.getBoolean("resume_last_station", false))
    val resumeLastStation: StateFlow<Boolean> = _resumeLastStation

    private var lastInteractionTime = System.currentTimeMillis()
    private var consecutiveErrors = 0
    private var searchJob: kotlinx.coroutines.Job? = null

    private val _searchFocusTrigger = MutableStateFlow(0)
    val searchFocusTrigger: StateFlow<Int> = _searchFocusTrigger

    init {
        initializePlayer()
        loadTags()
        loadStats()
        selectNavigationItem(defaultStartupCategory, force = true)
        startPlaybackTimer()
        startScreensaverTimer()
        checkAutoUpdate()
        refreshFavoriteStations()
        refreshRecentStations()
        viewModelScope.launch {
            delay(500)
            _isInitialized.value = true
        }
        viewModelScope.launch {
            delay(1500)
            if (_resumeLastStation.value && _currentStation.value == null) {
                val uuid = prefs.getString("last_station_uuid", null)
                if (uuid != null) {
                    val station = _allStations.value.find { it.stationUuid == uuid }
                        ?: _favoriteStations.value.find { it.stationUuid == uuid }
                        ?: _recentStations.value.find { it.stationUuid == uuid }
                    if (station != null) {
                        playStation(station)
                    }
                }
            }
        }
        viewModelScope.launch {
            PlaybackService.playerAction.collect { action ->
                when (action) {
                    PlayerAction.Next -> playNext()
                    PlayerAction.Previous -> playPrevious()
                }
            }
        }
    }

    @OptIn(UnstableApi::class)
    private fun initializePlayer(retryCount: Int = 0) {
        val context = getApplication<Application>()
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val future = MediaController.Builder(context, sessionToken).buildAsync()

        future.addListener({
            try {
                val controller = future.get()
                player = controller
                controllerFuture = null
                controller.addListener(object : Player.Listener {
                    override fun onPlayerError(error: PlaybackException) {
                        consecutiveErrors++
                        if (consecutiveErrors <= 3) {
                            viewModelScope.launch {
                                delay(2000L * consecutiveErrors)
                                retryCurrentStation()
                            }
                        } else if (consecutiveErrors <= 8) {
                            playNext(isAuto = true)
                        } else {
                            _error.value = str(R.string.error_playback_failed, error.message ?: "unknown")
                            stopPlayback()
                            consecutiveErrors = 0
                        }
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _isPlaying.value = isPlaying
                        if (isPlaying) {
                            consecutiveErrors = 0
                        }
                    }

                    override fun onMediaMetadataChanged(mediaMetadata: androidx.media3.common.MediaMetadata) {
                        _mediaMetadata.value = mediaMetadata
                    }

                    override fun onTracksChanged(tracks: androidx.media3.common.Tracks) {
                        for (group in tracks.groups) {
                            if (group.type == C.TRACK_TYPE_AUDIO && group.isSelected) {
                                for (i in 0 until group.length) {
                                    if (group.isTrackSelected(i)) {
                                        _audioFormat.value = group.getTrackFormat(i)
                                        break
                                    }
                                }
                            }
                        }
                    }
                })

                // Sync initial state
                _isPlaying.value = controller.isPlaying
                _mediaMetadata.value = controller.mediaMetadata
                _currentStation.value = _allStations.value.find { it.stationUuid == controller.currentMediaItem?.mediaId }
                    ?: _favoriteStations.value.find { it.stationUuid == controller.currentMediaItem?.mediaId }
                    ?: _recentStations.value.find { it.stationUuid == controller.currentMediaItem?.mediaId }
            } catch (e: Exception) {
                if (retryCount < 3) {
                    controllerFuture = null
                    viewModelScope.launch {
                        delay(2000L * (retryCount + 1))
                        initializePlayer(retryCount + 1)
                    }
                } else {
                    controllerFuture = null
                    _error.value = str(R.string.error_playback_service)
                }
            }
        }, com.google.common.util.concurrent.MoreExecutors.directExecutor())
        controllerFuture = future
    }

    private var controllerFuture: com.google.common.util.concurrent.ListenableFuture<*>? = null

    private fun reinitializePlayer() {
        player?.release()
        player = null
        controllerFuture?.cancel(true)
        controllerFuture = null

        val context = getApplication<Application>()
        val intent = android.content.Intent(context, PlaybackService::class.java)
        context.stopService(intent)
        viewModelScope.launch {
            delay(300)
            initializePlayer()
        }
    }

    fun toggleAudioPassthrough() {
        val newValue = !_audioPassthrough.value
        _audioPassthrough.value = newValue
        prefs.edit().putBoolean("audio_passthrough", newValue).apply()
        reinitializePlayer()
    }

    private fun startScreensaverTimer() {
        viewModelScope.launch {
            while (true) {
                if (_screensaverEnabled.value && _isPlaying.value && !_isScreensaverShowing.value) {
                    val idleTime = System.currentTimeMillis() - lastInteractionTime
                    if (idleTime > _screensaverTimeout.value * 60 * 1000L) {
                        _isScreensaverShowing.value = true
                    }
                }
                kotlinx.coroutines.delay(5000)
            }
        }
    }

    fun resetScreensaverTimer() {
        lastInteractionTime = System.currentTimeMillis()
        if (_isScreensaverShowing.value) {
            _isScreensaverShowing.value = false
        }
    }

    private fun checkAutoUpdate() {
        val intervalHours = _autoUpdateInterval.value
        if (intervalHours > 0) {
            val lastUpdate = _lastDbUpdate.value
            val now = System.currentTimeMillis()
            val intervalMillis = intervalHours * 60 * 60 * 1000L
            if (now - lastUpdate > intervalMillis) {
                updateDatabase()
            }
        }
    }

    fun setAutoUpdateInterval(hours: Int) {
        _autoUpdateInterval.value = hours
        prefs.edit().putInt("auto_update_interval", hours).apply()
        if (hours > 0) checkAutoUpdate()
    }

    fun toggleScreensaver(enabled: Boolean) {
        _screensaverEnabled.value = enabled
        prefs.edit().putBoolean("screensaver_enabled", enabled).apply()
    }

    fun setScreensaverTimeout(minutes: Int) {
        _screensaverTimeout.value = minutes
        prefs.edit().putInt("screensaver_timeout", minutes).apply()
    }

    fun setScreensaverMode(mode: ScreensaverMode) {
        _screensaverMode.value = mode
        prefs.edit().putString("screensaver_mode", mode.name).apply()
    }

    data class FilePickerState(
        val currentPath: String,
        val files: List<File>,
        val isExport: Boolean,
        val suggestedFileName: String = ""
    )

    fun openFilePicker(isExport: Boolean, suggestedFileName: String = "") {
        val root = Environment.getExternalStorageDirectory() ?: File("/")
        // If we are in the root, also try to find 'Download' folder which is most common on TV
        val initialDir = if (!isExport) {
            val downloadDir = File(root, "Download")
            if (downloadDir.exists() && downloadDir.isDirectory) downloadDir else root
        } else {
            root
        }
        
        _filePickerState.value = FilePickerState(
            currentPath = initialDir.absolutePath,
            files = listFiles(initialDir),
            isExport = isExport,
            suggestedFileName = suggestedFileName
        )
    }

    fun closeFilePicker() {
        _filePickerState.value = null
    }

    fun navigateInFilePicker(directory: File) {
        val currentState = _filePickerState.value ?: return
        _filePickerState.value = currentState.copy(
            currentPath = directory.absolutePath,
            files = listFiles(directory)
        )
    }

    fun navigateUpFilePicker() {
        val currentState = _filePickerState.value ?: return
        val currentFile = File(currentState.currentPath)
        val parent = currentFile.parentFile ?: return
        _filePickerState.value = currentState.copy(
            currentPath = parent.absolutePath,
            files = listFiles(parent)
        )
    }

    private fun listFiles(directory: File): List<File> {
        val files = directory.listFiles()
        if (files == null) {
            _error.value = str(R.string.error_permission_denied, directory.name)
            return emptyList()
        }
        return files.toList().sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
    }

    fun handleFileSelection(file: File) {
        val currentState = _filePickerState.value ?: return
        if (currentState.isExport) {
            val targetFile = if (file.isDirectory) {
                File(file, currentState.suggestedFileName)
            } else {
                file
            }
            saveFavoritesToFile(targetFile)
            closeFilePicker()
        } else {
            if (!file.isDirectory) {
                prepareImportFavoritesFromFile(file)
            }
        }
    }

    private fun prepareImportFavoritesFromFile(file: File) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val lines = withContext(Dispatchers.IO) { file.readLines() }
                val importedStations = parsePlaylistLines(lines)
                
                if (importedStations.isNotEmpty()) {
                    _pendingImportStations.value = importedStations
                    closeFilePicker()
                } else {
                    _error.value = str(R.string.error_no_stations_in_file)
                    closeFilePicker()
                }
            } catch (e: Exception) {
                _error.value = str(R.string.error_import_failed, e.message ?: "unknown")
                closeFilePicker()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun confirmRestore(replace: Boolean) {
        val stations = _pendingImportStations.value ?: return
        if (replace) {
            _favorites.value = emptySet()
            _favoriteStations.value = emptyList()
        }
        mergeImportedStations(stations)
        _pendingImportStations.value = null
    }

    fun cancelRestore() {
        _pendingImportStations.value = null
    }

    private fun saveFavoritesToFile(file: File) {
        viewModelScope.launch {
            try {
                val content = generateM3uContent()
                withContext(Dispatchers.IO) { file.writeText(content) }
                _error.value = str(R.string.status_favorites_exported_to, file.name)
            } catch (e: Exception) {
                _error.value = str(R.string.error_export_failed, e.message ?: "unknown")
            }
        }
    }

    private fun generateM3uContent(): String {
        val stations = _favoriteStations.value
        val content = StringBuilder("#EXTM3U\n")
        stations.forEach { station ->
            content.append("#EXTINF:-1 tvg-id=\"${station.stationUuid}\" tvg-logo=\"${station.favicon}\" group-title=\"${station.tags}\" tvg-country=\"${station.countryCode ?: ""}\",${station.name}\n")
            // Custom tag for full station metadata recovery (values are escaped)
            content.append("#EXT-X-PURE-RADIO-DATA:uuid=${escapeM3uValue(station.stationUuid)};name=${escapeM3uValue(station.name)};favicon=${escapeM3uValue(station.favicon)};tags=${escapeM3uValue(station.tags)};country=${escapeM3uValue(station.country)};countrycode=${escapeM3uValue(station.countryCode ?: "")};lang=${escapeM3uValue(station.language)};votes=${station.votes};codec=${escapeM3uValue(station.codec ?: "")};bitrate=${station.bitrate}\n")
            content.append("${station.url}\n")
        }
        return content.toString()
    }

    private fun escapeM3uValue(value: String): String {
        return value
            .replace("\\", "\\\\")
            .replace(";", "\\;")
            .replace("=", "\\=")
    }

    private fun splitM3uData(data: String, delimiter: Char): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var i = 0
        while (i < data.length) {
            val c = data[i]
            if (c == '\\' && i + 1 < data.length) {
                current.append(c)
                current.append(data[i + 1])
                i += 2
            } else if (c == delimiter) {
                result.add(current.toString())
                current.clear()
                i += 1
            } else {
                current.append(c)
                i += 1
            }
        }
        result.add(current.toString())
        return result
    }

    private fun unescapeM3uValue(value: String): String {
        return value
            .replace("\\;", ";")
            .replace("\\=", "=")
            .replace("\\\\", "\\")
    }

    private fun parsePlaylistLines(lines: List<String>): List<Station> {
        if (lines.isEmpty()) return emptyList()
        
        val firstLine = lines.firstOrNull { it.isNotBlank() }?.trim()?.removePrefix("\uFEFF") ?: return emptyList()
        
        return when {
            firstLine.startsWith("#EXTM3U", ignoreCase = true) -> parseM3uLines(lines)
            firstLine.startsWith("[playlist]", ignoreCase = true) -> parsePlsLines(lines)
            else -> parseM3uLines(lines)
        }
    }

    private fun parsePlsLines(lines: List<String>): List<Station> {
        val importedStations = mutableListOf<Station>()
        val entries = mutableMapOf<Int, MutableMap<String, String>>()
        
        for (line in lines) {
            val trimmedLine = line.trim()
            if (trimmedLine.isEmpty() || trimmedLine.startsWith("[") || trimmedLine.startsWith("#")) continue
            
            val parts = trimmedLine.split("=", limit = 2)
            if (parts.size < 2) continue
            
            val key = parts[0].trim()
            val value = parts[1].trim()
            
            val keyName = key.filter { it.isLetter() }.lowercase()
            val index = key.filter { it.isDigit() }.toIntOrNull() ?: continue
            
            val entry = entries.getOrPut(index) { mutableMapOf() }
            entry[keyName] = value
        }
        
        entries.keys.sorted().forEach { index ->
            val entry = entries[index] ?: return@forEach
            val url = entry["file"] ?: entry["url"] ?: return@forEach
            val name = entry["title"] ?: url.substringAfterLast("/").substringBefore("?")
            
            importedStations.add(Station(
                stationUuid = java.util.UUID.randomUUID().toString(),
                name = name,
                url = url,
                favicon = "",
                tags = "",
                country = "",
                countryCode = null,
                language = "",
                votes = 0,
                codec = "",
                bitrate = 0
            ))
        }
        
        return importedStations
    }

    private fun parseM3uLines(lines: List<String>): List<Station> {
        val importedStations = mutableListOf<Station>()
        var currentName = ""
        var currentUuid = ""
        var currentFavicon = ""
        var currentTags = ""
        var currentCountry = ""
        var currentCountryCode = ""
        var currentLanguage = ""
        var currentVotes = 0
        var currentCodec = ""
        var currentBitrate = 0
        
        for (line in lines) {
            val trimmedLine = line.trim()
            if (trimmedLine.isEmpty()) continue
            
            when {
                trimmedLine.startsWith("#EXT-X-PURE-RADIO-DATA:") -> {
                    val rawData = trimmedLine.removePrefix("#EXT-X-PURE-RADIO-DATA:")
                    val pairs = splitM3uData(rawData, ';')
                    val map = mutableMapOf<String, String>()
                    for (pair in pairs) {
                        val parts = splitM3uData(pair, '=')
                        if (parts.size == 2) {
                            map[parts[0]] = unescapeM3uValue(parts[1])
                        }
                    }
                    if (map.containsKey("uuid")) currentUuid = map["uuid"] ?: ""
                    if (map.containsKey("name")) currentName = map["name"] ?: ""
                    if (map.containsKey("favicon")) currentFavicon = map["favicon"] ?: ""
                    if (map.containsKey("tags")) currentTags = map["tags"] ?: ""
                    if (map.containsKey("country")) currentCountry = map["country"] ?: ""
                    if (map.containsKey("countrycode")) currentCountryCode = map["countrycode"] ?: ""
                    if (map.containsKey("lang")) currentLanguage = map["lang"] ?: ""
                    if (map.containsKey("votes")) currentVotes = map["votes"]?.toIntOrNull() ?: 0
                    if (map.containsKey("codec")) currentCodec = map["codec"] ?: ""
                    if (map.containsKey("bitrate")) currentBitrate = map["bitrate"]?.toIntOrNull() ?: 0
                }
                trimmedLine.startsWith("#EXTINF:") -> {
                    val logoRegex = "tvg-logo=\"([^\"]*)\"".toRegex()
                    val idRegex = "tvg-id=\"([^\"]*)\"".toRegex()
                    val groupRegex = "group-title=\"([^\"]*)\"".toRegex()
                    
                    logoRegex.find(trimmedLine)?.let { currentFavicon = it.groupValues[1] }
                    idRegex.find(trimmedLine)?.let { currentUuid = it.groupValues[1] }
                    groupRegex.find(trimmedLine)?.let { currentTags = it.groupValues[1] }
                    
                    val namePart = trimmedLine.split(",", limit = 2).lastOrNull() ?: ""
                    if (namePart.isNotBlank()) currentName = namePart.trim()
                }
                !trimmedLine.startsWith("#") -> {
                    val url = trimmedLine
                    val name = if (currentName.isNotEmpty()) currentName else url.substringAfterLast("/").substringBefore("?")
                    
                    importedStations.add(Station(
                        stationUuid = if (currentUuid.isNotEmpty()) currentUuid else java.util.UUID.randomUUID().toString(),
                        name = name,
                        url = url,
                        favicon = currentFavicon,
                        tags = currentTags,
                        country = currentCountry,
                        countryCode = if (currentCountryCode.isNotEmpty()) currentCountryCode else null,
                        language = currentLanguage,
                        votes = currentVotes,
                        codec = currentCodec,
                        bitrate = currentBitrate
                    ))
                    
                    currentName = ""; currentUuid = ""; currentFavicon = ""; currentTags = ""
                    currentCountry = ""; currentCountryCode = ""; currentLanguage = ""
                    currentVotes = 0; currentCodec = ""; currentBitrate = 0
                }
            }
        }
        return importedStations
    }

    private fun mergeImportedStations(importedStations: List<Station>) {
        val currentFavorites = _favorites.value.toMutableSet()
        val currentStationList = _favoriteStations.value.toMutableList()
        
        importedStations.forEach { station ->
            if (!currentFavorites.contains(station.stationUuid)) {
                currentFavorites.add(station.stationUuid)
                currentStationList.add(station)
            }
        }
        
        _favorites.value = currentFavorites
        _favoriteStations.value = currentStationList
        saveFavoritesToPrefs(currentFavorites, currentStationList)
        if (_selectedNavItem.value == NavigationItem.Favourites) {
            _stations.value = currentStationList
        }
        refreshFavoriteStations()
    }

    private fun startPlaybackTimer() {
        viewModelScope.launch {
            while (true) {
                if (_isPlaying.value) {
                    _playbackTime.value = player?.currentPosition ?: 0L
                    val duration = player?.duration ?: 0L
                    val isLive = player?.isCurrentMediaItemLive ?: false
                    _playbackDuration.value = if (duration > 0 && !isLive) duration else 0L
                }
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    private fun loadStats() {
        viewModelScope.launch {
            _serverStats.value = repository.getStats()
        }
    }

    fun updateDatabase() {
        viewModelScope.launch {
            if (_isLoading.value) return@launch
            _isLoading.value = true
            try {
                val stats = repository.getStats()
                if (stats != null) {
                    _serverStats.value = stats
                    _lastDbUpdate.value = System.currentTimeMillis()
                    prefs.edit().putLong("last_db_update", _lastDbUpdate.value).apply()
                }
                _tags.value = repository.getTags(limit = 500)
                _countries.value = repository.getCountries()
            } catch (e: Exception) {
                _error.value = str(R.string.error_database_update)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadFavoritesFromPrefs(): Set<String> {
        return prefs.getStringSet("favorites", emptySet()) ?: emptySet()
    }

    private fun loadRecentSearches(): List<String> {
        val json = prefs.getString("recent_searches_json", null) ?: return emptyList()
        return try {
            com.google.gson.Gson().fromJson(json, object : com.google.gson.reflect.TypeToken<List<String>>() {}.type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveRecentSearches(searches: List<String>) {
        val json = com.google.gson.Gson().toJson(searches)
        prefs.edit().putString("recent_searches_json", json).apply()
    }

    fun addToRecentSearches(query: String) {
        if (query.length < 3) return
        val current = _recentSearches.value.toMutableList()
        
        current.removeAll { it.equals(query, ignoreCase = true) }
        current.add(0, query)
        
        if (current.size > 12) current.removeAt(current.size - 1)
        _recentSearches.value = current
        saveRecentSearches(current)
    }

    fun clearRecentSearches() {
        _recentSearches.value = emptyList()
        saveRecentSearches(emptyList())
    }

    private fun loadFavoriteStationsFromPrefs(): List<Station> {
        val json = prefs.getString("favorite_stations_json", null) ?: return emptyList()
        return try {
            com.google.gson.Gson().fromJson(json, object : com.google.gson.reflect.TypeToken<List<Station>>() {}.type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveFavoritesToPrefs(favorites: Set<String>, stations: List<Station>) {
        val json = com.google.gson.Gson().toJson(stations)
        prefs.edit()
            .putStringSet("favorites", favorites)
            .putString("favorite_stations_json", json)
            .apply()
    }

    private fun loadVisibleGenres(): Set<String> {
        return prefs.getStringSet("visible_genres", emptySet()) ?: emptySet()
    }

    private fun saveVisibleGenres(genres: Set<String>) {
        prefs.edit().putStringSet("visible_genres", genres).apply()
    }

    private fun loadVisibleCountries(): Set<String> {
        return prefs.getStringSet("visible_countries", emptySet()) ?: emptySet()
    }

    private fun saveVisibleCountries(countries: Set<String>) {
        prefs.edit().putStringSet("visible_countries", countries).apply()
    }

    private fun loadHideBroken(): Boolean {
        return prefs.getBoolean("hide_broken", true)
    }

    private fun saveHideBroken(hide: Boolean) {
        prefs.edit().putBoolean("hide_broken", hide).apply()
    }

    private fun loadAppTheme(): AppTheme {
        val name = prefs.getString("app_theme", AppTheme.RetroGold.name) ?: AppTheme.RetroGold.name
        return try { AppTheme.valueOf(name) } catch (e: Exception) { AppTheme.RetroGold }
    }

    fun setAppTheme(theme: AppTheme) {
        _appTheme.value = theme
        prefs.edit().putString("app_theme", theme.name).apply()
    }

    private fun loadAppLanguage(): AppLanguage {
        val code = prefs.getString("app_language", AppLanguage.English.code) ?: AppLanguage.English.code
        return AppLanguage.entries.firstOrNull { it.code == code } ?: AppLanguage.English
    }

    fun setAppLanguage(language: AppLanguage) {
        _appLanguage.value = language
        prefs.edit().putString("app_language", language.code).apply()
        _languageChangeEvent.tryEmit(Unit)
    }

    fun setQuitConfirmationEnabled(enabled: Boolean) {
        _quitConfirmationEnabled.value = enabled
        prefs.edit().putBoolean("quit_confirmation", enabled).apply()
    }

    fun setResumeLastStation(enabled: Boolean) {
        _resumeLastStation.value = enabled
        prefs.edit().putBoolean("resume_last_station", enabled).apply()
    }

    fun setDefaultStartupCategory(category: NavigationItem) {
        _defaultStartupCategory.value = category
        prefs.edit().putString("default_startup_category", category.name).apply()
    }

    private fun loadRecentsFromPrefs(): List<Station> {
        val json = prefs.getString("recent_stations_json", null) ?: return emptyList()
        return try {
            com.google.gson.Gson().fromJson(json, object : com.google.gson.reflect.TypeToken<List<Station>>() {}.type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveRecentsToPrefs(stations: List<Station>) {
        val json = com.google.gson.Gson().toJson(stations)
        prefs.edit().putString("recent_stations_json", json).apply()
    }

    private fun addToRecent(station: Station) {
        val current = _recentStations.value.toMutableList()
        current.removeAll { it.stationUuid == station.stationUuid }
        current.add(0, station)
        if (current.size > 50) {
            current.removeAt(current.size - 1)
        }
        _recentStations.value = current
        saveRecentsToPrefs(current)
    }

    fun toggleHideBroken() {
        val newValue = !_hideBrokenStations.value
        _hideBrokenStations.value = newValue
        saveHideBroken(newValue)
        refreshCurrentContent()
    }

    fun toggleMinTagFilter() {
        val newValue = !_minTagFilter.value
        _minTagFilter.value = newValue
        prefs.edit().putBoolean("min_tag_filter", newValue).apply()
    }

    fun setSettingsSubMenu(menu: String?) {
        _settingsSubMenu.value = menu
    }

    fun setError(message: String) {
        _error.value = message
    }

    fun clearError() {
        _error.value = null
    }

    fun setGenreSortMode(mode: GenreSortMode) {
        _genreSortMode.value = mode
    }

    fun showGenreDialog(genreName: String) {
        _showGenreDialog.value = genreName
    }

    fun hideGenreDialog() {
        _showGenreDialog.value = null
    }

    fun addToHome(genreName: String) {
        val current = _visibleGenres.value.toMutableSet()
        current.add(genreName)
        _visibleGenres.value = current
        saveVisibleGenres(current)
        _showGenreDialog.value = null
        if (_selectedNavItem.value == NavigationItem.Home) {
            loadTopStations()
        }
    }

    fun removeFromHome(genreName: String) {
        val current = _visibleGenres.value.toMutableSet()
        current.remove(genreName)
        _visibleGenres.value = current
        saveVisibleGenres(current)
        _showGenreDialog.value = null
        if (_selectedNavItem.value == NavigationItem.Home) {
            loadTopStations()
        }
    }

    fun isGenreOnHome(genreName: String): Boolean {
        return _visibleGenres.value.contains(genreName)
    }

    fun toggleGenreVisibility(tagName: String) {
        val current = _visibleGenres.value.toMutableSet()
        if (current.contains(tagName)) {
            current.remove(tagName)
        } else {
            current.add(tagName)
        }
        _visibleGenres.value = current
        saveVisibleGenres(current)
        if (_selectedNavItem.value == NavigationItem.Home) {
            loadTopStations()
        }
    }

    fun toggleCountryVisibility(countryName: String) {
        val current = _visibleCountries.value.toMutableSet()
        if (current.contains(countryName)) {
            current.remove(countryName)
        } else {
            current.add(countryName)
        }
        _visibleCountries.value = current
        saveVisibleCountries(current)
        if (_selectedNavItem.value == NavigationItem.Home) {
            loadTopStations()
        }
    }

    fun setTagSearchQuery(query: String) {
        _tagSearchQuery.value = query
    }

    private fun refreshCurrentContent() {
        when (_selectedNavItem.value) {
            NavigationItem.Home -> loadTopStations()
            NavigationItem.Popular -> loadPopularStations()
            NavigationItem.Genres -> {
                _selectedTag.value?.let { selectTag(it) }
            }
            NavigationItem.Countries -> {
                _selectedCountry.value?.let { selectCountry(it) }
            }
            NavigationItem.Search -> {
                if (_searchQuery.value.length > 2) onSearchQueryChange(_searchQuery.value)
            }
            else -> {}
        }
    }

    fun selectNavigationItem(item: NavigationItem, force: Boolean = false) {
        val isNewItem = _selectedNavItem.value != item
        if (!isNewItem && !force && item != NavigationItem.Search) return
        
        _selectedNavItem.value = item
        _error.value = null
        _selectedTag.value = null
        _selectedCountry.value = null
        _selectedSearchTag.value = null
        _settingsSubMenu.value = null
        
        if (isNewItem) {
            _allStations.value = emptyList()
            _stations.value = emptyList()
            _genreGroups.value = emptyList()
        }

        when (item) {
            NavigationItem.Home -> loadTopStations()
            NavigationItem.Popular -> loadPopularStations()
            NavigationItem.Recent -> {
                _allStations.value = _recentStations.value
                _stations.value = _recentStations.value
                refreshRecentStations()
            }
            NavigationItem.Genres -> loadTags()
            NavigationItem.Countries -> loadCountries()
            NavigationItem.Favourites -> loadFavorites()
            NavigationItem.Search -> {
                _genreGroups.value = emptyList()
                _tagSearchGroups.value = emptyList()
                if (_searchQuery.value.length > 2) {
                    if (_searchMode.value == SearchMode.Tag) {
                        searchStationsByTag(_searchQuery.value)
                    } else {
                        searchStations(_searchQuery.value)
                    }
                } else {
                    _allStations.value = emptyList()
                    _stations.value = emptyList()
                }
                _searchFocusTrigger.value++
            }
            else -> {
                _allStations.value = emptyList()
                _stations.value = emptyList()
                _genreGroups.value = emptyList()
            }
        }
    }

    private fun updateStations(newStations: List<Station>) {
        _allStations.value = newStations
        applyFilters()
        newStations.take(30).forEach { cacheFavicon(it.stationUuid, it.favicon) }
    }

    private fun loadBitrateFilters(): Set<BitrateFilter> {
        val saved = prefs.getStringSet("bitrate_filters", null) ?: return emptySet()
        return saved.mapNotNull { 
            try { BitrateFilter.valueOf(it) } catch (e: Exception) { null }
        }.toSet()
    }

    private fun saveBitrateFilters(filters: Set<BitrateFilter>) {
        prefs.edit().putStringSet("bitrate_filters", filters.map { it.name }.toSet()).apply()
    }

    fun toggleBitrateFilter(filter: BitrateFilter) {
        val current = _selectedBitrates.value.toMutableSet()
        if (current.contains(filter)) {
            current.remove(filter)
        } else {
            current.add(filter)
        }
        _selectedBitrates.value = current
        saveBitrateFilters(current)
        
        refreshCurrentContent()
    }

    private fun applyFilters() {
        val bitrates = _selectedBitrates.value
        _stations.value = _allStations.value.filter { matchesBitrateFilter(it, bitrates) }
        
        _genreGroups.value = _genreGroups.value.map { group ->
            group.copy(filteredCount = group.stations.count { matchesBitrateFilter(it, bitrates) })
        }

        _tagSearchGroups.value = _tagSearchGroups.value.map { group ->
            group.copy(filteredCount = group.stations.count { matchesBitrateFilter(it, bitrates) })
        }

        if (!_isLoading.value && _stations.value.size < 100 && _hasMoreStations.value) {
            loadMoreStations(5)
        }
    }

    private fun matchesBitrateFilter(station: Station, bitrates: Set<BitrateFilter>): Boolean {
        if (bitrates.isEmpty()) return true
        val br = station.bitrate
        val isFlac = station.codec.equals("FLAC", ignoreCase = true) || 
                     station.tags.contains("flac", ignoreCase = true) ||
                     station.name.contains("flac", ignoreCase = true) ||
                     br >= 900
        
        return (bitrates.contains(BitrateFilter.Low) && br < 192 && !isFlac) ||
                (bitrates.contains(BitrateFilter.High) && br >= 192 && !isFlac) ||
                (bitrates.contains(BitrateFilter.FLAC) && isFlac)
    }

    private fun loadPopularStations() {
        viewModelScope.launch {
            _isLoading.value = true
            _stationOffset.value = 0
            _hasMoreStations.value = true
            try {
                val stations = repository.searchStations(limit = 100, offset = 0, hideBroken = _hideBrokenStations.value)
                _hasMoreStations.value = stations.size >= 100
                updateStations(stations)
            } catch (e: Exception) {
                _error.value = str(R.string.error_load_popular)
            } finally {
                _isLoading.value = false
                applyFilters()
            }
        }
    }

    fun loadMoreStations(remainingRetries: Int = 0) {
        viewModelScope.launch {
            if (!_hasMoreStations.value || _isLoading.value) return@launch
            _isLoading.value = true
            try {
                val newOffset = _stationOffset.value + 100
                val hideBroken = _hideBrokenStations.value
                val newStations = when (_selectedNavItem.value) {
                    NavigationItem.Popular -> {
                        repository.searchStations(limit = 100, offset = newOffset, hideBroken = hideBroken)
                    }
                    NavigationItem.Home, NavigationItem.Genres -> {
                        val tag = _selectedTag.value
                        if (tag != null) {
                            repository.searchStations(tag = tag.name, limit = 100, offset = newOffset, hideBroken = hideBroken)
                        } else {
                            repository.searchStations(limit = 100, offset = newOffset, hideBroken = hideBroken)
                        }
                    }
                    NavigationItem.Countries -> {
                        val country = _selectedCountry.value
                        if (country != null) {
                            repository.searchStations(country = country.name, limit = 100, offset = newOffset, hideBroken = hideBroken)
                        } else emptyList()
                    }
                    NavigationItem.Search -> {
                        val query = _searchQuery.value
                        if (query.isNotEmpty()) {
                            if (_searchMode.value == SearchMode.Name) {
                                repository.searchStations(query = query, limit = 100, offset = newOffset, hideBroken = hideBroken)
                            } else {
                                repository.searchStations(tag = query, limit = 100, offset = newOffset, hideBroken = hideBroken)
                            }
                        } else emptyList()
                    }
                    else -> emptyList()
                }
                _hasMoreStations.value = newStations.size >= 100
                _stationOffset.value = newOffset
                _allStations.value = (_allStations.value + newStations).distinctBy { it.stationUuid }
                applyFilters()
            } catch (e: Exception) {
                _error.value = str(R.string.error_load_more)
            } finally {
                _isLoading.value = false
            }

            if (remainingRetries > 0 && _stations.value.size < 100 && _hasMoreStations.value) {
                loadMoreStations(remainingRetries - 1)
            }
        }
    }

    private fun loadTopStations() {
        viewModelScope.launch {
            _isLoading.value = true

            if (_genreGroups.value.isNotEmpty()) {
                applyFilters()
            }

            try {
                val selectedGenres = _visibleGenres.value
                val selectedCountries = _visibleCountries.value
                val hideBroken = _hideBrokenStations.value
                
                if (selectedGenres.isEmpty() && selectedCountries.isEmpty()) {
                    _genreGroups.value = emptyList()
                    _stationOffset.value = 0
                    val topStations = repository.getTopStations(hideBroken = hideBroken)
                    _hasMoreStations.value = topStations.size >= 100
                    if (_selectedBitrates.value.contains(BitrateFilter.FLAC)) {
                        val flacStations = repository.searchStations(tag = "flac", limit = 100, hideBroken = hideBroken)
                        updateStations((topStations + flacStations).distinctBy { it.stationUuid })
                    } else {
                        updateStations(topStations)
                    }
                } else {
                    val bitrates = _selectedBitrates.value
                    val groups = mutableListOf<GenreGroup>()
                    
                    val genreDeferred = selectedGenres.map { genre ->
                        async(Dispatchers.IO) {
                            var offset = 0
                            var allStations = repository.searchStations(tag = genre, limit = 100, offset = offset, hideBroken = hideBroken)
                            var filteredCount = allStations.count { matchesBitrateFilter(it, bitrates) }
                            var retries = 0
                            while (retries < 5 && allStations.size >= 100 && filteredCount < 100) {
                                offset += 100
                                val moreStations = repository.searchStations(tag = genre, limit = 100, offset = offset, hideBroken = hideBroken)
                                if (moreStations.isEmpty()) break
                                allStations = (allStations + moreStations).distinctBy { it.stationUuid }
                                filteredCount = allStations.count { matchesBitrateFilter(it, bitrates) }
                                retries++
                            }
                            val total = _tags.value.find { it.name == genre }?.stationcount ?: allStations.size
                            GenreGroup(genre, allStations, total, filteredCount, isCountry = false)
                        }
                    }
                    
                    val countryDeferred = selectedCountries.map { country ->
                        async(Dispatchers.IO) {
                            var offset = 0
                            var allStations = repository.searchStations(country = country, limit = 100, offset = offset, hideBroken = hideBroken)
                            var filteredCount = allStations.count { matchesBitrateFilter(it, bitrates) }
                            var retries = 0
                            while (retries < 5 && allStations.size >= 100 && filteredCount < 100) {
                                offset += 100
                                val moreStations = repository.searchStations(country = country, limit = 100, offset = offset, hideBroken = hideBroken)
                                if (moreStations.isEmpty()) break
                                allStations = (allStations + moreStations).distinctBy { it.stationUuid }
                                filteredCount = allStations.count { matchesBitrateFilter(it, bitrates) }
                                retries++
                            }
                            val total = _countries.value.find { it.name == country }?.stationcount ?: allStations.size
                            GenreGroup(country, allStations, total, filteredCount, isCountry = true)
                        }
                    }
                    
                    groups.addAll((genreDeferred + countryDeferred).awaitAll())
                    _genreGroups.value = groups
                }
            } catch (e: Exception) {
                _error.value = str(R.string.error_load_stations)
            } finally {
                _isLoading.value = false
                if (_genreGroups.value.isEmpty()) {
                    applyFilters()
                }
            }
        }
    }

    private fun loadTags() {
        viewModelScope.launch {
            if (_tags.value.isEmpty()) {
                _isLoading.value = true
                try {
                    _tags.value = repository.getTags()
                } catch (e: Exception) {
                    _error.value = str(R.string.error_load_genres)
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    private fun loadCountries() {
        viewModelScope.launch {
            if (_countries.value.isEmpty()) {
                _isLoading.value = true
                try {
                    _countries.value = repository.getCountries()
                } catch (e: Exception) {
                    _error.value = str(R.string.error_load_countries)
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    private fun refreshFavoriteStations() {
        val uuids = _favorites.value.joinToString(",")
        if (uuids.isEmpty()) return
        viewModelScope.launch {
            try {
                val updatedStations = repository.getStationsByUuid(uuids)
                if (updatedStations.isNotEmpty()) {
                    val merged = _favoriteStations.value.toMutableList()
                    updatedStations.forEach { updated ->
                        val idx = merged.indexOfFirst { it.stationUuid == updated.stationUuid }
                        if (idx != -1) {
                            merged[idx] = updated
                        } else {
                            merged.add(updated)
                        }
                    }
                    _favoriteStations.value = merged
                    saveFavoritesToPrefs(_favorites.value, merged)
                    if (_selectedNavItem.value == NavigationItem.Favourites) {
                        _stations.value = merged
                    }
                    _currentStation.value?.let { current ->
                        merged.find { it.stationUuid == current.stationUuid }?.let {
                            if (it.countryCode != current.countryCode) {
                                _currentStation.value = it
                            }
                        }
                    }
                }
            } catch (_: Exception) {}
        }
    }

    private fun refreshRecentStations() {
        val uuids = _recentStations.value.map { it.stationUuid }.joinToString(",")
        if (uuids.isEmpty()) return
        viewModelScope.launch {
            try {
                val updatedStations = repository.getStationsByUuid(uuids)
                if (updatedStations.isNotEmpty()) {
                    val currentOrder = _recentStations.value.map { it.stationUuid }
                    val updatedList = currentOrder.mapNotNull { uuid ->
                        updatedStations.find { it.stationUuid == uuid }
                    }
                    _recentStations.value = updatedList
                    saveRecentsToPrefs(updatedList)
                    if (_selectedNavItem.value == NavigationItem.Recent) {
                        _allStations.value = updatedList
                        applyFilters()
                    }
                }
            } catch (_: Exception) {}
        }
    }

    private fun loadFavorites() {
        _stations.value = _favoriteStations.value
        _allStations.value = _favoriteStations.value 
        refreshFavoriteStations()
    }

    fun toggleFavorite(station: Station) {
        val currentFavorites = _favorites.value.toMutableSet()
        val currentStationList = _favoriteStations.value.toMutableList()

        if (currentFavorites.contains(station.stationUuid)) {
            currentFavorites.remove(station.stationUuid)
            currentStationList.removeAll { it.stationUuid == station.stationUuid }
        } else {
            currentFavorites.add(station.stationUuid)
            if (!currentStationList.any { it.stationUuid == station.stationUuid }) {
                currentStationList.add(station)
            }
        }
        _favorites.value = currentFavorites
        _favoriteStations.value = currentStationList
        saveFavoritesToPrefs(currentFavorites, currentStationList)
        
        if (_selectedNavItem.value == NavigationItem.Favourites) {
            _stations.value = currentStationList
        }
    }

    fun exportFavoritesToM3u(uri: Uri) {
        viewModelScope.launch {
            try {
                val context = getApplication() as Context
                val content = generateM3uContent()
                
                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(content.toByteArray(Charsets.UTF_8))
                    }
                }
                _error.value = str(R.string.status_favorites_exported)
            } catch (e: Exception) {
                _error.value = str(R.string.error_export_failed, e.message ?: "unknown")
            }
        }
    }

    fun importFavoritesFromM3u(uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val context = getApplication() as Context
                var importedStations = emptyList<Station>()
                
                withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        val lines = inputStream.bufferedReader().readLines()
                        importedStations = parsePlaylistLines(lines)
                    }
                }
                
                if (importedStations.isNotEmpty()) {
                    _pendingImportStations.value = importedStations
                } else {
                    _error.value = str(R.string.error_no_stations_in_file)
                }
            } catch (e: Exception) {
                _error.value = str(R.string.error_import_failed, e.message ?: "unknown")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectTag(tag: Tag?) {
        val isNewTag = _selectedTag.value != tag
        _selectedTag.value = tag
        if (tag == null) return
        if (isNewTag) {
            _allStations.value = emptyList()
            _stations.value = emptyList()
        }
        _stationOffset.value = 0
        _hasMoreStations.value = true
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val isFlacFilter = _selectedBitrates.value.contains(BitrateFilter.FLAC)
                val stations = repository.searchStations(tag = tag.name, limit = 100, hideBroken = _hideBrokenStations.value)
                _hasMoreStations.value = stations.size >= 100
                if (isFlacFilter) {
                    val flacStations = repository.searchStations(tag = "flac", query = tag.name, limit = 100, hideBroken = _hideBrokenStations.value)
                    updateStations((stations + flacStations).distinctBy { it.stationUuid })
                } else {
                    updateStations(stations)
                }
            } catch (e: Exception) {
                _error.value = str(R.string.error_load_genre_stations)
            } finally {
                _isLoading.value = false
                applyFilters()
            }
        }
    }

    fun selectCountry(country: Country?) {
        val isNewCountry = _selectedCountry.value != country
        _selectedCountry.value = country
        if (country == null) return
        if (isNewCountry) {
            _allStations.value = emptyList()
            _stations.value = emptyList()
        }
        _stationOffset.value = 0
        _hasMoreStations.value = true
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val isFlacFilter = _selectedBitrates.value.contains(BitrateFilter.FLAC)
                val stations = repository.searchStations(country = country.name, limit = 100, hideBroken = _hideBrokenStations.value)
                _hasMoreStations.value = stations.size >= 100
                if (isFlacFilter) {
                    val flacStations = repository.searchStations(tag = "flac", country = country.name, limit = 100, hideBroken = _hideBrokenStations.value)
                    updateStations((stations + flacStations).distinctBy { it.stationUuid })
                } else {
                    updateStations(stations)
                }
            } catch (e: Exception) {
                _error.value = str(R.string.error_load_country_stations)
            } finally {
                _isLoading.value = false
                applyFilters()
            }
        }
    }

    fun toggleSearchMode() {
        _searchMode.value = when (_searchMode.value) {
            SearchMode.Name -> SearchMode.Tag
            SearchMode.Tag -> SearchMode.Name
        }
        _selectedSearchTag.value = null
        _tagSearchGroups.value = emptyList()
        if (_searchQuery.value.length > 2) {
            onSearchQueryChange(_searchQuery.value)
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        if (query.length > 2) {
            searchJob?.cancel()
            searchJob = viewModelScope.launch {
                delay(400)
                when (_searchMode.value) {
                    SearchMode.Name -> searchStations(query)
                    SearchMode.Tag -> searchStationsByTag(query)
                }
            }
        } else if (query.isEmpty()) {
            clearSearch()
        }
    }

    fun selectSearchTag(tagName: String?) {
        _selectedSearchTag.value = tagName
        if (tagName == null) {
            applyFilters()
        } else {
            val hideBroken = _hideBrokenStations.value
            val localFiltered = _allStations.value.filter { station ->
                station.tags.split(",").any { it.trim().equals(tagName, ignoreCase = true) }
            }
            _stations.value = localFiltered
            
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    val remoteStations = repository.searchStations(tag = tagName, limit = 100, hideBroken = hideBroken)
                    _hasMoreStations.value = false
                    val combined = (_allStations.value + remoteStations).distinctBy { it.stationUuid }
                    _allStations.value = combined
                    val filtered = combined.filter { station ->
                        station.tags.split(",").any { it.trim().equals(tagName, ignoreCase = true) }
                    }
                    _stations.value = filtered
                } catch (e: Exception) {
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        _searchQuery.value = ""
        _allStations.value = emptyList()
        _stations.value = emptyList()
        _tagSearchGroups.value = emptyList()
        _selectedSearchTag.value = null
        _searchFocusTrigger.value++
    }

    private fun searchStationsByTag(tagQuery: String) {
        _isLoading.value = true
        _error.value = null
        _selectedSearchTag.value = null
        _stationOffset.value = 0
        _hasMoreStations.value = true
        
        viewModelScope.launch {
            try {
                val hideBroken = _hideBrokenStations.value
                var offset = 0
                var allResults = repository.searchStations(tag = tagQuery, limit = 100, offset = offset, hideBroken = hideBroken)
                var retries = 0
                while (retries < 5 && allResults.size >= 100) {
                    offset += 100
                    val more = repository.searchStations(tag = tagQuery, limit = 100, offset = offset, hideBroken = hideBroken)
                    if (more.isEmpty()) break
                    allResults = (allResults + more).distinctBy { it.stationUuid }
                    retries++
                }
                
                val tagMap = mutableMapOf<String, MutableList<Station>>()
                allResults.forEach { station ->
                    station.tags.split(",").map { it.trim() }.filter { it.isNotBlank() }.forEach { tag ->
                        tagMap.getOrPut(tag) { mutableListOf() }.add(station)
                    }
                }
                
                _tagSearchGroups.value = tagMap.entries
                    .filter { it.key.contains(tagQuery, ignoreCase = true) }
                    .sortedByDescending { it.value.size }
                    .map { (tag, stations) ->
                        GenreGroup(tag, stations, stations.size, stations.size)
                    }
                
                _hasMoreStations.value = false
                updateStations(allResults)
            } catch (e: Exception) {
                _error.value = str(R.string.error_tag_search)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun cacheFavicon(uuid: String, url: String) {
        if (url.isBlank() || faviconCache.containsKey(uuid)) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val conn = java.net.URL(url).openConnection() as java.net.HttpURLConnection
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                conn.doInput = true
                conn.inputStream.use { faviconCache[uuid] = it.readBytes() }
            } catch (_: Exception) {}
        }
    }

    @OptIn(UnstableApi::class)
    fun playStation(station: Station, resetErrors: Boolean = true) {
        if (resetErrors) consecutiveErrors = 0
        var finalStation = station
        if (finalStation.countryCode.isNullOrEmpty()) {
            _allStations.value.find { it.stationUuid == station.stationUuid && !it.countryCode.isNullOrEmpty() }?.let {
                finalStation = it
            }
        }
        
        if (_selectedNavItem.value == NavigationItem.Search && _searchQuery.value.length > 2) {
            addToRecentSearches(_searchQuery.value)
        }

        addToRecent(finalStation)
        prefs.edit().putString("last_station_uuid", finalStation.stationUuid).apply()
        _currentStation.value = finalStation
        _error.value = null
        _mediaMetadata.value = null
        _audioFormat.value = null

        val artworkUri = if (finalStation.favicon.isNotEmpty()) {
            android.net.Uri.parse(finalStation.favicon)
        } else {
            android.net.Uri.parse("android.resource://${getApplication<android.app.Application>().packageName}/${com.toxa.pureradio.R.drawable.ic_radio_logo}")
        }

        val cachedBytes = faviconCache[finalStation.stationUuid]

        player?.let {
            it.stop()
            it.clearMediaItems()
            val metaBuilder = androidx.media3.common.MediaMetadata.Builder()
                .setTitle(finalStation.name)
                .setArtist(finalStation.tags)
                .setArtworkUri(artworkUri)
            if (cachedBytes != null) {
                metaBuilder.setArtworkData(cachedBytes, androidx.media3.common.MediaMetadata.PICTURE_TYPE_OTHER)
            }
            val mediaItemBuilder = MediaItem.Builder()
                .setUri(finalStation.url)
                .setMediaId(finalStation.stationUuid)
                .setMediaMetadata(metaBuilder.build())
            
            val isHls = finalStation.url.lowercase().let { url ->
                url.endsWith(".m3u8") || url.endsWith(".m3u")
            } || (finalStation.codec?.contains("hls", ignoreCase = true) == true)
            if (isHls) {
                mediaItemBuilder.setMimeType(androidx.media3.common.MimeTypes.APPLICATION_M3U8)
            }
            
            it.setMediaItem(mediaItemBuilder.build())
            it.prepare()
            it.play()
            _isPlaying.value = true
        }
    }

    private fun retryCurrentStation() {
        val station = _currentStation.value ?: return
        player?.let { player ->
            player.stop()
            player.clearMediaItems()
            val builder = MediaItem.Builder()
                .setUri(station.url)
                .setMediaId(station.stationUuid)
            if (station.url.lowercase().let { it.endsWith(".m3u8") || it.endsWith(".m3u") }
                || (station.codec?.contains("hls", ignoreCase = true) == true)
            ) {
                builder.setMimeType(androidx.media3.common.MimeTypes.APPLICATION_M3U8)
            }
            player.setMediaItem(builder.build())
            player.prepare()
            player.play()
        }
    }

    fun playNext(isAuto: Boolean = false) {
        val current = _currentStation.value ?: return
        val activeList = _stations.value
        val indexInActive = activeList.indexOfFirst { it.stationUuid == current.stationUuid }
        
        val list = if (indexInActive != -1) activeList
        else if (_selectedNavItem.value == NavigationItem.Home && 
                 _visibleGenres.value.isNotEmpty() && 
                 _selectedTag.value == null && 
                 _selectedCountry.value == null) {
            val bitrates = _selectedBitrates.value
            _genreGroups.value.flatMap { group ->
                group.stations.filter { matchesBitrateFilter(it, bitrates) }
            }.distinctBy { it.stationUuid }
        } else _stations.value
        
        if (list.isEmpty()) return

        if (list.size == 1 && list[0].stationUuid == current.stationUuid) {
            _error.value = str(R.string.error_station_unavailable)
            stopPlayback()
            return
        }

        val index = list.indexOfFirst { it.stationUuid == current.stationUuid }
        if (index != -1) {
            val nextIndex = (index + 1) % list.size
            playStation(list[nextIndex], !isAuto)
        }
    }

    fun playPrevious() {
        val current = _currentStation.value ?: return
        val activeList = _stations.value
        val indexInActive = activeList.indexOfFirst { it.stationUuid == current.stationUuid }
        
        val list = if (indexInActive != -1) activeList
        else if (_selectedNavItem.value == NavigationItem.Home && 
                 _visibleGenres.value.isNotEmpty() && 
                 _selectedTag.value == null && 
                 _selectedCountry.value == null) {
            val bitrates = _selectedBitrates.value
            _genreGroups.value.flatMap { group ->
                group.stations.filter { matchesBitrateFilter(it, bitrates) }
            }.distinctBy { it.stationUuid }
        } else _stations.value

        if (list.isEmpty()) return
        val index = list.indexOfFirst { it.stationUuid == current.stationUuid }
        if (index != -1) {
            val prevIndex = if (index - 1 < 0) list.size - 1 else index - 1
            playStation(list[prevIndex])
        }
    }

    fun togglePlayPause() {
        player?.let {
            if (it.isPlaying) it.pause() else it.play()
            _isPlaying.value = it.isPlaying
        }
    }

    fun stopPlayback() {
        player?.stop()
        _isPlaying.value = false
        _currentStation.value = null
    }

    fun searchStations(query: String) {
        if (query.isEmpty()) return
        _isLoading.value = true
        _error.value = null
        _stationOffset.value = 0
        _hasMoreStations.value = true
        
        viewModelScope.launch {
            try {
                val results = repository.searchStations(query = query, hideBroken = _hideBrokenStations.value)
                val finalResults = if (_selectedBitrates.value.contains(BitrateFilter.FLAC)) {
                    val flacResults = repository.searchStations(query = query, tag = "flac", hideBroken = _hideBrokenStations.value)
                    (results + flacResults).distinctBy { it.stationUuid }
                } else results
                _hasMoreStations.value = finalResults.size >= 100
                updateStations(finalResults)
            } catch (e: Exception) {
                _error.value = str(R.string.error_search_failed)
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        player?.release()
        player = null
    }
}
