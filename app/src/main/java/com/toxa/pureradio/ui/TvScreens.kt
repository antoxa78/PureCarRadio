package com.toxa.pureradio.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicVideo
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.TheaterComedy
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.media3.common.util.UnstableApi
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Checkbox
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Glow
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.NavigationDrawer
import androidx.tv.material3.NavigationDrawerItem
import androidx.tv.material3.NavigationDrawerItemDefaults
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Switch
import androidx.tv.material3.Text
import androidx.tv.material3.rememberDrawerState
import coil.compose.AsyncImage
import com.toxa.pureradio.BuildConfig
import com.toxa.pureradio.MainActivity
import com.toxa.pureradio.R
import com.toxa.pureradio.data.model.Station
import com.toxa.pureradio.network.Country
import com.toxa.pureradio.network.Tag
import com.toxa.pureradio.ui.viewmodel.AppLanguage
import com.toxa.pureradio.ui.viewmodel.AppTheme
import com.toxa.pureradio.ui.viewmodel.BitrateFilter
import com.toxa.pureradio.ui.viewmodel.GenreGroup
import com.toxa.pureradio.ui.viewmodel.GenreSortMode
import com.toxa.pureradio.ui.viewmodel.MainViewModel
import com.toxa.pureradio.ui.viewmodel.NavigationItem
import com.toxa.pureradio.ui.viewmodel.ScreensaverMode
import com.toxa.pureradio.ui.viewmodel.SearchMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.system.exitProcess

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvMainLayout(isPip: Boolean, showSplash: Boolean, viewModel: MainViewModel) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .onKeyEvent {
                viewModel.resetScreensaverTimer()
                false
            },
        shape = RectangleShape,
        colors = SurfaceDefaults.colors(
            containerColor = if (isPip) Color.Transparent else MaterialTheme.colorScheme.background
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isPip) {
                PipContent(viewModel)
            } else if (showSplash) {
                SplashScreen()
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    MaterialTheme.colorScheme.background
                                ),
                                center = androidx.compose.ui.geometry.Offset(x = 1000f, y = 0f),
                                radius = 2000f
                            )
                        )
                )
                TvMainScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PipContent(viewModel: MainViewModel) {
    val currentStation by viewModel.currentStation.collectAsState()
    val mediaMetadata by viewModel.mediaMetadata.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f)),
        contentAlignment = Alignment.Center
    ) {
        currentStation?.let { station ->
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.size(54.dp),
                    colors = SurfaceDefaults.colors(containerColor = Color.White.copy(alpha = 0.1f))
                ) {
                    AsyncImage(
                        model = if (station.favicon.isNotEmpty()) station.favicon else R.drawable.ic_radio_logo,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().padding(6.dp),
                        contentScale = ContentScale.Fit,
                        error = coil.compose.rememberAsyncImagePainter(R.drawable.ic_radio_logo)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    val displayTitle = if (!mediaMetadata?.title.isNullOrEmpty()) {
                        mediaMetadata?.title.toString()
                    } else {
                        station.name
                    }
                    Text(
                        text = displayTitle,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = station.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        Icons.Default.OpenInFull,
                        contentDescription = stringResource(R.string.open_desc),
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(R.string.close_desc),
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvMainScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val selectedNavItem by viewModel.selectedNavItem.collectAsState()
    val stations by viewModel.stations.collectAsState()
    val genreGroups by viewModel.genreGroups.collectAsState()
    val tags by viewModel.tags.collectAsState()
    val countries by viewModel.countries.collectAsState()
    val currentStation by viewModel.currentStation.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val isInitialized by viewModel.isInitialized.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val selectedTag by viewModel.selectedTag.collectAsState()
    val selectedCountry by viewModel.selectedCountry.collectAsState()
    val selectedSearchTag by viewModel.selectedSearchTag.collectAsState()
    val selectedBitrates by viewModel.selectedBitrates.collectAsState()
    val hasMoreStations by viewModel.hasMoreStations.collectAsState()
    val settingsSubMenu by viewModel.settingsSubMenu.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val playbackTime by viewModel.playbackTime.collectAsState()
    val isScreensaverShowing by viewModel.isScreensaverShowing.collectAsState()
    val visibleGenres by viewModel.visibleGenres.collectAsState()
    val filteredTags by viewModel.filteredTags.collectAsState()
    val tagSearchQuery by viewModel.tagSearchQuery.collectAsState()
    val genreSortMode by viewModel.genreSortMode.collectAsState()
    val mediaMetadata by viewModel.mediaMetadata.collectAsState()
    val audioFormat by viewModel.audioFormat.collectAsState()
    val filePickerState by viewModel.filePickerState.collectAsState()
    val pendingImportStations by viewModel.pendingImportStations.collectAsState()
    val quitConfirmationEnabled by viewModel.quitConfirmationEnabled.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { viewModel.importFavoritesFromM3u(it) }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("*/*")
    ) { uri: Uri? ->
        uri?.let { viewModel.exportFavoritesToM3u(it) }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    var stationToFavorite by remember { mutableStateOf<Station?>(null) }
    var isDialogReady by remember { mutableStateOf(false) }
    var genreToAdd by remember { mutableStateOf<Tag?>(null) }
    var genreToRemove by remember { mutableStateOf<String?>(null) }
    var isGenreDialogReady by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = androidx.tv.material3.DrawerValue.Closed)
    val drawerFocusRequesters = remember { NavigationItem.entries.associateWith { FocusRequester() } }
    val dialogFocusRequester = remember { FocusRequester() }
    val cancelFocusRequester = remember { FocusRequester() }

    val isDrawerOpen = drawerState.currentValue == androidx.tv.material3.DrawerValue.Open
    val drawerOpenedIntentionally = remember { mutableStateOf(false) }

    BackHandler {
        if (isDrawerOpen) {
            if (quitConfirmationEnabled) {
                showExitDialog = true
            } else {
                exitProcess(0)
            }
        } else {
            when {
                pendingImportStations != null -> viewModel.cancelRestore()
                filePickerState != null -> viewModel.closeFilePicker()
                settingsSubMenu != null -> viewModel.setSettingsSubMenu(null)
                selectedTag != null -> viewModel.selectTag(null)
                selectedCountry != null -> viewModel.selectCountry(null)
                selectedSearchTag != null -> viewModel.selectSearchTag(null)
                selectedNavItem == NavigationItem.Search && (searchQuery.isNotEmpty() || stations.isNotEmpty()) -> viewModel.clearSearch()
                isInitialized -> {
                    drawerOpenedIntentionally.value = true
                    drawerState.setValue(androidx.tv.material3.DrawerValue.Open)
                    try { drawerFocusRequesters[selectedNavItem]?.requestFocus() } catch (_: Exception) {}
                }
            }
        }
    }

    LaunchedEffect(drawerState.currentValue) {
        if (drawerState.currentValue == androidx.tv.material3.DrawerValue.Open) {
            try { drawerFocusRequesters[selectedNavItem]?.requestFocus() } catch (_: Exception) {}
        }
    }

    LaunchedEffect(stationToFavorite) {
        if (stationToFavorite != null) {
            isDialogReady = false
            try { cancelFocusRequester.requestFocus() } catch (_: Exception) {}
            delay(800)
            isDialogReady = true
        }
    }

    LaunchedEffect(genreToAdd, genreToRemove) {
        if (genreToAdd != null || genreToRemove != null) {
            isGenreDialogReady = false
            try { cancelFocusRequester.requestFocus() } catch (_: Exception) {}
            delay(800)
            isGenreDialogReady = true
        }
    }

    LaunchedEffect(drawerOpenedIntentionally.value) {
        if (drawerOpenedIntentionally.value) {
            withFrameNanos { }
            drawerOpenedIntentionally.value = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavigationDrawer(
            drawerState = drawerState,
            drawerContent = { drawerValue ->
                val isDrawerCurrentlyOpen = drawerValue == androidx.tv.material3.DrawerValue.Open
                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight()
                        .onFocusChanged {
                            if (!it.hasFocus && drawerValue == androidx.tv.material3.DrawerValue.Open) {
                                drawerState.setValue(androidx.tv.material3.DrawerValue.Closed)
                            }
                        },
                    contentPadding = PaddingValues(top = 16.dp, bottom = 140.dp, start = 4.dp, end = 4.dp)
                ) {
                    items(NavigationItem.entries) { item ->
                        NavigationDrawerItem(
                            selected = selectedNavItem == item,
                            onClick = {
                                if (item == NavigationItem.Exit) {
                                    drawerState.setValue(androidx.tv.material3.DrawerValue.Closed)
                                    showExitDialog = true
                                } else {
                                    viewModel.selectNavigationItem(item)
                                    drawerState.setValue(androidx.tv.material3.DrawerValue.Closed)
                                }
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            modifier = Modifier
                                .focusProperties { canFocus = isDrawerCurrentlyOpen }
                                .then(if (isDrawerCurrentlyOpen) Modifier.focusRequester(drawerFocusRequesters[item]!!) else Modifier),
                            leadingContent = {
                                val icon = when (item) {
                                    NavigationItem.Home -> Icons.Default.Home
                                    NavigationItem.Popular -> Icons.Default.Mic
                                    NavigationItem.Recent -> Icons.Default.History
                                    NavigationItem.Search -> Icons.Default.Search
                                    NavigationItem.Genres -> Icons.AutoMirrored.Filled.List
                                    NavigationItem.Countries -> Icons.Default.Place
                                    NavigationItem.Favourites -> Icons.Default.Favorite
                                    NavigationItem.Settings -> Icons.Default.Settings
                                    NavigationItem.Exit -> Icons.AutoMirrored.Filled.ExitToApp
                                }
                                Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp))
                            }
                        ) {
                            Text(stringResource(item.labelRes), maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                val title = when {
                    selectedTag != null -> {
                        val name = selectedTag!!.name.lowercase().split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
                        "$name (${stations.size} Stations)"
                    }
                    selectedCountry != null -> {
                        val name = selectedCountry!!.name.lowercase().split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
                        "$name (${stations.size} Stations)"
                    }
                    else -> stringResource(selectedNavItem.labelRes)
                }
                val isDeepDive = selectedTag != null || selectedCountry != null

                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 12.dp, end = 32.dp, top = 8.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isDeepDive) {
                        Surface(
                            colors = SurfaceDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            shape = MaterialTheme.shapes.extraSmall,
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Text(
                                text = if (selectedTag != null) "GENRE" else "COUNTRY",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = if (isDeepDive) FontWeight.ExtraBold else FontWeight.Medium,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    val showBitrateFilters = (selectedNavItem == NavigationItem.Home) ||
                            (selectedNavItem == NavigationItem.Popular) ||
                            (selectedNavItem == NavigationItem.Genres) ||
                            (selectedNavItem == NavigationItem.Countries)

                    if (showBitrateFilters) {
                        TvBitrateFilters(
                            selectedBitrates = selectedBitrates,
                            onToggleFilter = { viewModel.toggleBitrateFilter(it) }
                        )
                    }
                }

                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    if (error != null) {
                        Text(text = error!!, style = MaterialTheme.typography.headlineMedium)
                    } else {
                        when (selectedNavItem) {
                            NavigationItem.Home -> {
                                if (genreGroups.isNotEmpty()) {
                                    if (selectedTag == null && selectedCountry == null) {
                                        key("home_groups") {
                                            GenreGroupGrid(
                                                groups = genreGroups,
                                                onGroupClick = { name ->
                                                    val group = genreGroups.find { it.genreName == name }
                                                    if (group?.isCountry == true) {
                                                        val country = countries.find { it.name == name }
                                                            ?: Country(name = name, iso_3166_1 = "", stationcount = 0)
                                                        viewModel.selectCountry(country)
                                                    } else {
                                                        val tag = tags.find { it.name == name }
                                                            ?: Tag(name = name, stationcount = group?.totalStations ?: 0)
                                                        viewModel.selectTag(tag)
                                                    }
                                                },
                        onGroupLongClick = { name ->
                            val group = genreGroups.find { it.genreName == name }
                            if (group != null) {
                                genreToRemove = name
                            }
                        }
                                            )
                                        }
                                    } else {
                                        key(selectedTag?.name ?: selectedCountry?.name ?: "home_stations") {
                                            StationGrid(
                                                stations = stations,
                                                viewModel = viewModel,
                                                onLoadMore = if (hasMoreStations) {{ viewModel.loadMoreStations() }} else null,
                                                onLongClick = { stationToFavorite = it }
                                            )
                                        }
                                    }
                                } else {
                                    key("home_top") {
                                        StationGrid(
                                            stations = stations,
                                            viewModel = viewModel,
                                            onLoadMore = if (hasMoreStations) {{ viewModel.loadMoreStations() }} else null,
                                            onLongClick = { stationToFavorite = it }
                                        )
                                    }
                                }
                            }
                            NavigationItem.Popular -> {
                                key("popular_stations") {
                                    StationGrid(
                                        stations = stations,
                                        viewModel = viewModel,
                                        onLoadMore = if (hasMoreStations) {{ viewModel.loadMoreStations() }} else null,
                                        onLongClick = { stationToFavorite = it }
                                    )
                                }
                            }
                            NavigationItem.Recent -> {
                                key("recent_stations") {
                                    StationGrid(stations, viewModel) { stationToFavorite = it }
                                }
                            }
                            NavigationItem.Favourites -> {
                                key("favorite_stations") {
                                    StationGrid(stations, viewModel) { stationToFavorite = it }
                                }
                            }
                            NavigationItem.Search -> {
                                TvSearchScreen(
                                    viewModel,
                                    onLongClick = { stationToFavorite = it },
                                    onTagGroupLongClick = { tagName ->
                                        if (visibleGenres.contains(tagName)) {
                                            genreToRemove = tagName
                                        } else {
                                            genreToAdd = tags.find { it.name.equals(tagName, ignoreCase = true) }
                                                ?: Tag(name = tagName, stationcount = 0)
                                        }
                                    }
                                )
                            }
                            NavigationItem.Genres -> {
                                if (selectedTag == null) {
                                    key("genres_list") {
                                        Column(modifier = Modifier.fillMaxSize()) {
                                            var localTagSearchQuery by remember { mutableStateOf(tagSearchQuery) }
                                            LaunchedEffect(tagSearchQuery) {
                                                if (tagSearchQuery != localTagSearchQuery) localTagSearchQuery = tagSearchQuery
                                            }
                                            val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(start = 12.dp, end = 32.dp, top = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                OutlinedTextField(
                                                    value = localTagSearchQuery,
                                                    onValueChange = { localTagSearchQuery = it; viewModel.setTagSearchQuery(it) },
                                                    label = { Text("Search genres...") },
                                                    modifier = Modifier.weight(1f),
                                                    singleLine = true,
                                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                                    keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
                                                    trailingIcon = {
                                                        if (localTagSearchQuery.isNotEmpty()) {
                                                            Button(
                                                                onClick = { viewModel.setTagSearchQuery(""); localTagSearchQuery = "" },
                                                                modifier = Modifier.size(36.dp),
                                                                colors = ButtonDefaults.colors(containerColor = Color.Transparent, contentColor = Color.White)
                                                            ) { Text("X", fontWeight = FontWeight.Bold) }
                                                        }
                                                    },
                                                    colors = TextFieldDefaults.colors(
                                                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                                                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        focusedContainerColor = Color.Transparent,
                                                        unfocusedContainerColor = Color.Transparent
                                                    )
                                                )
                                                Spacer(modifier = Modifier.width(16.dp))
                                                Text("Sort: ", style = MaterialTheme.typography.labelLarge)
                                                GenreSortMode.entries.forEach { mode ->
                                                    Button(
                                                        onClick = { viewModel.setGenreSortMode(mode) },
                                                        modifier = Modifier.padding(horizontal = 4.dp),
                                                        colors = if (genreSortMode == mode) ButtonDefaults.colors(
                                                            containerColor = MaterialTheme.colorScheme.primary,
                                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                                        ) else ButtonDefaults.colors()
                                                    ) { Text(if (mode == GenreSortMode.Name) "Name" else "Count") }
                                                }
                                            }
                                            Box(modifier = Modifier.weight(1f)) {
                                                TvTagGrid(
                                                    tags = filteredTags,
                                                    onTagClick = { viewModel.selectTag(it) },
                                                    onTagLongClick = { tag -> genreToAdd = tag }
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    key(selectedTag!!.name) {
                                        StationGrid(
                                            stations = stations,
                                            viewModel = viewModel,
                                            onLoadMore = if (hasMoreStations) {{ viewModel.loadMoreStations() }} else null,
                                            onLongClick = { stationToFavorite = it }
                                        )
                                    }
                                }
                            }
                            NavigationItem.Countries -> {
                                if (selectedCountry == null) {
                                    key("countries_list") {
                                        TvCountryGrid(countries) { viewModel.selectCountry(it) }
                                    }
                                } else {
                                    key(selectedCountry!!.name) {
                                        StationGrid(
                                            stations = stations,
                                            viewModel = viewModel,
                                            onLoadMore = if (hasMoreStations) {{ viewModel.loadMoreStations() }} else null,
                                            onLongClick = { stationToFavorite = it }
                                        )
                                    }
                                }
                            }
                            NavigationItem.Settings -> {
                                TvSettingsScreen(
                                    viewModel,
                                    onImportPlaylist = {
                                        try { importLauncher.launch(arrayOf("*/*")) }
                                        catch (e: Exception) {
                                            viewModel.setError("System picker unavailable. Using Internal Explorer.")
                                            viewModel.openFilePicker(isExport = false)
                                        }
                                    },
                                    onExportPlaylist = {
                                        try { exportLauncher.launch("pure_radio_favorites.m3u") }
                                        catch (e: Exception) {
                                            viewModel.setError("System picker unavailable. Using Internal Explorer.")
                                            viewModel.openFilePicker(isExport = true, suggestedFileName = "pure_radio_favorites.m3u")
                                        }
                                    },
                                    onPermissionRequest = {
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                                            if (!android.os.Environment.isExternalStorageManager()) {
                                                try {
                                                    val intent = Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:${context.packageName}"))
                                                    context.startActivity(intent)
                                                } catch (e: Exception) {
                                                    try {
                                                        val intent = Intent(android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                                                        context.startActivity(intent)
                                                    } catch (e2: Exception) {}
                                                }
                                            } else {
                                                viewModel.setError("All Files Access already granted")
                                            }
                                        } else {
                                            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                                        }
                                    }
                                )
                            }
                            NavigationItem.Exit -> {}
                        }
                    }

                    if (isLoading && selectedNavItem != NavigationItem.Search) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                LinearProgressIndicator(
                                    modifier = Modifier.width(200.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Searching for waves...",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }

                if (currentStation != null) {
                    Spacer(modifier = Modifier.height(115.dp))
                }
            }
        }

        currentStation?.let { station ->
            val playbackDuration by viewModel.playbackDuration.collectAsState()
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                TvNowPlayingBar(
                    station = station,
                    isPlaying = isPlaying,
                    isFavorite = favorites.contains(station.stationUuid),
                    playbackTime = playbackTime,
                    playbackDuration = playbackDuration,
                    mediaMetadata = mediaMetadata,
                    audioFormat = audioFormat,
                    onTogglePlay = { viewModel.togglePlayPause() },
                    onToggleFavorite = { viewModel.toggleFavorite(station) },
                    onNext = { viewModel.playNext() },
                    onPrevious = { viewModel.playPrevious() }
                )
            }
        }
    }

    if (isScreensaverShowing) {
        TvScreensaver(viewModel)
    }

    filePickerState?.let { state ->
        TvFilePicker(
            state = state,
            onNavigate = { viewModel.navigateInFilePicker(it) },
            onNavigateUp = { viewModel.navigateUpFilePicker() },
            onSelected = { viewModel.handleFileSelection(it) },
            onDismiss = { viewModel.closeFilePicker() }
        )
    }

    stationToFavorite?.let { station ->
        val isAlreadyFavorite = favorites.contains(station.stationUuid)
        val isTv = (context as? MainActivity)?.isTv() == true
        if (isTv) {
            Dialog(onDismissRequest = { stationToFavorite = null }) {
                Surface(
                    modifier = Modifier.width(420.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = SurfaceDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = androidx.tv.material3.Border(
                        border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                        shape = MaterialTheme.shapes.extraLarge
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.size(80.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(if (isAlreadyFavorite) Icons.Default.FavoriteBorder else Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(if (isAlreadyFavorite) stringResource(R.string.fav_remove_title) else stringResource(R.string.fav_add_title), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(station.name, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(32.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Button(onClick = { if (isDialogReady) stationToFavorite = null }, modifier = Modifier.weight(1f).focusRequester(cancelFocusRequester),
                                colors = ButtonDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                            ) { Text(stringResource(R.string.cancel), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) }
                            Button(onClick = { if (isDialogReady) { viewModel.toggleFavorite(station); stationToFavorite = null } }, modifier = Modifier.weight(1f).focusRequester(dialogFocusRequester),
                                colors = ButtonDefaults.colors(containerColor = if (isAlreadyFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary, contentColor = if (isAlreadyFavorite) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary)
                            ) { Text(if (isAlreadyFavorite) stringResource(R.string.fav_remove) else stringResource(R.string.fav_add), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold) }
                        }
                    }
                }
            }
        }
    }

    genreToAdd?.let { tag ->
        Dialog(onDismissRequest = { genreToAdd = null }) {
            Surface(modifier = Modifier.width(420.dp), shape = MaterialTheme.shapes.extraLarge,
                colors = SurfaceDefaults.colors(containerColor = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.onSurface),
                border = androidx.tv.material3.Border(border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)), shape = MaterialTheme.shapes.extraLarge)
            ) {
                Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(80.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(stringResource(R.string.home_add_to_home), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(tag.name.lowercase().split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(onClick = { if (isGenreDialogReady) genreToAdd = null }, modifier = Modifier.weight(1f), colors = ButtonDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)) { Text(stringResource(R.string.back_desc), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) }
                        Button(onClick = { if (isGenreDialogReady) { viewModel.toggleGenreVisibility(tag.name); genreToAdd = null } }, modifier = Modifier.weight(1f), colors = ButtonDefaults.colors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)) { Text(stringResource(R.string.fav_add), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold) }
                    }
                }
            }
        }
    }

    genreToRemove?.let { genreName ->
        val removeFocusRequester = remember { FocusRequester() }
        val backFocusRequester = remember { FocusRequester() }
        val group = genreGroups.find { it.genreName == genreName }
        val isCountry = group?.isCountry == true

        Dialog(onDismissRequest = { genreToRemove = null }) {
            Surface(modifier = Modifier.width(420.dp), shape = MaterialTheme.shapes.extraLarge,
                colors = SurfaceDefaults.colors(containerColor = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.onSurface),
                border = androidx.tv.material3.Border(border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)), shape = MaterialTheme.shapes.extraLarge)
            ) {
                Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(80.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(if (isCountry) stringResource(R.string.home_remove_title_country) else stringResource(R.string.home_remove_title_genre), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(genreName.lowercase().split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(onClick = { if (isGenreDialogReady) genreToRemove = null }, modifier = Modifier.weight(1f).focusRequester(backFocusRequester), colors = ButtonDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)) { Text(stringResource(R.string.back_desc), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) }
                        Button(onClick = { if (isGenreDialogReady) { if (isCountry) viewModel.toggleCountryVisibility(genreName) else viewModel.toggleGenreVisibility(genreName); genreToRemove = null } }, modifier = Modifier.weight(1f).focusRequester(removeFocusRequester), colors = ButtonDefaults.colors(containerColor = MaterialTheme.colorScheme.error, contentColor = MaterialTheme.colorScheme.onError)) { Text(stringResource(R.string.fav_remove), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold) }
                    }
                    LaunchedEffect(Unit) { try { removeFocusRequester.requestFocus() } catch (_: Exception) {} }
                }
            }
        }
    }

    if (showExitDialog) {
        Dialog(onDismissRequest = { showExitDialog = false }) {
            Surface(shape = MaterialTheme.shapes.medium, colors = SurfaceDefaults.colors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(32.dp).fillMaxWidth(0.4f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.exit_title), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(onClick = { showExitDialog = false }, modifier = Modifier.weight(1f), colors = ButtonDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)) { Text(stringResource(R.string.no), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) }
                        Button(onClick = { exitProcess(0) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.colors(containerColor = MaterialTheme.colorScheme.error, contentColor = MaterialTheme.colorScheme.onError)) { Text(stringResource(R.string.yes), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold) }
                    }
                }
            }
        }
    }
    pendingImportStations?.let { stations ->
        var showConfirmation by remember { mutableStateOf(false) }
        val restoreFocusRequester = remember { FocusRequester() }
        if (!showConfirmation) {
            Dialog(onDismissRequest = { viewModel.cancelRestore() }) {
                Surface(modifier = Modifier.width(420.dp), shape = MaterialTheme.shapes.extraLarge,
                    colors = SurfaceDefaults.colors(containerColor = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.onSurface),
                    border = androidx.tv.material3.Border(border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)), shape = MaterialTheme.shapes.extraLarge)
                ) {
                    Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(stringResource(R.string.restore_title), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(stringResource(R.string.restore_found, stations.size), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 16.dp))
                        Text(stringResource(R.string.restore_choose_action), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 16.dp))
                        Button(onClick = { viewModel.confirmRestore(replace = false) }, modifier = Modifier.fillMaxWidth().focusRequester(restoreFocusRequester)) { Text(stringResource(R.string.restore_add_current), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { showConfirmation = true }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.restore_replace_btn), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { viewModel.cancelRestore() }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.cancel), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) }
                    }
                }
            }
        } else {
            Dialog(onDismissRequest = { showConfirmation = false }) {
                Surface(modifier = Modifier.width(420.dp), shape = MaterialTheme.shapes.extraLarge,
                    colors = SurfaceDefaults.colors(containerColor = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.onSurface),
                    border = androidx.tv.material3.Border(border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)), shape = MaterialTheme.shapes.extraLarge)
                ) {
                    Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(stringResource(R.string.restore_confirm_title), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(stringResource(R.string.restore_confirm_body), style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Button(onClick = { showConfirmation = false }, modifier = Modifier.weight(1f).focusRequester(restoreFocusRequester)) { Text(stringResource(R.string.no), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) }
                            Button(onClick = { viewModel.confirmRestore(replace = true) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.colors(containerColor = MaterialTheme.colorScheme.error)) { Text(stringResource(R.string.restore_yes_replace), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GenreGroupGrid(groups: List<GenreGroup>, autoFocus: Boolean = true, onGroupClick: (String) -> Unit, onGroupLongClick: ((String) -> Unit)? = null) {
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    val isTv = (context as? MainActivity)?.isTv() == true

    LaunchedEffect(autoFocus, groups.isNotEmpty()) {
        if (isTv && autoFocus) {
            try { focusRequester.requestFocus() } catch (e: Exception) {}
        }
    }
    if (groups.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().then(if (isTv) Modifier.focusRequester(focusRequester).focusable() else Modifier))
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(if (isTv) 5 else 2),
            contentPadding = PaddingValues(start = 12.dp, end = if (isTv) 32.dp else 12.dp, top = 16.dp, bottom = 140.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(groups, key = { _, group -> group.genreName }) { index, group ->
                GenreGroupCard(
                    group = group.copy(genreName = group.genreName.lowercase().split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }),
                    onClick = { onGroupClick(group.genreName) },
                    onLongClick = if (onGroupLongClick != null) {{ onGroupLongClick(group.genreName) }} else null,
                    modifier = if (isTv && index == 0) Modifier.focusRequester(focusRequester) else Modifier
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TvGenreGroupCard(group: GenreGroup, onClick: () -> Unit, onLongClick: (() -> Unit)? = null, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        onLongClick = onLongClick,
        modifier = modifier.padding(8.dp).height(180.dp),
        scale = CardDefaults.scale(focusedScale = 1.1f),
        glow = CardDefaults.glow(focusedGlow = Glow(elevationColor = getGenreColor(group.genreName).copy(alpha = 0.5f), elevation = 12.dp))
    ) { GenreGroupCardContent(group) }
}

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun StationGrid(
    stations: List<Station>,
    viewModel: MainViewModel,
    autoFocus: Boolean = true,
    onLoadMore: (() -> Unit)? = null,
    onLongClick: (Station) -> Unit = {}
) {
    val favorites by viewModel.favorites.collectAsState()
    val currentStation by viewModel.currentStation.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val loadMoreFocusRequester = remember { FocusRequester() }
    var loadMoreCount by remember { mutableIntStateOf(0) }

    val context = LocalContext.current
    val isTv = (context as? MainActivity)?.isTv() == true

    LaunchedEffect(autoFocus, stations.isNotEmpty()) {
        if (isTv && autoFocus && loadMoreCount == 0) {
            try { focusRequester.requestFocus() } catch (e: Exception) {}
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().then(if (isTv) Modifier.focusRequester(focusRequester) else Modifier)
    ) {
        if (stations.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(if (isTv) 5 else 2),
                contentPadding = PaddingValues(start = 12.dp, end = if (isTv) 32.dp else 12.dp, top = 16.dp, bottom = 140.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(stations, key = { _, station -> station.stationUuid }) { index, station ->
                    TvStationCard(
                        station = station,
                        isFavorite = favorites.contains(station.stationUuid),
                        isCurrent = currentStation?.stationUuid == station.stationUuid,
                        onClick = { viewModel.playStation(station) },
                        onLongClick = { onLongClick(station) },
                        modifier = if (isTv && index == 0) Modifier.focusRequester(focusRequester) else Modifier
                    )
                }
                if (onLoadMore != null) {
                    item(key = "load_more", span = { androidx.compose.foundation.lazy.grid.GridItemSpan(if (isTv) 5 else 2) }) {
                        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                            if (isTv) {
                                Button(onClick = { if (!isLoading) { loadMoreCount++; onLoadMore() } }, modifier = Modifier.focusRequester(loadMoreFocusRequester)) {
                                    Text(if (isLoading) "Loading..." else "Load More")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TvStationCard(
    station: Station,
    isFavorite: Boolean,
    isCurrent: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        onLongClick = onLongClick,
        modifier = modifier.padding(8.dp).height(180.dp),
        scale = CardDefaults.scale(focusedScale = 1.1f),
        glow = CardDefaults.glow(focusedGlow = Glow(elevationColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), elevation = 12.dp)),
        border = CardDefaults.border(focusedBorder = androidx.tv.material3.Border(border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary), shape = MaterialTheme.shapes.medium)),
        colors = CardDefaults.colors(containerColor = if (isCurrent) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) { StationCardContent(station, isFavorite, isCurrent) }
}

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TvTagGrid(tags: List<Tag>, autoFocus: Boolean = true, onTagClick: (Tag) -> Unit, onTagLongClick: ((Tag) -> Unit)? = null) {
    val focusRequester = remember { FocusRequester() }
    val hasFocused = remember { mutableStateOf(false) }
    LaunchedEffect(autoFocus, tags.isNotEmpty()) {
        if (autoFocus) { try { focusRequester.requestFocus() } catch (e: Exception) {}; if (tags.isNotEmpty()) hasFocused.value = true }
    }
    if (tags.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize())
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            contentPadding = PaddingValues(start = 12.dp, end = 32.dp, top = 32.dp, bottom = 140.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(tags, key = { _, tag -> tag.name }) { index, tag ->
                Card(
                    onClick = { onTagClick(tag) },
                    onLongClick = if (onTagLongClick != null) {{ onTagLongClick(tag) }} else null,
                    modifier = Modifier.padding(8.dp).height(180.dp).then(if (index == 0) Modifier.focusRequester(focusRequester) else Modifier),
                    scale = CardDefaults.scale(focusedScale = 1.1f)
                ) {
                    Box(modifier = Modifier.fillMaxSize().background(getGenreColor(tag.name))) {
                        AsyncImage(model = getGenreImageUrl(tag.name), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop, alpha = 0.4f)
                        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)))))
                        Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Text(text = tag.name.lowercase().split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(shape = MaterialTheme.shapes.extraSmall, colors = SurfaceDefaults.colors(containerColor = Color.White.copy(alpha = 0.2f))) {
                                Text("${tag.stationcount} stations", style = MaterialTheme.typography.labelSmall, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvCountryGrid(countries: List<Country>, autoFocus: Boolean = true, onCountryClick: (Country) -> Unit) {
    val focusRequester = remember { FocusRequester() }
    val hasFocused = remember { mutableStateOf(false) }
    LaunchedEffect(autoFocus, countries.isNotEmpty()) {
        if (autoFocus) { try { focusRequester.requestFocus() } catch (e: Exception) {}; if (countries.isNotEmpty()) hasFocused.value = true }
    }
    if (countries.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().focusRequester(focusRequester).focusable())
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            contentPadding = PaddingValues(start = 12.dp, end = 32.dp, top = 32.dp, bottom = 140.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(countries, key = { _, country -> country.iso_3166_1 }) { index, country ->
                Card(
                    onClick = { onCountryClick(country) },
                    modifier = Modifier.padding(8.dp).height(180.dp).then(if (index == 0) Modifier.focusRequester(focusRequester) else Modifier),
                    scale = CardDefaults.scale(focusedScale = 1.1f)
                ) {
                    Box(modifier = Modifier.fillMaxSize().background(getGenreColor(country.name))) {
                        val flagCode = country.iso_3166_1.lowercase().trim()
                        AsyncImage(model = if (flagCode.isNotEmpty()) "https://flagcdn.com/w160/$flagCode.png" else "https://images.unsplash.com/photo-1526772662000-3f88f10405ff?q=80&w=600&auto=format&fit=crop", contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop, alpha = 0.35f)
                        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)))))
                        Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Text(text = country.name.lowercase().split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(shape = MaterialTheme.shapes.extraSmall, colors = SurfaceDefaults.colors(containerColor = Color.White.copy(alpha = 0.2f))) {
                                Text("${country.stationcount} stations", style = MaterialTheme.typography.labelSmall, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvBitrateFilters(
    selectedBitrates: Set<BitrateFilter>,
    onToggleFilter: (BitrateFilter) -> Unit
) {
    val context = LocalContext.current
    val isTv = (context as? MainActivity)?.isTv() == true

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
        Text("Filter: ", style = MaterialTheme.typography.labelLarge)
        BitrateFilter.entries.forEach { filter ->
            val label = when (filter) { BitrateFilter.Low -> "Low"; BitrateFilter.High -> "High"; BitrateFilter.FLAC -> "FLAC" }
            val isSelected = selectedBitrates.contains(filter)
            Button(
                onClick = { onToggleFilter(filter) },
                modifier = Modifier.padding(horizontal = 4.dp),
                scale = if (isSelected) ButtonDefaults.scale(focusedScale = 1.1f) else ButtonDefaults.scale(),
                colors = if (isSelected) ButtonDefaults.colors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary) else ButtonDefaults.colors()
            ) { Text(label) }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvSettingsScreen(
    viewModel: MainViewModel,
    onImportPlaylist: () -> Unit,
    onExportPlaylist: () -> Unit,
    onPermissionRequest: () -> Unit
) {
    val visibleGenres by viewModel.visibleGenres.collectAsState()
    val tags by viewModel.tags.collectAsState()
    val filteredTags by viewModel.filteredTags.collectAsState()
    val tagSearchQuery by viewModel.tagSearchQuery.collectAsState()
    var localTagSearchQuery by remember { mutableStateOf(tagSearchQuery) }
    LaunchedEffect(tagSearchQuery) { if (tagSearchQuery != localTagSearchQuery) localTagSearchQuery = tagSearchQuery }
    val settingsSubMenu by viewModel.settingsSubMenu.collectAsState()
    val hideBroken by viewModel.hideBrokenStations.collectAsState()
    val serverStats by viewModel.serverStats.collectAsState()
    val lastUpdate by viewModel.lastDbUpdate.collectAsState()
    val autoUpdateInterval by viewModel.autoUpdateInterval.collectAsState()
    val screensaverEnabled by viewModel.screensaverEnabled.collectAsState()
    val screensaverTimeout by viewModel.screensaverTimeout.collectAsState()
    val screensaverMode by viewModel.screensaverMode.collectAsState()
    val audioPassthrough by viewModel.audioPassthrough.collectAsState()
    val resumeLastStation by viewModel.resumeLastStation.collectAsState()
    val genreSortMode by viewModel.genreSortMode.collectAsState()
    val minTagFilter by viewModel.minTagFilter.collectAsState()
    val appTheme by viewModel.appTheme.collectAsState()
    val quitConfirmationEnabled by viewModel.quitConfirmationEnabled.collectAsState()
    val defaultStartupCategory by viewModel.defaultStartupCategoryFlow.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()

    val subMenuFocusRequester = remember { FocusRequester() }
    val mainMenuFocusRequester = remember { FocusRequester() }

    LaunchedEffect(settingsSubMenu) {
        if (settingsSubMenu != null) { try { subMenuFocusRequester.requestFocus() } catch (_: Exception) {} }
        else { try { mainMenuFocusRequester.requestFocus() } catch (_: Exception) {} }
    }

    when (settingsSubMenu) {
        "HomeGenres" -> TvHomeGenresSettings(viewModel, visibleGenres, filteredTags, tagSearchQuery, genreSortMode, subMenuFocusRequester)
        "AutoUpdate" -> TvAutoUpdateSettings(viewModel, autoUpdateInterval, subMenuFocusRequester)
        "Screensaver" -> TvScreensaverSettings(viewModel, screensaverEnabled, screensaverTimeout, screensaverMode, subMenuFocusRequester)
        "AppTheme" -> TvAppThemeSettings(viewModel, appTheme, subMenuFocusRequester)
        "DefaultCategory" -> TvDefaultCategorySettings(viewModel, defaultStartupCategory, subMenuFocusRequester)
        "AppLanguage" -> TvAppLanguageSettings(viewModel, appLanguage, subMenuFocusRequester)
        else -> TvSettingsMain(viewModel, appTheme, quitConfirmationEnabled, screensaverEnabled, screensaverTimeout, hideBroken, minTagFilter, autoUpdateInterval, audioPassthrough, resumeLastStation, defaultStartupCategory, serverStats, lastUpdate, onImportPlaylist, onExportPlaylist, onPermissionRequest, mainMenuFocusRequester)
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvSettingsMain(viewModel: MainViewModel, appTheme: AppTheme, quitConfirmationEnabled: Boolean, screensaverEnabled: Boolean, screensaverTimeout: Int, hideBroken: Boolean, minTagFilter: Boolean, autoUpdateInterval: Int, audioPassthrough: Boolean, resumeLastStation: Boolean, defaultStartupCategory: NavigationItem, serverStats: com.toxa.pureradio.network.ServerStats?, lastUpdate: Long, onImportPlaylist: () -> Unit, onExportPlaylist: () -> Unit, onPermissionRequest: () -> Unit, mainMenuFocusRequester: FocusRequester) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 32.dp, end = 32.dp, top = 24.dp, bottom = 170.dp)
    ) {
        item {
            Text(stringResource(R.string.settings_title), style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(24.dp))
        }
        item {
            ListItem(selected = false, onClick = { viewModel.setSettingsSubMenu("AppTheme") }, modifier = Modifier.focusRequester(mainMenuFocusRequester),
                headlineContent = { Text(stringResource(R.string.settings_theme)) },
                supportingContent = { Text("Current: ${appTheme.name}") },
                leadingContent = { Icon(Icons.Default.TheaterComedy, contentDescription = null) },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) })
        }
        item {
            ListItem(selected = false, onClick = { viewModel.setSettingsSubMenu("AppLanguage") },
                headlineContent = { Text(stringResource(R.string.settings_language)) },
                supportingContent = { Text(stringResource(R.string.settings_language_desc)) },
                leadingContent = { Icon(Icons.Default.Public, contentDescription = null) },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) })
        }
        item {
            ListItem(selected = false, onClick = { viewModel.setQuitConfirmationEnabled(!quitConfirmationEnabled) },
                headlineContent = { Text(stringResource(R.string.settings_quit_confirmation)) }, supportingContent = { Text(stringResource(R.string.settings_quit_confirmation_desc)) },
                leadingContent = { Icon(Icons.Default.Warning, contentDescription = null) },
                trailingContent = { Switch(checked = quitConfirmationEnabled, onCheckedChange = null) })
        }
        item {
            ListItem(selected = false, onClick = { viewModel.setSettingsSubMenu("Screensaver") },
                headlineContent = { Text(stringResource(R.string.settings_screensaver)) }, supportingContent = { Text(if (screensaverEnabled) "Enabled (${screensaverTimeout}m)" else "Disabled") },
                leadingContent = { Icon(Icons.Default.MusicVideo, contentDescription = null) },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) })
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            ListItem(selected = false, onClick = { viewModel.toggleAudioPassthrough() },
                headlineContent = { Text(stringResource(R.string.settings_audio_passthrough)) }, supportingContent = { Text(stringResource(R.string.settings_audio_passthrough_desc)) },
                leadingContent = { Icon(Icons.Default.MusicNote, contentDescription = null) },
                trailingContent = { Switch(checked = audioPassthrough, onCheckedChange = null) })
        }
        item {
            ListItem(selected = false, onClick = { viewModel.setResumeLastStation(!resumeLastStation) },
                headlineContent = { Text(stringResource(R.string.settings_resume_last)) }, supportingContent = { Text(stringResource(R.string.settings_resume_last_desc)) },
                leadingContent = { Icon(Icons.Default.PlayArrow, contentDescription = null) },
                trailingContent = { Switch(checked = resumeLastStation, onCheckedChange = null) })
        }
        item {
            ListItem(selected = false, onClick = { viewModel.setSettingsSubMenu("DefaultCategory") },
                headlineContent = { Text(stringResource(R.string.settings_startup_category)) }, supportingContent = { Text("Current: ${stringResource(defaultStartupCategory.labelRes)}") },
                leadingContent = { Icon(Icons.Default.PlayArrow, contentDescription = null) },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) })
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            Text(stringResource(R.string.settings_database).uppercase(), style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp), color = MaterialTheme.colorScheme.primary)
            ListItem(selected = false, onClick = { viewModel.toggleHideBroken() },
                headlineContent = { Text(stringResource(R.string.database_smart_filter)) }, supportingContent = { Text(stringResource(R.string.database_smart_filter_desc)) },
                leadingContent = { Icon(Icons.Default.Public, contentDescription = null) },
                trailingContent = { Switch(checked = hideBroken, onCheckedChange = null) })
        }
        item {
            ListItem(selected = false, onClick = { viewModel.toggleMinTagFilter() },
                headlineContent = { Text(stringResource(R.string.database_min_tags)) }, supportingContent = { Text(stringResource(R.string.database_min_tags_desc)) },
                leadingContent = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                trailingContent = { Switch(checked = minTagFilter, onCheckedChange = null) })
        }
        item {
            ListItem(selected = false, onClick = { viewModel.setSettingsSubMenu("AutoUpdate") },
                headlineContent = { Text(stringResource(R.string.database_sync)) }, supportingContent = { Text("Update frequency: ${if(autoUpdateInterval > 0) "${autoUpdateInterval}h" else "Disabled"}") },
                leadingContent = { Icon(Icons.Default.History, contentDescription = null) },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) })
        }
        item {
            Text(stringResource(R.string.data_management).uppercase(), style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp), color = MaterialTheme.colorScheme.primary)
            ListItem(selected = false, onClick = { viewModel.openFilePicker(isExport = true, suggestedFileName = "pure_radio_favorites.m3u") },
                headlineContent = { Text(stringResource(R.string.data_backup)) }, supportingContent = { Text(stringResource(R.string.data_backup_desc)) },
                leadingContent = { Icon(Icons.Default.CloudUpload, contentDescription = null, tint = MaterialTheme.colorScheme.primary) })
            ListItem(selected = false, onClick = { viewModel.openFilePicker(isExport = false) },
                headlineContent = { Text(stringResource(R.string.data_restore)) }, supportingContent = { Text(stringResource(R.string.data_restore_desc)) },
                leadingContent = { Icon(Icons.Default.CloudDownload, contentDescription = null, tint = MaterialTheme.colorScheme.primary) })
            ListItem(selected = false, onClick = onPermissionRequest,
                headlineContent = { Text(stringResource(R.string.data_permissions)) }, supportingContent = { Text(stringResource(R.string.data_permissions_desc)) },
                leadingContent = { Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary) })
        }
        item {
            ListItem(selected = false, onClick = { viewModel.updateDatabase() },
                headlineContent = { Text(stringResource(R.string.database_refresh)) },
                supportingContent = { val dateStr = if (lastUpdate > 0) SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(lastUpdate)) else "Never"; Text("Last synced: $dateStr \u2022 ${serverStats?.stations ?: "..."} Stations available") },
                leadingContent = { Icon(Icons.Default.Radio, contentDescription = null) },
                trailingContent = { Icon(Icons.Default.History, contentDescription = null) })
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            Text(stringResource(R.string.settings_home_genres).uppercase(), style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(bottom = 8.dp), color = MaterialTheme.colorScheme.primary)
            ListItem(selected = false, onClick = { viewModel.setSettingsSubMenu("HomeGenres") },
                headlineContent = { Text(stringResource(R.string.settings_home_genres)) }, supportingContent = { Text(stringResource(R.string.settings_home_genres_desc)) },
                leadingContent = { Icon(Icons.Default.Home, contentDescription = null) },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) })
        }
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp), horizontalArrangement = Arrangement.Center) {
                Surface(colors = SurfaceDefaults.colors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)), shape = MaterialTheme.shapes.medium,
                    border = androidx.tv.material3.Border(border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)), shape = MaterialTheme.shapes.medium)) {
                    Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Radio, contentDescription = null, modifier = Modifier.size(56.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("PURE RADIO TV", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                        Text("A Premium Retro Experience", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Build: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(BuildConfig.BUILD_TIME))} \u2022 v${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvDefaultCategorySettings(viewModel: MainViewModel, currentCategory: NavigationItem, subMenuFocusRequester: FocusRequester) {
    val validCategories = listOf(
        NavigationItem.Home, NavigationItem.Popular, NavigationItem.Recent,
        NavigationItem.Genres, NavigationItem.Countries, NavigationItem.Favourites
    )
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(start = 32.dp, end = 32.dp, top = 24.dp, bottom = 170.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = { viewModel.setSettingsSubMenu(null) }, modifier = Modifier.focusRequester(subMenuFocusRequester)) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_desc)) }
                Spacer(modifier = Modifier.width(16.dp))
                Text(stringResource(R.string.settings_startup_category), style = MaterialTheme.typography.headlineMedium)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
        validCategories.forEach { category ->
            val desc = when (category) {
                NavigationItem.Home -> "Top stations"
                NavigationItem.Popular -> "Trending stations"
                NavigationItem.Recent -> "Recently played"
                NavigationItem.Genres -> "Browse by genre"
                NavigationItem.Countries -> "Browse by country"
                NavigationItem.Favourites -> "Your saved stations"
                else -> ""
            }
            item {
                ListItem(selected = currentCategory == category, onClick = { viewModel.setDefaultStartupCategory(category) },
                    headlineContent = { Text(stringResource(category.labelRes)) }, supportingContent = { Text(desc) },
                    trailingContent = { if (currentCategory == category) Icon(Icons.Default.GraphicEq, contentDescription = null, tint = MaterialTheme.colorScheme.primary) })
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvHomeGenresSettings(viewModel: MainViewModel, visibleGenres: Set<String>, filteredTags: List<Tag>, tagSearchQuery: String, genreSortMode: GenreSortMode, subMenuFocusRequester: FocusRequester) {
    var localTagSearchQuery by remember { mutableStateOf(tagSearchQuery) }
    LaunchedEffect(tagSearchQuery) { if (tagSearchQuery != localTagSearchQuery) localTagSearchQuery = tagSearchQuery }
    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(start = 32.dp, end = 32.dp, top = 24.dp, bottom = 170.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = { viewModel.setSettingsSubMenu(null) }, modifier = Modifier.focusRequester(subMenuFocusRequester)) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_desc)) }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Personalize Home Tab", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.weight(1f))
                Text("Sort: ", style = MaterialTheme.typography.labelLarge)
                GenreSortMode.entries.forEach { mode ->
                    Button(onClick = { viewModel.setGenreSortMode(mode) }, modifier = Modifier.padding(horizontal = 4.dp),
                        colors = if (genreSortMode == mode) ButtonDefaults.colors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary) else ButtonDefaults.colors()
                    ) { Text(mode.name) }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
        item {
            OutlinedTextField(value = localTagSearchQuery, onValueChange = { viewModel.setTagSearchQuery(it); localTagSearchQuery = it }, label = { Text("Search tags...") }, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), singleLine = true, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search), keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
                colors = TextFieldDefaults.colors(focusedTextColor = MaterialTheme.colorScheme.onSurface, unfocusedTextColor = MaterialTheme.colorScheme.onSurface, focusedLabelColor = MaterialTheme.colorScheme.primary, unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant, focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent))
        }
        item { Text("GENRES", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp), color = MaterialTheme.colorScheme.primary) }
        items(filteredTags) { tag ->
            ListItem(selected = false, onClick = { viewModel.toggleGenreVisibility(tag.name) },
                headlineContent = { Text(tag.name.lowercase().split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }) },
                supportingContent = { Text("${tag.stationcount} stations") },
                trailingContent = { Checkbox(checked = visibleGenres.contains(tag.name), onCheckedChange = null) })
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvAutoUpdateSettings(viewModel: MainViewModel, autoUpdateInterval: Int, subMenuFocusRequester: FocusRequester) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(start = 32.dp, end = 32.dp, top = 24.dp, bottom = 170.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = { viewModel.setSettingsSubMenu(null) }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_desc)) }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Database Update Interval", style = MaterialTheme.typography.headlineMedium)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
        listOf(0 to "Manual Only (Off)", 12 to "Every 12 Hours", 24 to "Every 24 Hours").forEachIndexed { index, (hours, label) ->
            item(key = hours) {
                ListItem(selected = autoUpdateInterval == hours, onClick = { viewModel.setAutoUpdateInterval(hours); viewModel.setSettingsSubMenu(null) },
                    modifier = if (index == 0) Modifier.focusRequester(subMenuFocusRequester) else Modifier,
                    headlineContent = { Text(label) },
                    trailingContent = { if (autoUpdateInterval == hours) Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary) })
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvScreensaverSettings(viewModel: MainViewModel, screensaverEnabled: Boolean, screensaverTimeout: Int, screensaverMode: ScreensaverMode, subMenuFocusRequester: FocusRequester) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(start = 32.dp, end = 32.dp, top = 24.dp, bottom = 170.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = { viewModel.setSettingsSubMenu(null) }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_desc)) }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Screensaver Preferences", style = MaterialTheme.typography.headlineMedium)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
        item {
            ListItem(selected = false, onClick = { viewModel.toggleScreensaver(!screensaverEnabled) }, modifier = Modifier.focusRequester(subMenuFocusRequester),
                headlineContent = { Text("Activate Screensaver") }, supportingContent = { Text("Automatically engage when music is playing") },
                trailingContent = { Switch(checked = screensaverEnabled, onCheckedChange = null) })
        }
        item { Text("Display Mode", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp), color = MaterialTheme.colorScheme.primary) }
        listOf(ScreensaverMode.StationInfo to "Retro Station Info", ScreensaverMode.BlackScreen to "Deep Black (OLED Safe)").forEach { (mode, label) ->
            item {
                ListItem(selected = screensaverMode == mode, onClick = { viewModel.setScreensaverMode(mode) },
                    headlineContent = { Text(label) },
                    trailingContent = { if (screensaverMode == mode) Icon(Icons.Default.GraphicEq, contentDescription = null, tint = MaterialTheme.colorScheme.primary) })
            }
        }
        item { Text("Idle Timeout", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp), color = MaterialTheme.colorScheme.primary) }
        listOf(1, 5, 10, 20, 30).forEach { minutes ->
            item {
                ListItem(selected = screensaverTimeout == minutes, onClick = { viewModel.setScreensaverTimeout(minutes); viewModel.setSettingsSubMenu(null) },
                    headlineContent = { Text("$minutes Minutes") },
                    trailingContent = { if (screensaverTimeout == minutes) Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary) })
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvAppThemeSettings(viewModel: MainViewModel, appTheme: AppTheme, subMenuFocusRequester: FocusRequester) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(start = 32.dp, end = 32.dp, top = 24.dp, bottom = 170.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = { viewModel.setSettingsSubMenu(null) }, modifier = Modifier.focusRequester(subMenuFocusRequester)) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_desc)) }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Application Theme", style = MaterialTheme.typography.headlineMedium)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
        items(AppTheme.entries) { theme ->
            val label = when (theme) { AppTheme.RetroGold -> "Retro Gold"; AppTheme.BlueNeon -> "Blue Neon"; AppTheme.Violet -> "Violet"; AppTheme.Monochrome -> "Monochrome"; AppTheme.Forest -> "Forest"; AppTheme.Contrast -> "Contrast"; AppTheme.Black -> "Black" }
            val desc = when (theme) { AppTheme.RetroGold -> "Classic gold and wood tones"; AppTheme.BlueNeon -> "Cool blue neon glow"; AppTheme.Violet -> "Purple and violet tones"; AppTheme.Monochrome -> "Grey and black"; AppTheme.Forest -> "Green and black"; AppTheme.Contrast -> "White and black"; AppTheme.Black -> "Pure black background" }
            ListItem(selected = appTheme == theme, onClick = { viewModel.setAppTheme(theme) },
                headlineContent = { Text(label) }, supportingContent = { Text(desc) },
                trailingContent = { if (appTheme == theme) Icon(Icons.Default.GraphicEq, contentDescription = null, tint = MaterialTheme.colorScheme.primary) })
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvAppLanguageSettings(viewModel: MainViewModel, currentLang: AppLanguage, subMenuFocusRequester: FocusRequester) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(start = 32.dp, end = 32.dp, top = 24.dp, bottom = 170.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = { viewModel.setSettingsSubMenu(null) }, modifier = Modifier.focusRequester(subMenuFocusRequester)) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_desc)) }
                Spacer(modifier = Modifier.width(16.dp))
                Text(stringResource(R.string.settings_language), style = MaterialTheme.typography.headlineMedium)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
        items(AppLanguage.entries) { lang ->
            ListItem(selected = currentLang == lang, onClick = { viewModel.setAppLanguage(lang) },
                headlineContent = { Text(lang.displayName) },
                trailingContent = { if (currentLang == lang) Icon(Icons.Default.GraphicEq, contentDescription = null, tint = MaterialTheme.colorScheme.primary) })
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvSearchScreen(viewModel: MainViewModel, onLongClick: (Station) -> Unit = {}, onTagGroupLongClick: ((String) -> Unit)? = null) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val stations by viewModel.stations.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchMode by viewModel.searchMode.collectAsState()
    val tagSearchGroups by viewModel.tagSearchGroups.collectAsState()
    val selectedSearchTag by viewModel.selectedSearchTag.collectAsState()
    val selectedBitrates by viewModel.selectedBitrates.collectAsState()
    val hasMoreStations by viewModel.hasMoreStations.collectAsState()
    val searchFocusTrigger by viewModel.searchFocusTrigger.collectAsState()
    val searchFieldFocusRequester = remember { FocusRequester() }
    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current
    val searchTriggered = remember { mutableIntStateOf(0) }
    var localSearchQuery by remember { mutableStateOf(searchQuery) }
    var isSearchFieldFocused by remember { mutableStateOf(false) }

    val prevSelectedSearchTag = remember { mutableStateOf(selectedSearchTag) }
    val isReturning = selectedSearchTag == null && prevSelectedSearchTag.value != null
    LaunchedEffect(selectedSearchTag) { prevSelectedSearchTag.value = selectedSearchTag }

    LaunchedEffect(searchQuery) { if (searchQuery != localSearchQuery) localSearchQuery = searchQuery }

    Column(modifier = Modifier.fillMaxSize().padding(start = 12.dp, end = 32.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(value = localSearchQuery, onValueChange = { viewModel.onSearchQueryChange(it); localSearchQuery = it }, label = { Text("Search stations...") },
                modifier = Modifier.weight(1f).padding(bottom = 16.dp).focusRequester(searchFieldFocusRequester).onFocusChanged { isSearchFieldFocused = it.isFocused },
                singleLine = true, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search), keyboardActions = KeyboardActions(onSearch = { searchTriggered.intValue++; viewModel.addToRecentSearches(localSearchQuery); keyboardController?.hide() }),
                colors = TextFieldDefaults.colors(focusedTextColor = MaterialTheme.colorScheme.onSurface, unfocusedTextColor = MaterialTheme.colorScheme.onSurface, focusedLabelColor = MaterialTheme.colorScheme.primary, unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant, focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent))
            Button(onClick = { viewModel.toggleSearchMode() }, modifier = Modifier.padding(bottom = 16.dp),
                colors = if (searchMode == SearchMode.Tag) ButtonDefaults.colors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary) else ButtonDefaults.colors()
            ) { Text(if (searchMode == SearchMode.Tag) "TAG" else "NAME") }
        }
        if (isLoading) Text("Searching...", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 4.dp))
        TvBitrateFilters(selectedBitrates = selectedBitrates, onToggleFilter = { viewModel.toggleBitrateFilter(it) })
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.weight(1f)) {
            if (stations.isEmpty() && localSearchQuery.isEmpty() && tagSearchGroups.isEmpty()) {
                if (recentSearches.isNotEmpty()) {
                    Column { Text("Recent Searches", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
                        LazyRow { items(recentSearches) { query ->
                            Button(onClick = { searchTriggered.intValue++; viewModel.onSearchQueryChange(query); keyboardController?.hide() }, modifier = Modifier.padding(end = 8.dp)) { Text(query) }
                        } }
                    }
                } else Text("Enter search query", modifier = Modifier.padding(top = 16.dp))
            } else if (searchMode == SearchMode.Tag && tagSearchGroups.isNotEmpty() && selectedSearchTag == null) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(text = "Found ${stations.size} stations \u2014 select a tag to filter", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Box(modifier = Modifier.weight(1f)) {
                        GenreGroupGrid(groups = tagSearchGroups, autoFocus = isReturning, onGroupClick = { viewModel.selectSearchTag(it) }, onGroupLongClick = onTagGroupLongClick)
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    if (selectedSearchTag != null) {
                        Row(modifier = Modifier.padding(bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(text = selectedSearchTag!!.lowercase().split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "(${stations.size} stations)", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        StationGrid(stations = stations, viewModel = viewModel, autoFocus = selectedSearchTag != null, onLongClick = onLongClick, onLoadMore = if (hasMoreStations) {{ viewModel.loadMoreStations() }} else null)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class, UnstableApi::class)
@Composable
fun TvScreensaver(viewModel: MainViewModel) {
    val currentStation by viewModel.currentStation.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val playbackTime by viewModel.playbackTime.collectAsState()
    val screensaverMode by viewModel.screensaverMode.collectAsState()
    val audioFormat by viewModel.audioFormat.collectAsState()
    val mediaMetadata by viewModel.mediaMetadata.collectAsState()
    val focusRequester = remember { FocusRequester() }

    val infiniteTransition = rememberInfiniteTransition(label = "Bouncing")
    val xOffset by infiniteTransition.animateFloat(initialValue = -0.2f, targetValue = 0.2f,
        animationSpec = infiniteRepeatable(tween(15000, easing = LinearEasing), RepeatMode.Reverse), label = "x")
    val yOffset by infiniteTransition.animateFloat(initialValue = -0.2f, targetValue = 0.2f,
        animationSpec = infiniteRepeatable(tween(11000, easing = LinearEasing), RepeatMode.Reverse), label = "y")

    LaunchedEffect(Unit) { try { focusRequester.requestFocus() } catch (_: Exception) {} }

    Surface(modifier = Modifier.fillMaxSize().focusRequester(focusRequester).focusable().onKeyEvent { viewModel.resetScreensaverTimer(); true },
        colors = SurfaceDefaults.colors(containerColor = Color.Black)
    ) {
        if (screensaverMode == ScreensaverMode.StationInfo) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(modifier = Modifier.fillMaxWidth(0.8f).align(Alignment.Center).offset(x = (xOffset * 400).dp, y = (yOffset * 200).dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    currentStation?.let { station ->
                        Box(contentAlignment = Alignment.Center) {
                            AsyncImage(model = if (station.favicon.isNotEmpty()) station.favicon else R.drawable.ic_radio_logo, contentDescription = null, modifier = Modifier.size(260.dp), contentScale = ContentScale.Fit)
                            val code = station.countryCode?.trim()?.lowercase()
                            if (!code.isNullOrEmpty() && code.length == 2) {
                                AsyncImage(model = "https://flagcdn.com/w80/$code.png", contentDescription = null, modifier = Modifier.size(48.dp).align(Alignment.TopStart).offset(x = (-20).dp, y = (-10).dp), contentScale = ContentScale.Fit)
                            }
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                        val technicalInfo = audioFormat?.let { format ->
                            listOfNotNull(
                                format.sampleMimeType?.removePrefix("audio/")?.uppercase()?.replace("MPEG", "MP3")?.replace("MP4A-LATM", "AAC") ?: station.codec.orEmpty().uppercase(),
                                if (format.bitrate > 0) "${format.bitrate / 1000}k" else "${station.bitrate}k",
                                if (format.sampleRate > 0) "${format.sampleRate / 1000}kHz" else "",
                                when (format.channelCount) { 1 -> "Mono"; 2 -> "Stereo"; in 3..8 -> "${format.channelCount}ch"; else -> "" }
                            ).joinToString(" ")
                        } ?: "${station.bitrate}k"
                        Surface(shape = MaterialTheme.shapes.extraSmall, colors = SurfaceDefaults.colors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)), modifier = Modifier.padding(bottom = 8.dp)) {
                            Text(text = technicalInfo, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                        val displayTitle = if (!mediaMetadata?.title.isNullOrEmpty()) mediaMetadata?.title.toString() else station.name
                        Text(text = displayTitle, style = MaterialTheme.typography.displayMedium, color = Color.White, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Bold)
                        if (!mediaMetadata?.artist.isNullOrEmpty()) {
                            Text(text = mediaMetadata?.artist.toString(), style = MaterialTheme.typography.headlineMedium, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 4.dp))
                        }
                        val timeStr = String.format(Locale.getDefault(), "%02d:%02d", (playbackTime / 1000) / 60, (playbackTime / 1000) % 60)
                        Text(text = if (isPlaying) "Playing \u2022 $timeStr" else "Paused \u2022 $timeStr", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), modifier = Modifier.padding(top = 8.dp))
                        Spacer(modifier = Modifier.height(24.dp))
                        WaveformAnalyzer(isPlaying = isPlaying)
                    }
                }
                Text(text = "Press any key to return", style = MaterialTheme.typography.labelMedium, color = Color.Gray.copy(alpha = 0.5f), modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp))
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvFilePicker(
    state: MainViewModel.FilePickerState,
    onNavigate: (File) -> Unit,
    onNavigateUp: () -> Unit,
    onSelected: (File) -> Unit,
    onDismiss: () -> Unit
) {
    val folderIconColor = Color(0xFFFFCA28)
    Dialog(onDismissRequest = onDismiss) {
        Surface(modifier = Modifier.fillMaxWidth(0.85f).fillMaxHeight(0.85f), shape = MaterialTheme.shapes.large,
            colors = SurfaceDefaults.colors(containerColor = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.onSurface),
            border = androidx.tv.material3.Border(border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)), shape = MaterialTheme.shapes.large)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
                    Icon(if (state.isExport) Icons.Default.CreateNewFolder else Icons.Default.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(if (state.isExport) "Backup Favourites" else "Restore Favourites", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Button(onClick = onDismiss) { Text("Close") }
                }
                Surface(colors = SurfaceDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)), shape = MaterialTheme.shapes.small, modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(14.dp), tint = folderIconColor)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(state.currentPath, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(modifier = Modifier.weight(1f), contentPadding = PaddingValues(bottom = 16.dp)) {
                    item {
                        ListItem(selected = false, onClick = onNavigateUp, headlineContent = { Text("..", fontWeight = FontWeight.Bold) }, supportingContent = { Text("Go to Parent Directory") }, leadingContent = { Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, tint = folderIconColor) })
                    }
                    items(state.files) { file ->
                        val isPlaylist = file.name.lowercase().let { it.endsWith(".m3u") || it.endsWith(".m3u8") || it.endsWith(".pls") || it.endsWith(".txt") }
                        ListItem(selected = false, onClick = { if (file.isDirectory) onNavigate(file) else onSelected(file) }, enabled = true,
                            headlineContent = { Text(file.name, fontWeight = if (file.isDirectory) FontWeight.Bold else FontWeight.Normal) },
                            leadingContent = { Icon(if (file.isDirectory) Icons.Default.Folder else Icons.AutoMirrored.Filled.InsertDriveFile, contentDescription = null, tint = if (file.isDirectory) folderIconColor else if (isPlaylist) MaterialTheme.colorScheme.primary else Color.Gray) },
                            supportingContent = { if (file.isDirectory) Text("Folder") else { val size = file.length(); Text(if (size > 1024 * 1024) "${size / (1024 * 1024)} MB" else "${size / 1024} KB") } }
                        )
                    }
                }
                if (state.isExport) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(colors = SurfaceDefaults.colors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)), shape = MaterialTheme.shapes.medium, modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) { Text("Export filename:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary); Text(state.suggestedFileName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold) }
                            Button(onClick = { onSelected(File(state.currentPath)) }, colors = ButtonDefaults.colors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)) { Text("SAVE HERE") }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class, UnstableApi::class)
@Composable
fun TvNowPlayingBar(
    station: Station,
    isPlaying: Boolean,
    isFavorite: Boolean,
    playbackTime: Long,
    playbackDuration: Long,
    mediaMetadata: androidx.media3.common.MediaMetadata?,
    audioFormat: androidx.media3.common.Format?,
    onTogglePlay: () -> Unit,
    onToggleFavorite: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxWidth().height(115.dp),
        colors = SurfaceDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f)), shape = RectangleShape
    ) {
        Row(modifier = Modifier.padding(horizontal = 24.dp).fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            Row(modifier = Modifier.weight(1.2f), verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = MaterialTheme.shapes.small, modifier = Modifier.size(60.dp), colors = SurfaceDefaults.colors(containerColor = Color.White.copy(alpha = 0.1f))) {
                    AsyncImage(model = if (station.favicon.isNotEmpty()) station.favicon else R.drawable.ic_radio_logo, contentDescription = null, modifier = Modifier.fillMaxSize().padding(8.dp), contentScale = ContentScale.Fit)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    val technicalInfo = audioFormat?.let { format ->
                        listOfNotNull(
                            format.sampleMimeType?.removePrefix("audio/")?.uppercase()?.replace("MPEG", "MP3")?.replace("MP4A-LATM", "AAC") ?: station.codec.orEmpty().uppercase(),
                            if (format.bitrate > 0) "${format.bitrate / 1000}k" else "${station.bitrate}k",
                            if (format.sampleRate > 0) "${format.sampleRate / 1000}kHz" else "",
                            when (format.channelCount) { 1 -> "Mono"; 2 -> "Stereo"; in 3..8 -> "${format.channelCount}ch"; else -> "" }
                        ).joinToString(" ")
                    } ?: "${station.bitrate}k"
                    Surface(shape = MaterialTheme.shapes.extraSmall, colors = SurfaceDefaults.colors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)), modifier = Modifier.padding(bottom = 4.dp)) {
                        Text(text = technicalInfo, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                    }
                    val displayTitle = if (!mediaMetadata?.title.isNullOrEmpty()) mediaMetadata?.title.toString() else station.name
                    val displaySubtitle = if (!mediaMetadata?.artist.isNullOrEmpty()) mediaMetadata?.artist.toString() else station.country
                    Text(text = displayTitle, style = MaterialTheme.typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (mediaMetadata?.artist.isNullOrEmpty()) {
                            val code = station.countryCode?.trim()?.lowercase()
                            if (!code.isNullOrEmpty() && code.length == 2) {
                                AsyncImage(model = "https://flagcdn.com/w40/$code.png", contentDescription = null, modifier = Modifier.size(24.dp).padding(end = 8.dp), contentScale = ContentScale.Fit)
                            }
                        }
                        Text(text = displaySubtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        if (!mediaMetadata?.title.isNullOrEmpty()) {
                            Text(text = " \u2022 ${station.name}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                val timeStr = String.format(Locale.getDefault(), "%02d:%02d", (playbackTime / 1000) / 60, (playbackTime / 1000) % 60)
                Text(text = timeStr, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 16.dp))
                Card(onClick = onPrevious, modifier = Modifier.padding(horizontal = 4.dp)) { Icon(Icons.Default.SkipPrevious, contentDescription = stringResource(R.string.previous_desc), modifier = Modifier.padding(10.dp).size(24.dp)) }
                Card(onClick = onTogglePlay, modifier = Modifier.padding(horizontal = 4.dp), colors = CardDefaults.colors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)) {
                    Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = if (isPlaying) stringResource(R.string.pause_desc) else stringResource(R.string.play_desc), modifier = Modifier.padding(12.dp).size(28.dp))
                }
                Card(onClick = onNext, modifier = Modifier.padding(horizontal = 4.dp)) { Icon(Icons.Default.SkipNext, contentDescription = stringResource(R.string.next_desc), modifier = Modifier.padding(10.dp).size(24.dp)) }
                Spacer(modifier = Modifier.width(8.dp))
                Card(onClick = onToggleFavorite, modifier = Modifier.padding(horizontal = 4.dp)) {
                    Icon(if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = stringResource(R.string.favorite_desc), modifier = Modifier.padding(10.dp).size(24.dp), tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                }
            }
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) { WaveformAnalyzer(isPlaying = isPlaying) }
        }
        if (playbackDuration > 0) {
            LinearProgressIndicator(progress = { playbackTime.toFloat() / playbackDuration.toFloat() }, modifier = Modifier.fillMaxWidth().height(2.dp).align(Alignment.BottomCenter), color = MaterialTheme.colorScheme.primary, trackColor = Color.Transparent)
        }
    }
}

@Composable
fun SplashScreen() {
    val iconAlpha = remember { androidx.compose.animation.core.Animatable(0f) }
    val iconScale = remember { androidx.compose.animation.core.Animatable(0.6f) }
    val glowAlpha = remember { androidx.compose.animation.core.Animatable(0f) }
    val titleOffset = remember { androidx.compose.animation.core.Animatable(40f) }
    val titleAlpha = remember { androidx.compose.animation.core.Animatable(0f) }
    val subAlpha = remember { androidx.compose.animation.core.Animatable(0f) }
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseGlow = infiniteTransition.animateFloat(initialValue = 0.3f, targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse), label = "pulse")

    LaunchedEffect(Unit) {
        iconAlpha.animateTo(1f, animationSpec = tween(400))
        iconScale.animateTo(1f, animationSpec = tween(500, easing = FastOutLinearInEasing))
        glowAlpha.animateTo(1f, animationSpec = tween(700))
        titleOffset.animateTo(0f, animationSpec = tween(500, easing = FastOutLinearInEasing))
        titleAlpha.animateTo(1f, animationSpec = tween(300))
        delay(100)
        subAlpha.animateTo(1f, animationSpec = tween(400))
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1a1a1a)), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.size(220.dp).graphicsLayer { alpha = glowAlpha.value * pulseGlow.value }.background(Color(0xFFFBC02D).copy(alpha = 0.08f), CircleShape))
                Box(modifier = Modifier.size(160.dp).graphicsLayer { alpha = glowAlpha.value * pulseGlow.value * 0.5f }.background(Color(0xFFFBC02D).copy(alpha = 0.05f), CircleShape))
                Icon(
                    painter = androidx.compose.ui.res.painterResource(R.drawable.ic_radio_logo),
                    contentDescription = "Pure Radio",
                    modifier = Modifier.size(130.dp).graphicsLayer { alpha = iconAlpha.value; scaleX = iconScale.value; scaleY = iconScale.value },
                    tint = Color.Unspecified
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "Pure Radio", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = Color(0xFFFBC02D),
                modifier = Modifier.graphicsLayer { alpha = titleAlpha.value; translationY = titleOffset.value })
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Thousands of stations, free", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF8D6E63),
                modifier = Modifier.graphicsLayer { alpha = subAlpha.value })
        }
    }
}

@Composable
fun GenreGroupCard(group: GenreGroup, onClick: () -> Unit, onLongClick: (() -> Unit)? = null, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val isTv = (context as? MainActivity)?.isTv() == true
    if (isTv) TvGenreGroupCard(group, onClick, onLongClick, modifier)
    else PhoneGenreGroupCard(group, onClick, onLongClick)
}

@Composable
fun GenreGroupCardContent(group: GenreGroup) {
    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(model = getGenreImageUrl(group.genreName), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop, alpha = 0.4f)
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)))))
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(text = group.genreName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Color.White, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(4.dp))
            Surface(shape = MaterialTheme.shapes.extraSmall, colors = SurfaceDefaults.colors(containerColor = Color.White.copy(alpha = 0.2f))) {
                Text(text = "${group.filteredCount} / ${group.totalStations}", style = MaterialTheme.typography.labelSmall, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
            }
        }
    }
}

@Composable
fun StationCard(station: Station, isFavorite: Boolean, isCurrent: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit, onLongClick: () -> Unit = {}) {
    val context = LocalContext.current
    val isTv = (context as? MainActivity)?.isTv() == true
    if (isTv) TvStationCard(station, isFavorite, isCurrent, modifier, onClick, onLongClick)
    else PhoneStationCard(station, isFavorite, isCurrent, onClick, onLongClick)
}

@Composable
fun StationCardContent(station: Station, isFavorite: Boolean, isCurrent: Boolean) {
    Column(modifier = Modifier.padding(12.dp).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
        Box(contentAlignment = Alignment.Center) {
            Surface(shape = MaterialTheme.shapes.small, modifier = Modifier.size(80.dp), colors = SurfaceDefaults.colors(containerColor = Color.White.copy(alpha = 0.05f))) {
                AsyncImage(model = if (station.favicon.isNotEmpty()) station.favicon else R.drawable.ic_radio_logo, contentDescription = null, modifier = Modifier.fillMaxSize().padding(12.dp), contentScale = ContentScale.Fit, error = coil.compose.rememberAsyncImagePainter(R.drawable.ic_radio_logo))
            }
            val code = station.countryCode?.trim()?.lowercase()
            if (!code.isNullOrEmpty() && code.length == 2) {
                AsyncImage(model = "https://flagcdn.com/w40/$code.png", contentDescription = null, modifier = Modifier.size(24.dp).align(Alignment.TopStart).padding(4.dp), contentScale = ContentScale.Fit)
            }
            if (isFavorite) Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(20.dp).align(Alignment.TopEnd).padding(4.dp), tint = MaterialTheme.colorScheme.primary)
            if (isCurrent) Icon(Icons.Default.GraphicEq, contentDescription = null, modifier = Modifier.size(24.dp).align(Alignment.BottomEnd).padding(4.dp), tint = MaterialTheme.colorScheme.primary)
        }
        Text(text = station.name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal, textAlign = TextAlign.Center)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = station.votes.toString(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
        }
    }
}

fun getGenreColor(genre: String): Color {
    val hash = genre.hashCode()
    return Color((Math.abs(hash) % 100) + 20, (Math.abs(hash shr 8) % 100) + 20, (Math.abs(hash shr 16) % 100) + 20)
}

fun getGenreImageUrl(genre: String): String {
    val g = genre.lowercase().trim()
    return when {
        g.contains("heavy metal") -> "https://images.unsplash.com/photo-1541614101331-1a5a3a194e90?q=80&w=600&auto=format&fit=crop"
        g.contains("metal") -> "https://images.unsplash.com/photo-1598387181032-a3103a2db5b3?q=80&w=600&auto=format&fit=crop"
        g.contains("punk") -> "https://images.unsplash.com/photo-1583790155708-360d8a5563c0?q=80&w=600&auto=format&fit=crop"
        g.contains("hard rock") -> "https://images.unsplash.com/photo-1521334885634-9552f1055677?q=80&w=600&auto=format&fit=crop"
        g.contains("classic rock") -> "https://images.unsplash.com/photo-1459749411177-042180ce673c?q=80&w=600&auto=format&fit=crop"
        g.contains("rock") -> "https://images.unsplash.com/photo-1498038432885-c6f3f1b912ee?q=80&w=600&auto=format&fit=crop"
        g.contains("alternative") || g.contains("indie") -> "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?q=80&w=600&auto=format&fit=crop"
        g.contains("synthpop") -> "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?q=80&w=600&auto=format&fit=crop"
        g.contains("pop") || g.contains("hits") || g.contains("top") || g.contains("chart") -> "https://images.unsplash.com/photo-1514525253361-bee8a187449a?q=80&w=600&auto=format&fit=crop"
        g.contains("smooth jazz") -> "https://images.unsplash.com/photo-1525994886773-080587e161c3?q=80&w=600&auto=format&fit=crop"
        g.contains("jazz") -> "https://images.unsplash.com/photo-1511192336575-5a79af67a629?q=80&w=600&auto=format&fit=crop"
        g.contains("blues") -> "https://images.unsplash.com/photo-1553034545-31a386996173?q=80&w=600&auto=format&fit=crop"
        g.contains("soul") -> "https://images.unsplash.com/photo-1460723237483-7a6dc9d0b212?q=80&w=600&auto=format&fit=crop"
        g.contains("funk") || g.contains("disco") -> "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?q=80&w=600&auto=format&fit=crop"
        g.contains("orchestra") || g.contains("symphony") -> "https://images.unsplash.com/photo-1465847899035-1379e576ee5d?q=80&w=600&auto=format&fit=crop"
        g.contains("classical") || g.contains("classic") -> "https://images.unsplash.com/photo-1507838596018-b943e1dd13a9?q=80&w=600&auto=format&fit=crop"
        g.contains("opera") -> "https://images.unsplash.com/photo-1520529125433-21950920427e?q=80&w=600&auto=format&fit=crop"
        g.contains("techno") -> "https://images.unsplash.com/photo-1571266028243-3716f02d2d2e?q=80&w=600&auto=format&fit=crop"
        g.contains("deep house") -> "https://images.unsplash.com/photo-1493225255756-d9584f8606e9?q=80&w=600&auto=format&fit=crop"
        g.contains("house") -> "https://images.unsplash.com/photo-1557683316-973673baf926?q=80&w=600&auto=format&fit=crop"
        g.contains("trance") -> "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?q=80&w=600&auto=format&fit=crop"
        g.contains("psytrance") -> "https://images.unsplash.com/photo-1520092352425-9fae9f057c9a?q=80&w=600&auto=format&fit=crop"
        g.contains("electro") || g.contains("edm") -> "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?q=80&w=600&auto=format&fit=crop"
        g.contains("ambient") || g.contains("lofi") -> "https://images.unsplash.com/photo-1516280440614-37939bbacd81?q=80&w=600&auto=format&fit=crop"
        g.contains("chillout") || g.contains("chill") -> "https://images.unsplash.com/photo-1519681393784-d120267933ba?q=80&w=600&auto=format&fit=crop"
        g.contains("lounge") -> "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?q=80&w=600&auto=format&fit=crop"
        g.contains("country") -> "https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?q=80&w=600&auto=format&fit=crop"
        g.contains("bluegrass") -> "https://images.unsplash.com/photo-1525201548942-d8b8c09ec8d1?q=80&w=600&auto=format&fit=crop"
        g.contains("folk") -> "https://images.unsplash.com/photo-1468164016595-6108e4c60c8b?q=80&w=600&auto=format&fit=crop"
        g.contains("hip hop") -> "https://images.unsplash.com/photo-1520262454473-a1a82276a574?q=80&w=600&auto=format&fit=crop"
        g.contains("rap") || g.contains("urban") || g.contains("r&b") -> "https://images.unsplash.com/photo-1524368535928-5b5e00ddc76b?q=80&w=600&auto=format&fit=crop"
        g.contains("reggae") -> "https://images.unsplash.com/photo-1510915228340-29c85a43dcfe?q=80&w=600&auto=format&fit=crop"
        g.contains("ska") -> "https://images.unsplash.com/photo-1461896836934-ffe607ba8211?q=80&w=600&auto=format&fit=crop"
        g.contains("world") -> "https://images.unsplash.com/photo-1526218626217-dc65a29bb444?q=80&w=600&auto=format&fit=crop"
        g.contains("latin") -> "https://images.unsplash.com/photo-1525994886773-080587e161c3?q=80&w=600&auto=format&fit=crop"
        g.contains("80s") -> "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?q=80&w=600&auto=format&fit=crop"
        g.contains("90s") -> "https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?q=80&w=600&auto=format&fit=crop"
        g.contains("70s") -> "https://images.unsplash.com/photo-1516062423079-7ca13cdc7f5a?q=80&w=600&auto=format&fit=crop"
        g.contains("60s") || g.contains("oldies") || g.contains("retro") -> "https://images.unsplash.com/photo-1484755560615-a4c64e99529b?q=80&w=600&auto=format&fit=crop"
        g.contains("soundtrack") || g.contains("movie") || g.contains("film") -> "https://images.unsplash.com/photo-1485846234645-a62644f84728?q=80&w=600&auto=format&fit=crop"
        g.contains("meditation") || g.contains("spiritual") || g.contains("religious") -> "https://images.unsplash.com/photo-1506126613408-eca07ce68773?q=80&w=600&auto=format&fit=crop"
        g.contains("news") || g.contains("talk") || g.contains("info") -> "https://images.unsplash.com/photo-1472289065668-ce650ac443d2?q=80&w=600&auto=format&fit=crop"
        g.contains("sport") -> "https://images.unsplash.com/photo-1461896836934-ffe607ba8211?q=80&w=600&auto=format&fit=crop"
        g.contains("comedy") -> "https://images.unsplash.com/photo-1527224857830-43a7acc85260?q=80&w=600&auto=format&fit=crop"
        g.contains("christmas") || g.contains("xmas") -> "https://images.unsplash.com/photo-1543589077-47d81606c1bf?q=80&w=600&auto=format&fit=crop"
        g.contains("kids") || g.contains("children") -> "https://images.unsplash.com/photo-1516627145497-ae6968895b74?q=80&w=600&auto=format&fit=crop"
        else -> "https://images.unsplash.com/photo-1453090927415-5f45085b65c0?q=80&w=600&auto=format&fit=crop"
    }
}

@Composable
fun BitrateFilters(selectedBitrates: Set<BitrateFilter>, onToggleFilter: (BitrateFilter) -> Unit) {
    val context = LocalContext.current
    val isTv = (context as? MainActivity)?.isTv() == true
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
        Text("Filter: ", style = MaterialTheme.typography.labelLarge)
        BitrateFilter.entries.forEach { filter ->
            val label = when (filter) { BitrateFilter.Low -> "Low"; BitrateFilter.High -> "High"; BitrateFilter.FLAC -> "FLAC" }
            val isSelected = selectedBitrates.contains(filter)
            if (isTv) {
                Button(onClick = { onToggleFilter(filter) }, modifier = Modifier.padding(horizontal = 4.dp),
                    scale = if (isSelected) ButtonDefaults.scale(focusedScale = 1.1f) else ButtonDefaults.scale(),
                    colors = if (isSelected) ButtonDefaults.colors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary) else ButtonDefaults.colors()
                ) { Text(label) }
            } else {
                androidx.compose.material3.FilterChip(selected = isSelected, onClick = { onToggleFilter(filter) }, label = { androidx.compose.material3.Text(label) }, modifier = Modifier.padding(horizontal = 4.dp))
            }
        }
    }
}
