package com.toxa.pureradio.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import com.toxa.pureradio.ui.theme.paletteFor
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.Format
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import com.toxa.pureradio.BuildConfig
import com.toxa.pureradio.R
import com.toxa.pureradio.data.model.Station
import com.toxa.pureradio.network.Country
import com.toxa.pureradio.network.Tag
import com.toxa.pureradio.ui.viewmodel.AppTheme
import com.toxa.pureradio.ui.viewmodel.BitrateFilter
import com.toxa.pureradio.ui.viewmodel.GenreGroup
import com.toxa.pureradio.ui.viewmodel.GenreSortMode
import com.toxa.pureradio.ui.viewmodel.AppLanguage
import com.toxa.pureradio.ui.viewmodel.MainViewModel
import com.toxa.pureradio.ui.viewmodel.NavigationItem
import com.toxa.pureradio.ui.viewmodel.ScreensaverMode
import com.toxa.pureradio.ui.viewmodel.SearchMode
import kotlinx.coroutines.delay
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneMainScreen(viewModel: MainViewModel) {
    val selectedNavItem by viewModel.selectedNavItem.collectAsState()
    val stations by viewModel.stations.collectAsState()
    val genreGroups by viewModel.genreGroups.collectAsState()
    val tags by viewModel.tags.collectAsState()
    val countries by viewModel.countries.collectAsState()
    val currentStation by viewModel.currentStation.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val selectedTag by viewModel.selectedTag.collectAsState()
    val selectedCountry by viewModel.selectedCountry.collectAsState()
    val selectedSearchTag by viewModel.selectedSearchTag.collectAsState()
    val selectedBitrates by viewModel.selectedBitrates.collectAsState()
    val hasMoreStations by viewModel.hasMoreStations.collectAsState()
    val settingsSubMenu by viewModel.settingsSubMenu.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val isScreensaverShowing by viewModel.isScreensaverShowing.collectAsState()

    val context = LocalContext.current
    var stationToFavorite by remember { mutableStateOf<Station?>(null) }

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

    Box(modifier = Modifier.fillMaxSize()) {
        when (selectedNavItem) {
            NavigationItem.Home -> PhoneHomeScreen(
                viewModel = viewModel,
                genreGroups = genreGroups,
                stations = stations,
                selectedTag = selectedTag,
                selectedCountry = selectedCountry,
                hasMoreStations = hasMoreStations,
                onStationLongClick = { stationToFavorite = it }
            )
            NavigationItem.Popular -> PhoneStationListScreen(
                title = stringResource(R.string.nav_popular),
                stations = stations,
                viewModel = viewModel,
                hasMoreStations = hasMoreStations,
                onLongClick = { stationToFavorite = it }
            )
            NavigationItem.Recent -> PhoneStationListScreen(
                title = stringResource(R.string.nav_recent),
                stations = stations,
                viewModel = viewModel,
                onLongClick = { stationToFavorite = it }
            )
            NavigationItem.Favourites -> PhoneStationListScreen(
                title = stringResource(R.string.nav_favourites),
                stations = stations,
                viewModel = viewModel,
                onLongClick = { stationToFavorite = it }
            )
            NavigationItem.Search -> PhoneSearchScreen(
                viewModel = viewModel,
                onLongClick = { stationToFavorite = it },
                onTagGroupLongClick = { tagName: String ->
                    viewModel.showGenreDialog(tagName)
                }
            )
            NavigationItem.Genres -> PhoneGenresScreen(
                viewModel = viewModel,
                tags = tags,
                filteredTags = viewModel.filteredTags.collectAsState().value,
                selectedTag = selectedTag,
                stations = stations,
                hasMoreStations = hasMoreStations,
                onStationLongClick = { stationToFavorite = it }
            )
            NavigationItem.Countries -> PhoneCountriesScreen(
                viewModel = viewModel,
                countries = countries,
                selectedCountry = selectedCountry,
                stations = stations,
                hasMoreStations = hasMoreStations,
                onStationLongClick = { stationToFavorite = it }
            )
            NavigationItem.Settings -> PhoneSettingsScreen(
                viewModel = viewModel,
                settingsSubMenu = settingsSubMenu,
                onImportPlaylist = {
                    try { importLauncher.launch(arrayOf("*/*")) }
                    catch (e: Exception) {
                        viewModel.setError(context.getString(R.string.error_system_picker_unavailable))
                        viewModel.openFilePicker(isExport = false)
                    }
                },
                onExportPlaylist = {
                    try { exportLauncher.launch("pure_radio_favorites.m3u") }
                    catch (e: Exception) {
                        viewModel.setError(context.getString(R.string.error_system_picker_unavailable))
                        viewModel.openFilePicker(isExport = true, suggestedFileName = "pure_radio_favorites.m3u")
                    }
                }
            )
            NavigationItem.Exit -> {}
        }

        if (isLoading && selectedNavItem != NavigationItem.Search) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.55f)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 28.dp, vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        MiniEqualizer(isPlaying = true, modifier = Modifier.size(width = 36.dp, height = 28.dp), bars = 4)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.searching_for_waves),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        LinearProgressIndicator(
                            modifier = Modifier
                                .width(180.dp)
                                .height(3.dp)
                                .clip(CircleShape),
                            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                        )
                    }
                }
            }
        }

        error?.let { message ->
            LaunchedEffect(error) {
                delay(5000)
                viewModel.clearError()
            }
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                shadowElevation = 6.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .fillMaxWidth()
                    .clickable { viewModel.clearError() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }

    stationToFavorite?.let { station ->
        val isAlreadyFavorite = favorites.contains(station.stationUuid)
        AlertDialog(
            onDismissRequest = { stationToFavorite = null },
            icon = { Icon(if (isAlreadyFavorite) Icons.Default.FavoriteBorder else Icons.Default.Favorite, contentDescription = null) },
            title = { Text(if (isAlreadyFavorite) stringResource(R.string.fav_remove_title) else stringResource(R.string.fav_add_title)) },
            text = { Text(station.name) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.toggleFavorite(station)
                    stationToFavorite = null
                }) {
                    Text(if (isAlreadyFavorite) stringResource(R.string.fav_remove) else stringResource(R.string.fav_add))
                }
            },
            dismissButton = {
                TextButton(onClick = { stationToFavorite = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }


    viewModel.pendingImportStations.collectAsState().value?.let { pending ->
        var showReplaceConfirm by remember { mutableStateOf(false) }

        if (!showReplaceConfirm) {
            Dialog(onDismissRequest = { viewModel.cancelRestore() }) {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    tonalElevation = 6.dp,
                    modifier = Modifier.width(420.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.CloudDownload,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            stringResource(R.string.restore_title),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            stringResource(R.string.restore_found, pending.size),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TextButton(
                                onClick = { viewModel.confirmRestore(replace = false) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(stringResource(R.string.restore_add_current), fontWeight = FontWeight.Bold)
                            }
                            TextButton(
                                onClick = { showReplaceConfirm = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text(stringResource(R.string.restore_replace_btn), fontWeight = FontWeight.Bold)
                            }
                            TextButton(
                                onClick = { viewModel.cancelRestore() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(stringResource(R.string.cancel))
                            }
                        }
                    }
                }
            }
        } else {
            Dialog(onDismissRequest = { showReplaceConfirm = false }) {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    tonalElevation = 6.dp,
                    modifier = Modifier.width(420.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            stringResource(R.string.restore_confirm_title),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            stringResource(R.string.restore_confirm_body),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = { showReplaceConfirm = false }) {
                                Text(stringResource(R.string.no))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(
                                onClick = { viewModel.confirmRestore(replace = true) },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text(stringResource(R.string.restore_yes_replace), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    viewModel.filePickerState.collectAsState().value?.let { state ->
        PhoneFilePicker(
            state = state,
            onNavigate = { viewModel.navigateInFilePicker(it) },
            onNavigateUp = { viewModel.navigateUpFilePicker() },
            onSelected = { viewModel.handleFileSelection(it) },
            onDismiss = { viewModel.closeFilePicker() }
        )
    }

    viewModel.showGenreDialog.collectAsState().value?.let { genreName ->
        val countries by viewModel.countries.collectAsState()
        val group = genreGroups.find { it.genreName == genreName }
        val isCountry = group?.isCountry == true || countries.any { it.name.equals(genreName, ignoreCase = true) }
        val isOnHome by remember { derivedStateOf {
            if (isCountry) viewModel.visibleCountries.value.contains(genreName)
            else viewModel.visibleGenres.value.contains(genreName)
        } }
        
        AlertDialog(
            onDismissRequest = { viewModel.hideGenreDialog() },
            title = { Text(
                if (isOnHome) {
                    if (isCountry) stringResource(R.string.home_remove_title_country) else stringResource(R.string.home_remove_title_genre)
                } else {
                    if (isCountry) stringResource(R.string.home_add_title_country) else stringResource(R.string.home_add_title_genre)
                }
            ) },
            text = { Text(genreName.lowercase().split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }) },
            confirmButton = {
                TextButton(onClick = {
                    if (isCountry) viewModel.toggleCountryVisibility(genreName)
                    else viewModel.toggleGenreVisibility(genreName)
                    viewModel.hideGenreDialog()
                }) {
                    Text(if (isOnHome) stringResource(R.string.fav_remove) else stringResource(R.string.fav_add))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideGenreDialog() }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhoneHomeScreen(
    viewModel: MainViewModel,
    genreGroups: List<GenreGroup>,
    stations: List<Station>,
    selectedTag: Tag?,
    selectedCountry: Country?,
    hasMoreStations: Boolean,
    onStationLongClick: (Station) -> Unit
) {
    val selectedBitrates by viewModel.selectedBitrates.collectAsState()
    val tags by viewModel.tags.collectAsState()
    val countries by viewModel.countries.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        val title = when {
            selectedTag != null -> selectedTag.name.displayCase()
            selectedCountry != null -> selectedCountry.name.displayCase()
            else -> stringResource(R.string.nav_home)
        }
        val isDeepDive = selectedTag != null || selectedCountry != null

        PhoneScreenHeader(
            title = title,
            subtitle = if (isDeepDive) stringResource(R.string.stations_count, stations.size) else null,
            onBack = if (isDeepDive) {
                {
                    if (selectedTag != null) viewModel.selectTag(null) else viewModel.selectCountry(null)
                }
            } else null
        )

        PhoneBitrateFilters(
            selectedBitrates = selectedBitrates,
            onToggleFilter = { viewModel.toggleBitrateFilter(it) }
        )
        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.weight(1f)) {
            if (genreGroups.isNotEmpty()) {
                if (selectedTag == null && selectedCountry == null) {
                    key("phone_home_groups") {
                        PhoneGenreGroupGrid(
                            groups = genreGroups,
                            onGroupClick = { name ->
                                val group = genreGroups.find { it.genreName == name }
                                if (group?.isCountry == true) {
                                    val country = countries.find { it.name.equals(name, ignoreCase = true) }
                                        ?: Country(name = name, iso_3166_1 = "", stationcount = 0)
                                    viewModel.selectCountry(country)
                                } else {
                                    val tag = tags.find { it.name.equals(name, ignoreCase = true) }
                                        ?: Tag(name = name, stationcount = group?.totalStations ?: 0)
                                    viewModel.selectTag(tag)
                                }
                            },
                             onGroupLongClick = { name ->
                                 val group = genreGroups.find { it.genreName == name }
                                 if (group != null) {
                                     viewModel.showGenreDialog(name)
                                 }
                             }
                        )
                    }
                } else {
                    key("phone_home_stations_${selectedTag?.name ?: selectedCountry?.name}") {
                        PhoneStationGrid(
                            stations = stations,
                            viewModel = viewModel,
                            onLoadMore = if (hasMoreStations) {{ viewModel.loadMoreStations() }} else null,
                            onLongClick = onStationLongClick
                        )
                    }
                }
            } else {
                key("phone_home_top") {
                    PhoneStationGrid(
                        stations = stations,
                        viewModel = viewModel,
                        onLoadMore = if (hasMoreStations) {{ viewModel.loadMoreStations() }} else null,
                        onLongClick = onStationLongClick
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhoneStationGrid(
    stations: List<Station>,
    viewModel: MainViewModel,
    onLoadMore: (() -> Unit)? = null,
    onLongClick: (Station) -> Unit = {}
) {
    val favorites by viewModel.favorites.collectAsState()
    val currentStation by viewModel.currentStation.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    if (stations.isEmpty()) {
        if (!isLoading) {
            PhoneEmptyState(
                icon = Icons.Default.Radio,
                message = stringResource(R.string.no_stations_found)
            )
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 156.dp),
        contentPadding = GridPadding,
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(GridGap),
        verticalArrangement = Arrangement.spacedBy(GridGap)
    ) {
        itemsIndexed(stations, key = { _, station -> station.stationUuid }) { _, station ->
            val isCurrent = currentStation?.stationUuid == station.stationUuid
            PhoneStationCard(
                station = station,
                isFavorite = favorites.contains(station.stationUuid),
                isCurrent = isCurrent,
                onClick = { viewModel.playStation(station, stations) },
                onLongClick = { onLongClick(station) },
                isPlaying = isCurrent && isPlaying
            )
        }

        if (onLoadMore != null) {
            item(key = "load_more", span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    FilledTonalButton(
                        onClick = { if (!isLoading) onLoadMore() },
                        enabled = !isLoading,
                        shape = CircleShape,
                        contentPadding = PaddingValues(horizontal = 28.dp, vertical = 12.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(stringResource(R.string.loading))
                        } else {
                            Text(stringResource(R.string.load_more))
                        }
                    }
                }
            }
        }
    }
}

/** 1234 -> "1.2K", 1_250_000 -> "1.3M". */
private fun compactCount(value: Int): String = when {
    value >= 1_000_000 -> String.format(Locale.US, "%.1fM", value / 1_000_000f).replace(".0M", "M")
    value >= 10_000 -> "${value / 1000}K"
    value >= 1_000 -> String.format(Locale.US, "%.1fK", value / 1000f).replace(".0K", "K")
    else -> value.toString()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhoneStationCard(
    station: Station,
    isFavorite: Boolean,
    isCurrent: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    isPlaying: Boolean = true
) {
    val shape = RoundedCornerShape(20.dp)
    val colors = MaterialTheme.colorScheme
    Surface(
        shape = shape,
        color = if (isCurrent) colors.primaryContainer.copy(alpha = 0.6f) else colors.surfaceContainer,
        border = BorderStroke(
            width = if (isCurrent) 1.5.dp else 1.dp,
            color = if (isCurrent) colors.primary.copy(alpha = 0.85f) else colors.outlineVariant.copy(alpha = 0.45f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(196.dp)
            .clip(shape)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                StationArtwork(
                    model = MediaUtils.getStationArtworkUrl(station.favicon, station.countryCode),
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = 16.dp
                )
                val code = station.countryCode?.trim()?.lowercase()
                if (!code.isNullOrEmpty() && code.length == 2) {
                    AsyncImage(
                        model = "https://flagcdn.com/w80/$code.png",
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .size(width = 22.dp, height = 16.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                if (isFavorite) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .size(26.dp)
                            .background(Color.Black.copy(alpha = 0.45f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = stringResource(R.string.favorite_desc),
                            modifier = Modifier.size(14.dp),
                            tint = colors.primary
                        )
                    }
                }
                if (isCurrent) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .size(30.dp)
                            .background(colors.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        MiniEqualizer(
                            isPlaying = isPlaying,
                            modifier = Modifier.size(width = 14.dp, height = 13.dp),
                            color = colors.onPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = station.name,
                style = MaterialTheme.typography.titleSmall,
                color = if (isCurrent) colors.primary else colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 2.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.padding(horizontal = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = null,
                    modifier = Modifier.size(11.dp),
                    tint = colors.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = compactCount(station.votes),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onSurfaceVariant
                )
                Spacer(modifier = Modifier.weight(1f))
                stationQualityLabel(station)?.let { quality ->
                    Text(
                        text = quality,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isCurrent) colors.primary else colors.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhoneGenreGroupGrid(
    groups: List<GenreGroup>,
    onGroupClick: (String) -> Unit,
    onGroupLongClick: ((String) -> Unit)? = null
) {
    if (groups.isEmpty()) {
        PhoneEmptyState(
            icon = Icons.AutoMirrored.Filled.List,
            message = stringResource(R.string.no_genres_selected)
        )
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 156.dp),
        contentPadding = GridPadding,
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(GridGap),
        verticalArrangement = Arrangement.spacedBy(GridGap)
    ) {
        itemsIndexed(groups, key = { _, group -> group.genreName }) { _, group ->
            PhoneGenreGroupCard(
                group = group.copy(genreName = group.genreName.displayCase()),
                onClick = { onGroupClick(group.genreName) },
                onLongClick = if (onGroupLongClick != null) {{ onGroupLongClick(group.genreName) }} else null
            )
        }
    }
}

/**
 * Photo tile used for genres, tags and countries: full-bleed image over the genre's
 * fallback colour, a bottom scrim for legibility and left-aligned title + caption.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PhonePhotoTile(
    title: String,
    caption: String?,
    imageUrl: String?,
    baseColor: Color,
    height: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    imageAlpha: Float = 0.75f,
    badge: (@Composable () -> Unit)? = null
) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .background(baseColor)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = imageAlpha
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Black.copy(alpha = 0.05f),
                        0.45f to Color.Black.copy(alpha = 0.15f),
                        1f to Color.Black.copy(alpha = 0.85f)
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, Color.White.copy(alpha = 0.08f), shape)
        )
        if (badge != null) {
            Box(modifier = Modifier.align(Alignment.TopStart).padding(10.dp)) {
                badge()
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(start = 14.dp, end = 14.dp, bottom = 12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (caption != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = caption,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.75f),
                    maxLines = 1
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhoneGenreGroupCard(group: GenreGroup, onClick: () -> Unit, onLongClick: (() -> Unit)? = null) {
    PhonePhotoTile(
        title = group.genreName,
        caption = "${group.filteredCount} / ${group.totalStations}",
        imageUrl = MediaUtils.getGenreImageUrl(group.genreName),
        baseColor = MediaUtils.getGenreColor(group.genreName),
        height = 132.dp,
        onClick = onClick,
        onLongClick = onLongClick
    )
}

fun getPhoneGenreColor(genre: String): Color = MediaUtils.getGenreColor(genre)

fun getPhoneGenreImageUrl(genre: String): String = MediaUtils.getGenreImageUrl(genre)

@Composable
fun PhoneStationListScreen(
    title: String,
    stations: List<Station>,
    viewModel: MainViewModel,
    hasMoreStations: Boolean = false,
    onLongClick: (Station) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        PhoneScreenHeader(
            title = title,
            subtitle = if (stations.isNotEmpty()) stringResource(R.string.stations_count, stations.size) else null
        )
        Box(modifier = Modifier.weight(1f)) {
            PhoneStationGrid(
                stations = stations,
                viewModel = viewModel,
                onLoadMore = if (hasMoreStations) {{ viewModel.loadMoreStations() }} else null,
                onLongClick = onLongClick
            )
        }
    }
}

@Composable
fun PhoneSearchScreen(
    viewModel: MainViewModel,
    onLongClick: (Station) -> Unit = {},
    onTagGroupLongClick: ((String) -> Unit)? = null
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val stations by viewModel.stations.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchMode by viewModel.searchMode.collectAsState()
    val tagSearchGroups by viewModel.tagSearchGroups.collectAsState()
    val selectedSearchTag by viewModel.selectedSearchTag.collectAsState()
    val selectedBitrates by viewModel.selectedBitrates.collectAsState()
    val hasMoreStations by viewModel.hasMoreStations.collectAsState()
    var localSearchQuery by remember { mutableStateOf(searchQuery) }
    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current

    LaunchedEffect(searchQuery) {
        if (searchQuery != localSearchQuery) {
            localSearchQuery = searchQuery
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            PhoneSearchField(
                value = localSearchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it); localSearchQuery = it },
                placeholder = stringResource(R.string.search_hint),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 10.dp),
                onSearch = {
                    viewModel.addToRecentSearches(localSearchQuery)
                    keyboardController?.hide()
                }
            )

            PhoneChipRow {
                PhoneChip(
                    selected = searchMode == SearchMode.Name,
                    label = stringResource(R.string.search_mode_name).displayCase(),
                    onClick = { if (searchMode != SearchMode.Name) viewModel.toggleSearchMode() }
                )
                PhoneChip(
                    selected = searchMode == SearchMode.Tag,
                    label = stringResource(R.string.search_mode_tag).displayCase(),
                    onClick = { if (searchMode != SearchMode.Tag) viewModel.toggleSearchMode() }
                )
                ChipDivider()
                BitrateChips(
                    selectedBitrates = selectedBitrates,
                    onToggleFilter = { viewModel.toggleBitrateFilter(it) }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.weight(1f)) {
                when {
                    stations.isEmpty() && localSearchQuery.isEmpty() && tagSearchGroups.isEmpty() -> {
                        if (recentSearches.isNotEmpty()) {
                            Column(modifier = Modifier.padding(top = 8.dp)) {
                                Text(
                                    stringResource(R.string.recent_searches).uppercase(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 1.2.sp,
                                    modifier = Modifier.padding(start = 20.dp, bottom = 8.dp)
                                )
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(recentSearches) { query ->
                                        AssistChip(
                                            onClick = {
                                                viewModel.onSearchQueryChange(query)
                                                keyboardController?.hide()
                                            },
                                            label = { Text(query, maxLines = 1) },
                                            leadingIcon = {
                                                Icon(
                                                    Icons.Default.History,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(AssistChipDefaults.IconSize)
                                                )
                                            },
                                            shape = CircleShape
                                        )
                                    }
                                }
                            }
                        } else {
                            PhoneEmptyState(
                                icon = Icons.Default.Search,
                                message = stringResource(R.string.enter_search_query)
                            )
                        }
                    }
                    searchMode == SearchMode.Tag && tagSearchGroups.isNotEmpty() && selectedSearchTag == null -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = stringResource(R.string.found_stations, stations.size),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                            )
                            PhoneTagSearchGroupGrid(
                                groups = tagSearchGroups,
                                onGroupClick = { viewModel.selectSearchTag(it) },
                                onGroupLongClick = onTagGroupLongClick
                            )
                        }
                    }
                    else -> {
                        PhoneStationGrid(
                            stations = stations,
                            viewModel = viewModel,
                            onLoadMore = if (hasMoreStations) {{ viewModel.loadMoreStations() }} else null,
                            onLongClick = onLongClick
                        )
                    }
                }
            }
        }

        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .align(Alignment.TopCenter),
                trackColor = Color.Transparent
            )
        }
    }
}

@Composable
fun PhoneTagSearchGroupGrid(
    groups: List<GenreGroup>,
    onGroupClick: (String) -> Unit,
    onGroupLongClick: ((String) -> Unit)? = null
) {
    if (groups.isEmpty()) {
        PhoneEmptyState(
            icon = Icons.Default.Search,
            message = stringResource(R.string.no_tag_groups_found)
        )
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 156.dp),
        contentPadding = GridPadding,
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(GridGap),
        verticalArrangement = Arrangement.spacedBy(GridGap)
    ) {
        itemsIndexed(groups, key = { _, group -> group.genreName }) { _, group ->
            PhoneGenreGroupCard(
                group = group.copy(genreName = group.genreName.displayCase()),
                onClick = { onGroupClick(group.genreName) },
                onLongClick = if (onGroupLongClick != null) {{ onGroupLongClick(group.genreName) }} else null
            )
        }
    }
}

@Composable
fun PhoneGenresScreen(
    viewModel: MainViewModel,
    tags: List<Tag>,
    filteredTags: List<Tag>,
    selectedTag: Tag?,
    stations: List<Station>,
    hasMoreStations: Boolean,
    onStationLongClick: (Station) -> Unit
) {
    val tagSearchQuery by viewModel.tagSearchQuery.collectAsState()
    val genreSortMode by viewModel.genreSortMode.collectAsState()
    var localTagSearchQuery by remember { mutableStateOf(tagSearchQuery) }
    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current

    LaunchedEffect(tagSearchQuery) {
        if (tagSearchQuery != localTagSearchQuery) {
            localTagSearchQuery = tagSearchQuery
        }
    }

    if (selectedTag != null) {
        Column(modifier = Modifier.fillMaxSize()) {
            PhoneScreenHeader(
                title = selectedTag.name.displayCase(),
                subtitle = stringResource(R.string.stations_count, stations.size),
                onBack = { viewModel.selectTag(null) }
            )
            PhoneStationGrid(
                stations = stations,
                viewModel = viewModel,
                onLoadMore = if (hasMoreStations) {{ viewModel.loadMoreStations() }} else null,
                onLongClick = onStationLongClick
            )
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        PhoneScreenHeader(title = stringResource(R.string.nav_genres))
        PhoneSearchField(
            value = localTagSearchQuery,
            onValueChange = { localTagSearchQuery = it; viewModel.setTagSearchQuery(it) },
            placeholder = stringResource(R.string.search_hint),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            onSearch = { keyboardController?.hide() }
        )
        Spacer(modifier = Modifier.height(10.dp))
        PhoneChipRow(caption = stringResource(R.string.sort_label)) {
            GenreSortMode.entries.forEach { mode ->
                PhoneChip(
                    selected = genreSortMode == mode,
                    label = if (mode == GenreSortMode.Name) stringResource(R.string.sort_name) else stringResource(R.string.sort_count),
                    onClick = { viewModel.setGenreSortMode(mode) }
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        Box(modifier = Modifier.weight(1f)) {
            PhoneTagGrid(
                tags = filteredTags,
                onTagClick = { viewModel.selectTag(it) },
                onTagLongClick = { tag -> viewModel.showGenreDialog(tag.name) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhoneTagGrid(
    tags: List<Tag>,
    onTagClick: (Tag) -> Unit,
    onTagLongClick: ((Tag) -> Unit)? = null
) {
    if (tags.isEmpty()) {
        PhoneEmptyState(
            icon = Icons.AutoMirrored.Filled.List,
            message = stringResource(R.string.no_genres_found)
        )
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 156.dp),
        contentPadding = GridPadding,
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(GridGap),
        verticalArrangement = Arrangement.spacedBy(GridGap)
    ) {
        itemsIndexed(tags, key = { _, tag -> tag.name }) { _, tag ->
            PhonePhotoTile(
                title = tag.name.displayCase(),
                caption = stringResource(R.string.stations_count, tag.stationcount),
                imageUrl = MediaUtils.getGenreImageUrl(tag.name),
                baseColor = MediaUtils.getGenreColor(tag.name),
                height = 112.dp,
                onClick = { onTagClick(tag) },
                onLongClick = { onTagLongClick?.invoke(tag) },
                imageAlpha = 0.6f
            )
        }
    }
}

@Composable
fun PhoneCountriesScreen(
    viewModel: MainViewModel,
    countries: List<Country>,
    selectedCountry: Country?,
    stations: List<Station>,
    hasMoreStations: Boolean,
    onStationLongClick: (Station) -> Unit
) {
    if (selectedCountry != null) {
        Column(modifier = Modifier.fillMaxSize()) {
            PhoneScreenHeader(
                title = selectedCountry.name.displayCase(),
                subtitle = stringResource(R.string.stations_count, stations.size),
                onBack = { viewModel.selectCountry(null) }
            )
            PhoneStationGrid(
                stations = stations,
                viewModel = viewModel,
                onLoadMore = if (hasMoreStations) {{ viewModel.loadMoreStations() }} else null,
                onLongClick = onStationLongClick
            )
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        PhoneScreenHeader(title = stringResource(R.string.nav_countries))
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 200.dp),
            contentPadding = GridPadding,
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(GridGap),
            verticalArrangement = Arrangement.spacedBy(GridGap)
        ) {
            itemsIndexed(countries, key = { _, country -> country.iso_3166_1 }) { _, country ->
                PhoneCountryTile(country = country, onClick = { viewModel.selectCountry(country) })
            }
        }
    }
}

@Composable
private fun PhoneCountryTile(country: Country, onClick: () -> Unit) {
    val shape = RoundedCornerShape(18.dp)
    Surface(
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)),
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(shape)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val flagUrl = MediaUtils.getCountryFlagUrl(country.iso_3166_1)
            Box(
                modifier = Modifier
                    .size(width = 44.dp, height = 32.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                contentAlignment = Alignment.Center
            ) {
                if (flagUrl != null) {
                    AsyncImage(
                        model = flagUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.Public,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = country.name.displayCase(),
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(R.string.stations_count, country.stationcount),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

/** Bitrate chips only (no container) so they can share a row with other chips. */
@Composable
private fun BitrateChips(
    selectedBitrates: Set<BitrateFilter>,
    onToggleFilter: (BitrateFilter) -> Unit
) {
    BitrateFilter.entries.forEach { filter ->
        val labelRes = when (filter) {
            BitrateFilter.Low -> R.string.filter_low
            BitrateFilter.High -> R.string.filter_high
            BitrateFilter.FLAC -> R.string.filter_flac
        }
        PhoneChip(
            selected = selectedBitrates.contains(filter),
            label = stringResource(labelRes),
            onClick = { onToggleFilter(filter) }
        )
    }
}

@Composable
private fun ChipDivider() {
    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .width(1.dp)
            .height(20.dp)
            .background(MaterialTheme.colorScheme.outlineVariant)
    )
}

@Composable
fun PhoneBitrateFilters(
    selectedBitrates: Set<BitrateFilter>,
    onToggleFilter: (BitrateFilter) -> Unit
) {
    PhoneChipRow(caption = stringResource(R.string.filter_label)) {
        BitrateChips(selectedBitrates = selectedBitrates, onToggleFilter = onToggleFilter)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneSettingsScreen(
    viewModel: MainViewModel,
    settingsSubMenu: String?,
    onImportPlaylist: () -> Unit,
    onExportPlaylist: () -> Unit
) {
    val context = LocalContext.current
    val onPermissionRequest = {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            if (!android.os.Environment.isExternalStorageManager()) {
                try {
                    val intent = android.content.Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, android.net.Uri.parse("package:${context.packageName}"))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    try {
                        val intent = android.content.Intent(android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                        context.startActivity(intent)
                    } catch (e2: Exception) {}
                }
            } else {
                viewModel.setError(context.getString(R.string.error_permission_already_granted))
            }
        } else {
            viewModel.setError(context.getString(R.string.error_permission_already_requested))
        }
    }

    val visibleGenres by viewModel.visibleGenres.collectAsState()
    val filteredTags by viewModel.filteredTags.collectAsState()
    val tagSearchQuery by viewModel.tagSearchQuery.collectAsState()
    var localTagSearchQuery by remember { mutableStateOf(tagSearchQuery) }
    LaunchedEffect(tagSearchQuery) {
        if (tagSearchQuery != localTagSearchQuery) localTagSearchQuery = tagSearchQuery
    }
    val hideBroken by viewModel.hideBrokenStations.collectAsState()
    val serverStats by viewModel.serverStats.collectAsState()
    val lastUpdate by viewModel.lastDbUpdate.collectAsState()
    val autoUpdateInterval by viewModel.autoUpdateInterval.collectAsState()
    val screensaverEnabled by viewModel.screensaverEnabled.collectAsState()
    val screensaverTimeout by viewModel.screensaverTimeout.collectAsState()
    val screensaverMode by viewModel.screensaverMode.collectAsState()
    val genreSortMode by viewModel.genreSortMode.collectAsState()
    val minTagFilter by viewModel.minTagFilter.collectAsState()
    val appTheme by viewModel.appTheme.collectAsState()
    val quitConfirmationEnabled by viewModel.quitConfirmationEnabled.collectAsState()
    val audioPassthrough by viewModel.audioPassthrough.collectAsState()
    val resumeLastStation by viewModel.resumeLastStation.collectAsState()
    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current
    val defaultStartupCategory by viewModel.defaultStartupCategoryFlow.collectAsState()

    when (settingsSubMenu) {
        "HomeGenres" -> PhoneHomeGenresSettings(
            viewModel, visibleGenres, filteredTags, tagSearchQuery, localTagSearchQuery,
            genreSortMode, keyboardController
        ) { viewModel.setTagSearchQuery(it); localTagSearchQuery = it }
        "AutoUpdate" -> PhoneAutoUpdateSettings(viewModel, autoUpdateInterval)
        "Screensaver" -> PhoneScreensaverSettings(viewModel, screensaverEnabled, screensaverTimeout, screensaverMode)
        "AppTheme" -> PhoneAppThemeSettings(viewModel, appTheme)
        "DefaultCategory" -> PhoneDefaultCategorySettings(viewModel, defaultStartupCategory)
        "AppLanguage" -> PhoneAppLanguageSettings(viewModel)
        "Waveform" -> PhoneWaveformSettings(viewModel)
        else -> PhoneSettingsMain(
            viewModel, appTheme, quitConfirmationEnabled, screensaverEnabled, screensaverTimeout,
            hideBroken, minTagFilter, autoUpdateInterval, audioPassthrough, resumeLastStation,
            defaultStartupCategory,
            serverStats, lastUpdate,
            onImportPlaylist, onExportPlaylist, onPermissionRequest
        )
    }
}

/** Header + scrolling body shared by every settings page. */
@Composable
private fun PhoneSettingsPage(
    title: String,
    onBack: (() -> Unit)?,
    content: LazyListScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        PhoneScreenHeader(title = title, onBack = onBack)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 32.dp),
            content = content
        )
    }
}

/** Mini preview of a theme: background, a raised surface and the accent colour. */
@Composable
private fun ThemeSwatch(theme: AppTheme, size: androidx.compose.ui.unit.Dp = 40.dp) {
    val palette = paletteFor(theme)
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = Modifier
            .size(size)
            .clip(shape)
            .background(palette.background)
            .border(1.dp, palette.outline.copy(alpha = 0.6f), shape)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.42f)
                .background(palette.surfaceHighest)
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(size * 0.42f)
                .background(palette.primary, CircleShape)
        )
    }
}

@Composable
private fun SettingsRadio(selected: Boolean) {
    RadioButton(selected = selected, onClick = null)
}

@Composable
private fun SettingsSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Switch(checked = checked, onCheckedChange = onCheckedChange)
}

@Composable
fun PhoneWaveformSettings(viewModel: MainViewModel) {
    val currentType by viewModel.waveformType.collectAsState()
    PhoneSettingsPage(
        title = stringResource(R.string.settings_waveform),
        onBack = { viewModel.setSettingsSubMenu(null) }
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(vertical = 28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    WaveformAnalyzer(isPlaying = true, type = currentType)
                }
            }
        }
        item {
            PhoneSettingsSection(title = stringResource(R.string.settings_waveform_desc)) {
                com.toxa.pureradio.ui.viewmodel.WaveformType.entries.forEachIndexed { index, type ->
                    val labelRes = when (type) {
                        com.toxa.pureradio.ui.viewmodel.WaveformType.Classic -> R.string.waveform_type_classic
                        com.toxa.pureradio.ui.viewmodel.WaveformType.Mirrored -> R.string.waveform_type_mirrored
                        com.toxa.pureradio.ui.viewmodel.WaveformType.BarsDots -> R.string.waveform_type_dots
                        com.toxa.pureradio.ui.viewmodel.WaveformType.BarDotsLevelHold -> R.string.waveform_type_dots_hold
                    }
                    PhoneSettingsRow(
                        title = stringResource(labelRes),
                        showDivider = index > 0,
                        onClick = { viewModel.setWaveformType(type) },
                        trailing = { SettingsRadio(currentType == type) }
                    )
                }
            }
        }
    }
}

@Composable
fun PhoneSettingsMain(
    viewModel: MainViewModel, appTheme: AppTheme, quitConfirmationEnabled: Boolean,
    screensaverEnabled: Boolean, screensaverTimeout: Int, hideBroken: Boolean,
    minTagFilter: Boolean, autoUpdateInterval: Int, audioPassthrough: Boolean,
    resumeLastStation: Boolean, defaultStartupCategory: NavigationItem,
    serverStats: com.toxa.pureradio.network.ServerStats?, lastUpdate: Long,
    onImportPlaylist: () -> Unit, onExportPlaylist: () -> Unit,
    onPermissionRequest: () -> Unit
) {
    PhoneSettingsPage(title = stringResource(R.string.settings_title), onBack = null) {
        item {
            PhoneSettingsSection(title = stringResource(R.string.settings_interface)) {
                PhoneSettingsRow(
                    title = stringResource(R.string.settings_theme),
                    subtitle = stringResource(R.string.current_theme, stringResource(appTheme.labelRes)),
                    leading = { ThemeSwatch(appTheme) },
                    onClick = { viewModel.setSettingsSubMenu("AppTheme") },
                    trailing = { SettingsChevron() }
                )
                PhoneSettingsRow(
                    title = stringResource(R.string.settings_language),
                    subtitle = stringResource(R.string.settings_language_desc),
                    icon = Icons.Default.Public,
                    showDivider = true,
                    onClick = { viewModel.setSettingsSubMenu("AppLanguage") },
                    trailing = { SettingsChevron() }
                )
                PhoneSettingsRow(
                    title = stringResource(R.string.settings_home_genres),
                    subtitle = stringResource(R.string.settings_home_genres_desc),
                    icon = Icons.Default.Home,
                    showDivider = true,
                    onClick = { viewModel.setSettingsSubMenu("HomeGenres") },
                    trailing = { SettingsChevron() }
                )
                PhoneSettingsRow(
                    title = stringResource(R.string.settings_startup_category),
                    subtitle = stringResource(R.string.current_category, stringResource(defaultStartupCategory.labelRes)),
                    icon = Icons.Default.PlayArrow,
                    showDivider = true,
                    onClick = { viewModel.setSettingsSubMenu("DefaultCategory") },
                    trailing = { SettingsChevron() }
                )
                PhoneSettingsRow(
                    title = stringResource(R.string.settings_quit_confirmation),
                    subtitle = stringResource(R.string.settings_quit_confirmation_desc),
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    showDivider = true,
                    onClick = { viewModel.setQuitConfirmationEnabled(!quitConfirmationEnabled) },
                    trailing = {
                        SettingsSwitch(quitConfirmationEnabled) { viewModel.setQuitConfirmationEnabled(it) }
                    }
                )
            }
        }

        item {
            val waveformType by viewModel.waveformType.collectAsState()
            val waveformName = when (waveformType) {
                com.toxa.pureradio.ui.viewmodel.WaveformType.Classic -> stringResource(R.string.waveform_type_classic)
                com.toxa.pureradio.ui.viewmodel.WaveformType.Mirrored -> stringResource(R.string.waveform_type_mirrored)
                com.toxa.pureradio.ui.viewmodel.WaveformType.BarsDots -> stringResource(R.string.waveform_type_dots)
                com.toxa.pureradio.ui.viewmodel.WaveformType.BarDotsLevelHold -> stringResource(R.string.waveform_type_dots_hold)
            }
            PhoneSettingsSection(title = stringResource(R.string.settings_playback)) {
                PhoneSettingsRow(
                    title = stringResource(R.string.settings_screensaver),
                    subtitle = if (screensaverEnabled) stringResource(R.string.screensaver_enabled_fmt, screensaverTimeout) else stringResource(R.string.screensaver_disabled),
                    icon = Icons.Default.MusicVideo,
                    onClick = { viewModel.setSettingsSubMenu("Screensaver") },
                    trailing = { SettingsChevron() }
                )
                PhoneSettingsRow(
                    title = stringResource(R.string.settings_waveform),
                    subtitle = stringResource(R.string.current_value, waveformName),
                    icon = Icons.Default.GraphicEq,
                    showDivider = true,
                    onClick = { viewModel.setSettingsSubMenu("Waveform") },
                    trailing = { SettingsChevron() }
                )
                PhoneSettingsRow(
                    title = stringResource(R.string.settings_audio_passthrough),
                    subtitle = stringResource(R.string.settings_audio_passthrough_desc),
                    icon = Icons.Default.MusicNote,
                    showDivider = true,
                    onClick = { viewModel.toggleAudioPassthrough() },
                    trailing = {
                        SettingsSwitch(audioPassthrough) { viewModel.toggleAudioPassthrough() }
                    }
                )
                PhoneSettingsRow(
                    title = stringResource(R.string.settings_resume_last),
                    subtitle = stringResource(R.string.settings_resume_last_desc),
                    icon = Icons.Default.Radio,
                    showDivider = true,
                    onClick = { viewModel.setResumeLastStation(!resumeLastStation) },
                    trailing = {
                        SettingsSwitch(resumeLastStation) { viewModel.setResumeLastStation(it) }
                    }
                )
            }
        }

        item {
            val dateStr = if (lastUpdate > 0) {
                SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(lastUpdate))
            } else {
                stringResource(R.string.last_update_never)
            }
            PhoneSettingsSection(title = stringResource(R.string.settings_database)) {
                PhoneSettingsRow(
                    title = stringResource(R.string.database_smart_filter),
                    subtitle = stringResource(R.string.database_smart_filter_desc),
                    icon = Icons.Default.Warning,
                    onClick = { viewModel.toggleHideBroken() },
                    trailing = {
                        SettingsSwitch(hideBroken) { viewModel.toggleHideBroken() }
                    }
                )
                PhoneSettingsRow(
                    title = stringResource(R.string.database_min_tags),
                    subtitle = stringResource(R.string.database_min_tags_desc),
                    icon = Icons.AutoMirrored.Filled.List,
                    showDivider = true,
                    onClick = { viewModel.toggleMinTagFilter() },
                    trailing = {
                        SettingsSwitch(minTagFilter) { viewModel.toggleMinTagFilter() }
                    }
                )
                PhoneSettingsRow(
                    title = stringResource(R.string.database_sync),
                    subtitle = stringResource(R.string.database_sync_desc),
                    icon = Icons.Default.History,
                    showDivider = true,
                    onClick = { viewModel.setSettingsSubMenu("AutoUpdate") },
                    trailing = { SettingsChevron() }
                )
                PhoneSettingsRow(
                    title = stringResource(R.string.database_refresh),
                    subtitle = stringResource(
                        R.string.last_update_format,
                        dateStr,
                        serverStats?.stations?.toString() ?: "…"
                    ),
                    icon = Icons.Default.CloudDownload,
                    showDivider = true,
                    onClick = { viewModel.updateDatabase() }
                )
            }
        }

        item {
            PhoneSettingsSection(title = stringResource(R.string.data_management)) {
                PhoneSettingsRow(
                    title = stringResource(R.string.data_backup),
                    subtitle = stringResource(R.string.data_backup_desc),
                    icon = Icons.Default.CloudUpload,
                    onClick = { onExportPlaylist() }
                )
                PhoneSettingsRow(
                    title = stringResource(R.string.data_restore),
                    subtitle = stringResource(R.string.data_restore_desc),
                    icon = Icons.Default.CloudDownload,
                    showDivider = true,
                    onClick = { onImportPlaylist() }
                )
                PhoneSettingsRow(
                    title = stringResource(R.string.data_permissions),
                    subtitle = stringResource(R.string.data_permissions_desc),
                    icon = Icons.Default.Folder,
                    showDivider = true,
                    onClick = { onPermissionRequest() }
                )
            }
        }

        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(R.drawable.ic_radio_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "PURE RADIO",
                        style = MaterialTheme.typography.titleMedium,
                        letterSpacing = 3.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        stringResource(R.string.splash_tagline),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    InfoPill(
                        text = "v${BuildConfig.VERSION_NAME} · ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(BuildConfig.BUILD_TIME))}",
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun PhoneDefaultCategorySettings(
    viewModel: MainViewModel, currentCategory: NavigationItem
) {
    val validCategories = NavigationItem.entries.filter {
        it in setOf(NavigationItem.Home, NavigationItem.Popular, NavigationItem.Recent,
                     NavigationItem.Genres, NavigationItem.Countries, NavigationItem.Favourites)
    }
    PhoneSettingsPage(
        title = stringResource(R.string.settings_startup_category),
        onBack = { viewModel.setSettingsSubMenu(null) }
    ) {
        item {
            PhoneSettingsSection(title = null) {
                validCategories.forEachIndexed { index, category ->
                    PhoneSettingsRow(
                        title = stringResource(category.labelRes),
                        icon = navigationIconFor(category),
                        showDivider = index > 0,
                        onClick = { viewModel.setDefaultStartupCategory(category) },
                        trailing = { SettingsRadio(category == currentCategory) }
                    )
                }
            }
        }
    }
}

/** Shared icon mapping for navigation destinations (menu, settings). */
fun navigationIconFor(item: NavigationItem): androidx.compose.ui.graphics.vector.ImageVector = when (item) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneHomeGenresSettings(
    viewModel: MainViewModel, visibleGenres: Set<String>, filteredTags: List<Tag>,
    tagSearchQuery: String, localTagSearchQuery: String, genreSortMode: GenreSortMode,
    keyboardController: androidx.compose.ui.platform.SoftwareKeyboardController?,
    onTagSearchChange: (String) -> Unit
) {
    PhoneSettingsPage(
        title = stringResource(R.string.settings_personalize_home),
        onBack = { viewModel.setSettingsSubMenu(null) }
    ) {
        item {
            PhoneSearchField(
                value = localTagSearchQuery,
                onValueChange = onTagSearchChange,
                placeholder = stringResource(R.string.search_hint),
                modifier = Modifier.fillMaxWidth(),
                onSearch = { keyboardController?.hide() }
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    stringResource(R.string.sort_label),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                GenreSortMode.entries.forEach { mode ->
                    PhoneChip(
                        selected = genreSortMode == mode,
                        label = if (mode == GenreSortMode.Name) stringResource(R.string.sort_name) else stringResource(R.string.sort_count),
                        onClick = { viewModel.setGenreSortMode(mode) }
                    )
                }
            }
            Text(
                stringResource(R.string.genres_section),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.2.sp,
                modifier = Modifier.padding(start = 12.dp, top = 16.dp, bottom = 4.dp)
            )
        }
        items(filteredTags) { tag ->
            val checked = visibleGenres.contains(tag.name)
            PhoneSettingsRow(
                title = tag.name.displayCase(),
                subtitle = stringResource(R.string.stations_count, tag.stationcount),
                onClick = { viewModel.toggleGenreVisibility(tag.name) },
                trailing = {
                    Checkbox(checked = checked, onCheckedChange = { viewModel.toggleGenreVisibility(tag.name) })
                }
            )
        }
    }
}

@Composable
fun PhoneAutoUpdateSettings(viewModel: MainViewModel, autoUpdateInterval: Int) {
    val options = listOf(
        0 to R.string.update_manual,
        12 to R.string.update_every_12h,
        24 to R.string.update_every_24h
    )
    PhoneSettingsPage(
        title = stringResource(R.string.settings_db_update_interval),
        onBack = { viewModel.setSettingsSubMenu(null) }
    ) {
        item {
            PhoneSettingsSection(title = null) {
                options.forEachIndexed { index, (hours, labelRes) ->
                    PhoneSettingsRow(
                        title = stringResource(labelRes),
                        showDivider = index > 0,
                        onClick = { viewModel.setAutoUpdateInterval(hours); viewModel.setSettingsSubMenu(null) },
                        trailing = { SettingsRadio(autoUpdateInterval == hours) }
                    )
                }
            }
        }
    }
}

@Composable
fun PhoneScreensaverSettings(
    viewModel: MainViewModel, screensaverEnabled: Boolean,
    screensaverTimeout: Int, screensaverMode: ScreensaverMode
) {
    val timeouts = listOf(1, 5, 10, 20, 30)
    PhoneSettingsPage(
        title = stringResource(R.string.settings_screensaver_prefs),
        onBack = { viewModel.setSettingsSubMenu(null) }
    ) {
        item {
            PhoneSettingsSection(title = null) {
                PhoneSettingsRow(
                    title = stringResource(R.string.screensaver_activate),
                    subtitle = stringResource(R.string.screensaver_activate_desc),
                    icon = Icons.Default.MusicVideo,
                    onClick = { viewModel.toggleScreensaver(!screensaverEnabled) },
                    trailing = {
                        SettingsSwitch(screensaverEnabled) { viewModel.toggleScreensaver(it) }
                    }
                )
            }
        }
        item {
            PhoneSettingsSection(title = stringResource(R.string.screensaver_display_mode)) {
                listOf(
                    ScreensaverMode.StationInfo to R.string.screensaver_mode_retro,
                    ScreensaverMode.BlackScreen to R.string.screensaver_mode_black
                ).forEachIndexed { index, (mode, labelRes) ->
                    PhoneSettingsRow(
                        title = stringResource(labelRes),
                        showDivider = index > 0,
                        onClick = { viewModel.setScreensaverMode(mode) },
                        trailing = { SettingsRadio(screensaverMode == mode) }
                    )
                }
            }
        }
        item {
            PhoneSettingsSection(title = stringResource(R.string.screensaver_idle_timeout)) {
                timeouts.forEachIndexed { index, minutes ->
                    PhoneSettingsRow(
                        title = stringResource(R.string.screensaver_minutes, minutes),
                        showDivider = index > 0,
                        onClick = { viewModel.setScreensaverTimeout(minutes); viewModel.setSettingsSubMenu(null) },
                        trailing = { SettingsRadio(screensaverTimeout == minutes) }
                    )
                }
            }
        }
    }
}

@Composable
fun PhoneAppThemeSettings(viewModel: MainViewModel, appTheme: AppTheme) {
    PhoneSettingsPage(
        title = stringResource(R.string.settings_application_theme),
        onBack = { viewModel.setSettingsSubMenu(null) }
    ) {
        item {
            PhoneSettingsSection(title = null) {
                AppTheme.entries.forEachIndexed { index, theme ->
                    PhoneSettingsRow(
                        title = stringResource(theme.labelRes),
                        subtitle = stringResource(theme.descRes),
                        leading = { ThemeSwatch(theme, size = 44.dp) },
                        showDivider = index > 0,
                        onClick = { viewModel.setAppTheme(theme) },
                        trailing = { SettingsRadio(appTheme == theme) }
                    )
                }
            }
        }
    }
}

@Composable
fun PhoneAppLanguageSettings(viewModel: MainViewModel) {
    val currentLang by viewModel.appLanguage.collectAsState()
    PhoneSettingsPage(
        title = stringResource(R.string.settings_language),
        onBack = { viewModel.setSettingsSubMenu(null) }
    ) {
        item {
            PhoneSettingsSection(title = null) {
                AppLanguage.entries.forEachIndexed { index, lang ->
                    PhoneSettingsRow(
                        title = lang.displayName,
                        showDivider = index > 0,
                        onClick = { viewModel.setAppLanguage(lang) },
                        trailing = { SettingsRadio(currentLang == lang) }
                    )
                }
            }
        }
    }
}

@Composable
fun WaveformAnalyzer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    type: com.toxa.pureradio.ui.viewmodel.WaveformType = com.toxa.pureradio.ui.viewmodel.WaveformType.Classic,
    compact: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Waveform")
    val barCount = 40
    Row(
        modifier = modifier.then(
            if (compact) Modifier.width(200.dp).height(44.dp)
            else Modifier.width(260.dp).height(60.dp)
        ),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(barCount) { i ->
            key(i) {
                val duration = remember { (500..1200).random() }
                val delay = remember { (i * 35) % 800 }
            val heightScale by infiniteTransition.animateFloat(
                initialValue = 0.1f, targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(duration, delay, easing = FastOutLinearInEasing), RepeatMode.Reverse),
                label = "Height_$i"
            )
            val distanceFromCenter = Math.abs(i - barCount / 2).toFloat()
            val centerWeight = 1f - (distanceFromCenter / (barCount / 2))
            val finalHeight = if (isPlaying) (0.1f + 0.9f * heightScale) * (0.2f + 0.8f * centerWeight) else 0.05f
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                when (type) {
                    com.toxa.pureradio.ui.viewmodel.WaveformType.Classic -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(finalHeight)
                                .align(Alignment.BottomCenter)
                                .background(
                                    brush = Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))),
                                    shape = RoundedCornerShape(1.dp)
                                )
                        )
                    }
                    com.toxa.pureradio.ui.viewmodel.WaveformType.Mirrored -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(finalHeight)
                                .align(Alignment.Center)
                                .background(
                                    brush = Brush.verticalGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                        )
                                    ),
                                    shape = RoundedCornerShape(1.dp)
                                )
                        )
                    }
                    com.toxa.pureradio.ui.viewmodel.WaveformType.BarsDots -> {
                        WaveformDots(finalHeight, isPlaying, holdPeak = false)
                    }
                    com.toxa.pureradio.ui.viewmodel.WaveformType.BarDotsLevelHold -> {
                        WaveformDots(finalHeight, isPlaying, holdPeak = true)
                    }
                }
            }
        }
    }
}
}

@Composable
fun WaveformDots(heightScale: Float, isPlaying: Boolean, holdPeak: Boolean) {
    val dotCount = 18
    val activeDots = if (isPlaying) (heightScale * dotCount).toInt().coerceIn(0, dotCount) else 0
    
    var peakIndex by remember { mutableIntStateOf(0) }
    val currentActiveDots by rememberUpdatedState(activeDots)
    
    LaunchedEffect(activeDots, holdPeak) {
        if (holdPeak && activeDots > peakIndex) {
            peakIndex = activeDots
        }
    }
    
    LaunchedEffect(isPlaying, holdPeak) {
        if (!isPlaying || !holdPeak) {
            peakIndex = 0
            return@LaunchedEffect
        }
        var holdTimer = 0L
        while (true) {
            delay(50) // Tick rate
            if (peakIndex > currentActiveDots) {
                if (holdTimer < 250) {
                    holdTimer += 50
                } else {
                    peakIndex = (peakIndex - 1).coerceAtLeast(currentActiveDots)
                }
            } else {
                holdTimer = 0
                // If the signal rises above the current peak while we are not in "hold" state
                if (activeDots > peakIndex) peakIndex = activeDots
            }
        }
    }
    
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(1.dp, Alignment.Bottom),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat(dotCount) { i ->
            val dotIndex = dotCount - 1 - i
            val isActive = dotIndex < activeDots
            val isPeak = holdPeak && dotIndex == peakIndex - 1 && peakIndex > 0
            
            val baseColor = MaterialTheme.colorScheme.primary
            val color = when {
                isPeak -> baseColor
                isActive -> {
                    val intensity = (dotIndex.toFloat() / dotCount)
                    // Brighter at the top (higher index), matching Classic style
                    baseColor.copy(alpha = 0.4f + (0.6f * intensity))
                }
                else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
            }
            
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(2.dp)
                    .background(color, RoundedCornerShape(0.5.dp))
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF040714)
@Composable
fun WaveformAnalyzerPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        com.toxa.pureradio.ui.viewmodel.WaveformType.entries.forEach { type ->
            Text(text = type.name, color = Color.White)
            WaveformAnalyzer(isPlaying = true, type = type)
        }
    }
}

@UnstableApi
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneNowPlayingBar(viewModel: MainViewModel, onClick: () -> Unit = {}) {
    val currentStation by viewModel.currentStation.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val mediaMetadata by viewModel.mediaMetadata.collectAsState()
    val bufferedPosition by viewModel.bufferedPosition.collectAsState()
    val isLive by viewModel.isLive.collectAsState()
    val audioFormat by viewModel.audioFormat.collectAsState()

    currentStation?.let { station ->
        val shape = RoundedCornerShape(22.dp)
        val colors = MaterialTheme.colorScheme
        Surface(
            shadowElevation = 12.dp,
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth()
                .height(76.dp)
                .clip(shape)
                .clickable { onClick() },
            shape = shape,
            color = colors.surfaceContainerHigh,
            border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.6f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            listOf(colors.primary.copy(alpha = 0.12f), Color.Transparent)
                        )
                    )
            ) {
                Row(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 12.dp)
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StationArtwork(
                        model = mediaMetadata?.artworkUri ?: MediaUtils.getStationArtworkUrl(station.favicon, station.countryCode),
                        modifier = Modifier.size(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = 7.dp
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = buildTitleText(mediaMetadata, station),
                            style = MaterialTheme.typography.titleSmall,
                            color = colors.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            MiniEqualizer(
                                isPlaying = isPlaying,
                                modifier = Modifier.size(width = 11.dp, height = 10.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = station.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            val techInfo = audioFormat?.let { format ->
                                buildString {
                                    if (format.bitrate > 0) append("${format.bitrate / 1000}k")
                                    val codec = format.sampleMimeType?.removePrefix("audio/")?.uppercase()
                                        ?.replace("MPEG", "MP3")?.replace("MP4A-LATM", "AAC")
                                    if (codec != null) {
                                        if (isNotEmpty()) append(" ")
                                        append(codec)
                                    }
                                }
                            } ?: if (station.bitrate > 0) "${station.bitrate}k" else ""

                            if (techInfo.isNotEmpty()) {
                                Text(
                                    text = "  ·  $techInfo",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.primary,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = colors.primary,
                        contentColor = colors.onPrimary,
                        modifier = Modifier.size(48.dp)
                    ) {
                        IconButton(onClick = { viewModel.togglePlayPause() }) {
                            Icon(
                                if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) stringResource(R.string.pause_desc) else stringResource(R.string.play_desc),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                if (isLive) {
                    val progress = (bufferedPosition.toFloat() / MainViewModel.MAX_BUFFER_MS).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .align(Alignment.BottomCenter),
                        color = colors.primary.copy(alpha = 0.6f),
                        trackColor = Color.Transparent
                    )
                }
            }
        }
    }
}

@UnstableApi
@Composable
fun PhoneNowPlayingDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val currentStation by viewModel.currentStation.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val mediaMetadata by viewModel.mediaMetadata.collectAsState()
    val playbackTime by viewModel.playbackTime.collectAsState()
    val playbackDuration by viewModel.playbackDuration.collectAsState()
    val audioFormat by viewModel.audioFormat.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val isLive by viewModel.isLive.collectAsState()
    val bufferedPosition by viewModel.bufferedPosition.collectAsState()
    val waveformType by viewModel.waveformType.collectAsState()

    currentStation?.let { station ->
        val isFavorite = favorites.contains(station.stationUuid)
        val artworkModel: Any? = mediaMetadata?.artworkUri ?: MediaUtils.getStationArtworkUrl(station.favicon, station.countryCode)
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    NowPlayingBackdrop(artworkModel = artworkModel)
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val compact = maxHeight < 640.dp && maxWidth >= 480.dp
                        if (compact) {
                            NowPlayingCompactContent(
                                station = station,
                                viewModel = viewModel,
                                artworkModel = artworkModel,
                                isFavorite = isFavorite,
                                isPlaying = isPlaying,
                                mediaMetadata = mediaMetadata,
                                playbackTime = playbackTime,
                                playbackDuration = playbackDuration,
                                bufferedPosition = bufferedPosition,
                                isLive = isLive,
                                audioFormat = audioFormat,
                                waveformType = waveformType,
                                showWaveform = maxHeight >= 300.dp,
                                onDismiss = onDismiss
                            )
                        } else {
                            NowPlayingFullContent(
                                station = station,
                                viewModel = viewModel,
                                artworkModel = artworkModel,
                                isFavorite = isFavorite,
                                isPlaying = isPlaying,
                                mediaMetadata = mediaMetadata,
                                playbackTime = playbackTime,
                                playbackDuration = playbackDuration,
                                bufferedPosition = bufferedPosition,
                                isLive = isLive,
                                audioFormat = audioFormat,
                                waveformType = waveformType,
                                onDismiss = onDismiss
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Ambient background for the player: a heavily blurred copy of the artwork (Android 12+,
 * where RenderEffect blur is available) plus an accent glow, faded into the theme background.
 */
@Composable
private fun NowPlayingBackdrop(artworkModel: Any?) {
    val colors = MaterialTheme.colorScheme
    Box(modifier = Modifier.fillMaxSize()) {
        if (artworkModel != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            AsyncImage(
                model = artworkModel,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = 0.45f,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(72.dp)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(colors.primary.copy(alpha = 0.22f), Color.Transparent),
                        center = androidx.compose.ui.geometry.Offset(0f, 0f),
                        radius = 1400f
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to colors.background.copy(alpha = 0.35f),
                        0.55f to colors.background.copy(alpha = 0.75f),
                        1f to colors.background
                    )
                )
        )
    }
}

private fun buildTitleText(mediaMetadata: MediaMetadata?, station: Station): String =
    mediaMetadata?.let {
        if (!it.title.isNullOrEmpty()) it.title.toString()
        else if (!it.displayTitle.isNullOrEmpty()) it.displayTitle.toString()
        else null
    } ?: station.name

@UnstableApi
private fun buildTechInfo(audioFormat: Format?, station: Station): String =
    audioFormat?.let { format ->
        buildString {
            format.codecs?.let { append(it.uppercase()) }
            if (format.bitrate > 0) {
                if (isNotEmpty()) append(" • ")
                append("${format.bitrate / 1000}k")
            }
            if (format.sampleRate > 0) {
                if (isNotEmpty()) append(" • ")
                append("${format.sampleRate / 1000}kHz")
            }
        }
    } ?: if (station.bitrate > 0) "${station.bitrate}k" else station.codec?.uppercase() ?: ""

@Composable
private fun LivePill() {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.error.copy(alpha = 0.16f),
        contentColor = MaterialTheme.colorScheme.error
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(MaterialTheme.colorScheme.error, CircleShape)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text("LIVE", style = MaterialTheme.typography.labelSmall, letterSpacing = 1.sp)
        }
    }
}

@UnstableApi
@Composable
private fun NowPlayingInfoPills(isLive: Boolean, audioFormat: Format?, station: Station) {
    val techInfo = buildTechInfo(audioFormat, station)
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isLive) LivePill()
        if (techInfo.isNotEmpty()) InfoPill(text = techInfo)
    }
}

@Composable
private fun PlaybackTimeLabel(playbackTime: Long) {
    val timeMinutes = (playbackTime / 1000) / 60
    val timeSeconds = (playbackTime / 1000) % 60
    Text(
        text = String.format(Locale.getDefault(), "%02d:%02d", timeMinutes, timeSeconds),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun PlaybackProgressSection(
    isLive: Boolean,
    bufferedPosition: Long,
    playbackDuration: Long,
    playbackTime: Long,
    showBufferLabel: Boolean
) {
    val trackModifier = Modifier
        .fillMaxWidth()
        .height(4.dp)
        .clip(CircleShape)
    Column(modifier = Modifier.fillMaxWidth()) {
        if (isLive) {
            val progress = (bufferedPosition.toFloat() / MainViewModel.MAX_BUFFER_MS).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { progress },
                modifier = trackModifier,
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
            )
        } else if (playbackDuration > 0) {
            LinearProgressIndicator(
                progress = { (playbackTime.toFloat() / playbackDuration.toFloat()).coerceIn(0f, 1f) },
                modifier = trackModifier,
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlaybackTimeLabel(playbackTime)
            Spacer(modifier = Modifier.weight(1f))
            if (isLive && showBufferLabel) {
                Text(
                    text = "Buffer ${bufferedPosition / 1000}s",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PlaybackControlsRow(viewModel: MainViewModel, isPlaying: Boolean, compact: Boolean) {
    val sideSize = if (compact) 48.dp else 60.dp
    val sideIcon = if (compact) 26.dp else 32.dp
    val playSize = if (compact) 64.dp else 80.dp
    val playIcon = if (compact) 34.dp else 42.dp
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(if (compact) 20.dp else 28.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.85f),
            contentColor = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(sideSize)
        ) {
            IconButton(onClick = { viewModel.playPrevious() }) {
                Icon(Icons.Default.SkipPrevious, contentDescription = stringResource(R.string.previous_desc), modifier = Modifier.size(sideIcon))
            }
        }
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shadowElevation = 10.dp,
            modifier = Modifier.size(playSize)
        ) {
            IconButton(onClick = { viewModel.togglePlayPause() }) {
                Icon(
                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) stringResource(R.string.pause_desc) else stringResource(R.string.play_desc),
                    modifier = Modifier.size(playIcon)
                )
            }
        }
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.85f),
            contentColor = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(sideSize)
        ) {
            IconButton(onClick = { viewModel.playNext() }) {
                Icon(Icons.Default.SkipNext, contentDescription = stringResource(R.string.next_desc), modifier = Modifier.size(sideIcon))
            }
        }
    }
}

@Composable
private fun FavoriteToggle(isFavorite: Boolean, onToggle: () -> Unit) {
    IconButton(onClick = onToggle) {
        Icon(
            if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = if (isFavorite) stringResource(R.string.remove_from_fav_desc) else stringResource(R.string.add_to_fav_desc),
            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@UnstableApi
@Composable
private fun NowPlayingFullContent(
    station: Station,
    viewModel: MainViewModel,
    artworkModel: Any?,
    isFavorite: Boolean,
    isPlaying: Boolean,
    mediaMetadata: MediaMetadata?,
    playbackTime: Long,
    playbackDuration: Long,
    bufferedPosition: Long,
    isLive: Boolean,
    audioFormat: Format?,
    waveformType: com.toxa.pureradio.ui.viewmodel.WaveformType,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDismiss) {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(R.string.close_desc),
                    modifier = Modifier.size(30.dp)
                )
            }
            Text(
                text = station.name.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.5.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            FavoriteToggle(isFavorite) { viewModel.toggleFavorite(station) }
        }

        Spacer(modifier = Modifier.weight(1f))

        val artShape = RoundedCornerShape(32.dp)
        StationArtwork(
            model = artworkModel,
            modifier = Modifier
                .widthIn(max = 300.dp)
                .fillMaxWidth(0.72f)
                .aspectRatio(1f)
                .shadow(
                    elevation = 28.dp,
                    shape = artShape,
                    ambientColor = MaterialTheme.colorScheme.primary,
                    spotColor = MaterialTheme.colorScheme.primary
                ),
            shape = artShape,
            contentPadding = 28.dp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = buildTitleText(mediaMetadata, station),
            style = MaterialTheme.typography.headlineSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        if (!mediaMetadata?.artist.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = mediaMetadata?.artist.toString(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        NowPlayingInfoPills(isLive = isLive, audioFormat = audioFormat, station = station)

        Spacer(modifier = Modifier.height(24.dp))
        WaveformAnalyzer(isPlaying = isPlaying, type = waveformType)
        Spacer(modifier = Modifier.weight(1f))

        PlaybackProgressSection(
            isLive = isLive,
            bufferedPosition = bufferedPosition,
            playbackDuration = playbackDuration,
            playbackTime = playbackTime,
            showBufferLabel = true
        )

        Spacer(modifier = Modifier.height(24.dp))
        PlaybackControlsRow(viewModel, isPlaying, compact = false)
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@UnstableApi
@Composable
private fun NowPlayingCompactContent(
    station: Station,
    viewModel: MainViewModel,
    artworkModel: Any?,
    isFavorite: Boolean,
    isPlaying: Boolean,
    mediaMetadata: MediaMetadata?,
    playbackTime: Long,
    playbackDuration: Long,
    bufferedPosition: Long,
    isLive: Boolean,
    audioFormat: Format?,
    waveformType: com.toxa.pureradio.ui.viewmodel.WaveformType,
    showWaveform: Boolean,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val artShape = RoundedCornerShape(28.dp)
            StationArtwork(
                model = artworkModel,
                modifier = Modifier
                    .heightIn(max = 280.dp)
                    .fillMaxHeight(0.8f)
                    .aspectRatio(1f, matchHeightConstraintsFirst = true)
                    .shadow(
                        elevation = 24.dp,
                        shape = artShape,
                        ambientColor = MaterialTheme.colorScheme.primary,
                        spotColor = MaterialTheme.colorScheme.primary
                    ),
                shape = artShape,
                contentPadding = 22.dp
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 32.dp, end = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = station.name.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = buildTitleText(mediaMetadata, station),
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )

                if (!mediaMetadata?.artist.isNullOrEmpty()) {
                    Text(
                        text = mediaMetadata?.artist.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                NowPlayingInfoPills(isLive = isLive, audioFormat = audioFormat, station = station)

                if (showWaveform) {
                    Spacer(modifier = Modifier.height(10.dp))
                    WaveformAnalyzer(isPlaying = isPlaying, type = waveformType, compact = true)
                }

                Spacer(modifier = Modifier.height(12.dp))

                PlaybackProgressSection(
                    isLive = isLive,
                    bufferedPosition = bufferedPosition,
                    playbackDuration = playbackDuration,
                    playbackTime = playbackTime,
                    showBufferLabel = false
                )

                Spacer(modifier = Modifier.height(10.dp))
                PlaybackControlsRow(viewModel, isPlaying, compact = true)
            }
        }

        Row(modifier = Modifier.align(Alignment.TopEnd)) {
            FavoriteToggle(isFavorite) { viewModel.toggleFavorite(station) }
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close_desc))
            }
        }
    }
}

@Composable
fun PhoneScreensaver(viewModel: MainViewModel) {
    val currentStation by viewModel.currentStation.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val playbackTime by viewModel.playbackTime.collectAsState()
    val screensaverMode by viewModel.screensaverMode.collectAsState()
    val mediaMetadata by viewModel.mediaMetadata.collectAsState()
    val waveformType by viewModel.waveformType.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "Bounce")
    val xOffset by infiniteTransition.animateFloat(
        initialValue = -0.2f, targetValue = 0.2f,
        animationSpec = infiniteRepeatable(tween(15000, easing = LinearEasing), RepeatMode.Reverse),
        label = "x"
    )
    val yOffset by infiniteTransition.animateFloat(
        initialValue = -0.2f, targetValue = 0.2f,
        animationSpec = infiniteRepeatable(tween(11000, easing = LinearEasing), RepeatMode.Reverse),
        label = "y"
    )

    Surface(
        modifier = Modifier.fillMaxSize().clickable { viewModel.resetScreensaverTimer() },
        color = Color.Black
    ) {
        if (screensaverMode == ScreensaverMode.StationInfo) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                currentStation?.let { station ->
                    val displayTitle = if (!mediaMetadata?.title.isNullOrEmpty()) mediaMetadata?.title.toString() else station.name
                    Column(
                        modifier = Modifier.fillMaxWidth(0.9f).offset(x = (xOffset * 150).dp, y = (yOffset * 300).dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        StationArtwork(
                            model = mediaMetadata?.artworkUri ?: MediaUtils.getStationArtworkUrl(station.favicon, station.countryCode),
                            modifier = Modifier.size(128.dp),
                            shape = RoundedCornerShape(28.dp),
                            contentPadding = 16.dp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = displayTitle,
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        if (displayTitle != station.name) {
                            Text(
                                text = station.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            MiniEqualizer(
                                isPlaying = isPlaying,
                                modifier = Modifier.size(width = 12.dp, height = 11.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            val timeMinutes = (playbackTime / 1000) / 60
                            val timeSeconds = (playbackTime / 1000) % 60
                            Text(
                                text = if (isPlaying) {
                                    stringResource(R.string.playing_state_format, String.format(Locale.getDefault(), "%02d:%02d", timeMinutes, timeSeconds))
                                } else {
                                    stringResource(R.string.paused_state)
                                },
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White.copy(alpha = 0.55f)
                            )
                        }
                        Spacer(modifier = Modifier.height(28.dp))
                        WaveformAnalyzer(isPlaying = isPlaying, type = waveformType)
                    }
                }
                Text(
                    text = stringResource(R.string.tap_to_return),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneFilePicker(
    state: MainViewModel.FilePickerState,
    onNavigate: (File) -> Unit,
    onNavigateUp: () -> Unit,
    onSelected: (File) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.85f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Icon(
                        if (state.isExport) Icons.Default.CreateNewFolder else Icons.Default.Folder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (state.isExport) stringResource(R.string.file_picker_backup) else stringResource(R.string.file_picker_restore),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onDismiss) { Text(stringResource(R.string.file_picker_close)) }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFFFCA28))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(state.currentPath, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(modifier = Modifier.weight(1f)) {
                    item {
                        ListItem(
                            headlineContent = { Text("..", fontWeight = FontWeight.Bold) },
                            supportingContent = { Text(stringResource(R.string.file_picker_parent_dir)) },
                            leadingContent = { Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, tint = Color(0xFFFFCA28)) },
                            modifier = Modifier.clickable { onNavigateUp() }
                        )
                    }
                    items(state.files) { file ->
                        val isPlaylist = file.name.lowercase().let {
                            it.endsWith(".m3u") || it.endsWith(".m3u8") || it.endsWith(".pls") || it.endsWith(".txt")
                        }
                        ListItem(
                            headlineContent = { Text(file.name, fontWeight = if (file.isDirectory) FontWeight.Bold else FontWeight.Normal) },
                            leadingContent = {
                                Icon(
                                    if (file.isDirectory) Icons.Default.Folder else Icons.AutoMirrored.Filled.InsertDriveFile,
                                    contentDescription = null,
                                    tint = if (file.isDirectory) Color(0xFFFFCA28) else if (isPlaylist) MaterialTheme.colorScheme.primary else Color.Gray
                                )
                            },
                            modifier = Modifier.clickable {
                                if (file.isDirectory) onNavigate(file) else onSelected(file)
                            }
                        )
                    }
                }

                if (state.isExport) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("File:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                Text(state.suggestedFileName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            }
                            Button(onClick = { onSelected(File(state.currentPath)) }) {
                                Text(stringResource(R.string.file_picker_save_here))
                            }
                        }
                    }
                }
            }
        }
    }
}
