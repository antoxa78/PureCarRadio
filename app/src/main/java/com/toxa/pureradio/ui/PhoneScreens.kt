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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    LinearProgressIndicator(modifier = Modifier.width(200.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.searching_for_waves),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                }
            }
        }

        error?.let {
            LaunchedEffect(error) {
                delay(5000)
                viewModel.clearError()
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

    currentStation?.let { _ ->
        if (isScreensaverShowing) {
            PhoneScreensaver(viewModel)
        }
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
        val group = genreGroups.find { it.genreName == genreName }
        val isCountry = group?.isCountry == true
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
            selectedTag != null -> {
                val name = selectedTag.name.lowercase().split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
                "$name (${stations.size})"
            }
            selectedCountry != null -> {
                val name = selectedCountry.name.lowercase().split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
                "$name (${stations.size})"
            }
            else -> stringResource(R.string.nav_home)
        }
        val isDeepDive = selectedTag != null || selectedCountry != null

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = if (isDeepDive) FontWeight.Bold else FontWeight.Medium,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PhoneBitrateFilters(
                selectedBitrates = selectedBitrates,
                onToggleFilter = { viewModel.toggleBitrateFilter(it) }
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            if (genreGroups.isNotEmpty()) {
                if (selectedTag == null && selectedCountry == null) {
                    key("phone_home_groups") {
                        PhoneGenreGroupGrid(
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
    val isLoading by viewModel.isLoading.collectAsState()

    if (stations.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.no_stations_found), style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 140.dp),
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(stations, key = { _, station -> station.stationUuid }) { _, station ->
            PhoneStationCard(
                station = station,
                isFavorite = favorites.contains(station.stationUuid),
                isCurrent = currentStation?.stationUuid == station.stationUuid,
                onClick = { viewModel.playStation(station) },
                onLongClick = { onLongClick(station) }
            )
        }

        if (onLoadMore != null) {
            item(key = "load_more", span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { if (!isLoading) onLoadMore() },
                        enabled = !isLoading
                    ) {
                        Text(if (isLoading) stringResource(R.string.loading) else stringResource(R.string.load_more))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhoneStationCard(
    station: Station,
    isFavorite: Boolean,
    isCurrent: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(72.dp),
                    color = Color.White.copy(alpha = 0.05f)
                ) {
                    AsyncImage(
                        model = if (station.favicon.isNotEmpty()) station.favicon else R.drawable.ic_radio_logo,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                        contentScale = ContentScale.Fit,
                        error = coil.compose.rememberAsyncImagePainter(R.drawable.ic_radio_logo)
                    )
                }
                val code = station.countryCode?.trim()?.lowercase()
                if (!code.isNullOrEmpty() && code.length == 2) {
                    AsyncImage(
                        model = "https://flagcdn.com/w40/$code.png",
                        contentDescription = null,
                        modifier = Modifier.size(18.dp).align(Alignment.TopStart),
                        contentScale = ContentScale.Fit
                    )
                }
                if (isFavorite) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp).align(Alignment.TopEnd),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                if (isCurrent) {
                    Icon(
                        Icons.Default.GraphicEq,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp).align(Alignment.BottomEnd),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = station.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = station.votes.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
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
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.no_genres_selected), style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 140.dp),
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(groups, key = { _, group -> group.genreName }) { _, group ->
            PhoneGenreGroupCard(
                group = group.copy(genreName = group.genreName.lowercase().split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }),
                onClick = { onGroupClick(group.genreName) },
                onLongClick = if (onGroupLongClick != null) {{ onGroupLongClick(group.genreName) }} else null
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhoneGenreGroupCard(group: GenreGroup, onClick: () -> Unit, onLongClick: (() -> Unit)? = null) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(containerColor = getPhoneGenreColor(group.genreName))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = getPhoneGenreImageUrl(group.genreName),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.3f
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                        )
                    )
            )
            Column(
                modifier = Modifier.fillMaxSize().padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = group.genreName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${group.filteredCount} / ${group.totalStations}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

fun getPhoneGenreColor(genre: String): Color {
    val hash = genre.hashCode()
    val r = (Math.abs(hash) % 100) + 20
    val g = (Math.abs(hash shr 8) % 100) + 20
    val b = (Math.abs(hash shr 16) % 100) + 20
    return Color(r, g, b)
}

fun getPhoneGenreImageUrl(genre: String): String {
    val g = genre.lowercase().trim()
    return when {
        g.contains("rock") || g.contains("metal") || g.contains("punk") ->
            "https://images.unsplash.com/photo-1498038432885-c6f3f1b912ee?q=80&w=600&auto=format&fit=crop"
        g.contains("pop") || g.contains("hits") || g.contains("top") ->
            "https://images.unsplash.com/photo-1514525253361-bee8a187449a?q=80&w=600&auto=format&fit=crop"
        g.contains("jazz") || g.contains("blues") || g.contains("soul") ->
            "https://images.unsplash.com/photo-1511192336575-5a79af67a629?q=80&w=600&auto=format&fit=crop"
        g.contains("classical") || g.contains("orchestra") || g.contains("opera") ->
            "https://images.unsplash.com/photo-1507838596018-b943e1dd13a9?q=80&w=600&auto=format&fit=crop"
        g.contains("electronic") || g.contains("techno") || g.contains("house") || g.contains("edm") ->
            "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?q=80&w=600&auto=format&fit=crop"
        g.contains("ambient") || g.contains("chill") || g.contains("lounge") ->
            "https://images.unsplash.com/photo-1516280440614-37939bbacd81?q=80&w=600&auto=format&fit=crop"
        g.contains("country") || g.contains("folk") ->
            "https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?q=80&w=600&auto=format&fit=crop"
        g.contains("hip") || g.contains("rap") || g.contains("r&b") ->
            "https://images.unsplash.com/photo-1520262454473-a1a82276a574?q=80&w=600&auto=format&fit=crop"
        g.contains("reggae") || g.contains("ska") ->
            "https://images.unsplash.com/photo-1510915228340-29c85a43dcfe?q=80&w=600&auto=format&fit=crop"
        g.contains("news") || g.contains("talk") ->
            "https://images.unsplash.com/photo-1472289065668-ce650ac443d2?q=80&w=600&auto=format&fit=crop"
        g.contains("80s") || g.contains("90s") || g.contains("70s") || g.contains("retro") ->
            "https://images.unsplash.com/photo-1484755560615-a4c64e99529b?q=80&w=600&auto=format&fit=crop"
        g.contains("soundtrack") || g.contains("movie") ->
            "https://images.unsplash.com/photo-1485846234645-a62644f84728?q=80&w=600&auto=format&fit=crop"
        g.contains("latin") || g.contains("world") ->
            "https://images.unsplash.com/photo-1526218626217-dc65a29bb444?q=80&w=600&auto=format&fit=crop"
        else -> "https://images.unsplash.com/photo-1453090927415-5f45085b65c0?q=80&w=600&auto=format&fit=crop"
    }
}

@Composable
fun PhoneStationListScreen(
    title: String,
    stations: List<Station>,
    viewModel: MainViewModel,
    hasMoreStations: Boolean = false,
    onLongClick: (Station) -> Unit
) {
    val selectedBitrates by viewModel.selectedBitrates.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
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

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = localSearchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it); localSearchQuery = it },
                label = { Text(stringResource(R.string.search_hint)) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.addToRecentSearches(localSearchQuery)
                        keyboardController?.hide()
                    }
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
            Button(
                onClick = { viewModel.toggleSearchMode() },
                colors = if (searchMode == SearchMode.Tag) {
                    androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                } else {
                    androidx.compose.material3.ButtonDefaults.buttonColors()
                }
            ) {
                Text(if (searchMode == SearchMode.Tag) stringResource(R.string.search_mode_tag) else stringResource(R.string.search_mode_name))
            }
        }

        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            PhoneBitrateFilters(
                selectedBitrates = selectedBitrates,
                onToggleFilter = { viewModel.toggleBitrateFilter(it) }
            )
        }

        Box(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
            when {
                stations.isEmpty() && localSearchQuery.isEmpty() && tagSearchGroups.isEmpty() -> {
                    if (recentSearches.isNotEmpty()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(stringResource(R.string.recent_searches), style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(bottom = 8.dp))
                            LazyRow {
                                items(recentSearches) { query ->
                                    Button(
                                        onClick = {
                                            viewModel.onSearchQueryChange(query)
                                            keyboardController?.hide()
                                        },
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) { Text(query) }
                                }
                            }
                        }
                    } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(stringResource(R.string.enter_search_query), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
                searchMode == SearchMode.Tag && tagSearchGroups.isNotEmpty() && selectedSearchTag == null -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = stringResource(R.string.found_stations, stations.size),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(4.dp)
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
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
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
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.no_tag_groups_found))
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 140.dp),
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(groups, key = { _, group -> group.genreName }) { _, group ->
            PhoneGenreGroupCard(
                group = group.copy(genreName = group.genreName.lowercase().split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }),
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
    val selectedBitrates by viewModel.selectedBitrates.collectAsState()
    var localTagSearchQuery by remember { mutableStateOf(tagSearchQuery) }
    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current

    LaunchedEffect(tagSearchQuery) {
        if (tagSearchQuery != localTagSearchQuery) {
            localTagSearchQuery = tagSearchQuery
        }
    }

    if (selectedTag != null) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.selectTag(null) }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_desc))
                }
                Text(
                    text = selectedTag.name.lowercase().split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }
            PhoneStationGrid(
                stations = stations,
                viewModel = viewModel,
                onLoadMore = if (hasMoreStations) {{ viewModel.loadMoreStations() }} else null,
                onLongClick = onStationLongClick
            )
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = localTagSearchQuery,
                onValueChange = { localTagSearchQuery = it; viewModel.setTagSearchQuery(it) },
                label = { Text(stringResource(R.string.search_hint)) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
                trailingIcon = {
                    if (localTagSearchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setTagSearchQuery(""); localTagSearchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.clear_desc))
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.sort_label), style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.width(4.dp))
            GenreSortMode.entries.forEach { mode ->
                FilterChip(
                    selected = genreSortMode == mode,
                    onClick = { viewModel.setGenreSortMode(mode) },
                    label = { Text(if (mode == GenreSortMode.Name) "Name" else "Count", style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

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
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.no_genres_found), style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 140.dp),
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(tags, key = { _, tag -> tag.name }) { _, tag ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .combinedClickable(
                        onClick = { onTagClick(tag) },
                        onLongClick = { onTagLongClick?.invoke(tag) }
                    ),
                colors = CardDefaults.cardColors(containerColor = getPhoneGenreColor(tag.name))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = getPhoneGenreImageUrl(tag.name),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alpha = 0.3f
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                                )
                            )
                    )
                    Column(
                        modifier = Modifier.fillMaxSize().padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = tag.name.lowercase().split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } },
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${tag.stationcount} stations",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
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
    val selectedBitrates by viewModel.selectedBitrates.collectAsState()

    if (selectedCountry != null) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.selectCountry(null) }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_desc))
                }
                Text(
                    text = selectedCountry.name.lowercase().split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }
            PhoneStationGrid(
                stations = stations,
                viewModel = viewModel,
                onLoadMore = if (hasMoreStations) {{ viewModel.loadMoreStations() }} else null,
                onLongClick = onStationLongClick
            )
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 140.dp),
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(countries, key = { _, country -> country.iso_3166_1 }) { _, country ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clickable { viewModel.selectCountry(country) },
                colors = CardDefaults.cardColors(containerColor = getPhoneGenreColor(country.name))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    val flagCode = country.iso_3166_1.lowercase().trim()
                    AsyncImage(
                        model = if (flagCode.isNotEmpty()) "https://flagcdn.com/w160/$flagCode.png"
                                else "https://images.unsplash.com/photo-1526772662000-3f88f10405ff?q=80&w=600&auto=format&fit=crop",
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alpha = 0.25f
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                                )
                            )
                    )
                    Column(
                        modifier = Modifier.fillMaxSize().padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = country.name.lowercase().split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } },
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${country.stationcount} stations",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PhoneBitrateFilters(
    selectedBitrates: Set<BitrateFilter>,
    onToggleFilter: (BitrateFilter) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(stringResource(R.string.filter_label), style = MaterialTheme.typography.labelSmall)
        BitrateFilter.entries.forEach { filter ->
            val labelRes = when (filter) {
                BitrateFilter.Low -> R.string.filter_low
                BitrateFilter.High -> R.string.filter_high
                BitrateFilter.FLAC -> R.string.filter_flac
            }
            FilterChip(
                selected = selectedBitrates.contains(filter),
                onClick = { onToggleFilter(filter) },
                label = { Text(stringResource(labelRes)) },
                modifier = Modifier.padding(horizontal = 2.dp)
            )
        }
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
        else -> PhoneSettingsMain(
            viewModel, appTheme, quitConfirmationEnabled, screensaverEnabled, screensaverTimeout,
            hideBroken, minTagFilter, autoUpdateInterval, audioPassthrough, resumeLastStation,
            defaultStartupCategory,
            serverStats, lastUpdate,
            onImportPlaylist, onExportPlaylist, onPermissionRequest
        )
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
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 140.dp)
    ) {
        item {
            Text(stringResource(R.string.settings_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text(stringResource(R.string.settings_interface).uppercase(), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_theme)) },
                supportingContent = { Text(stringResource(R.string.current_theme, appTheme.name)) },
                leadingContent = { Icon(Icons.Default.TheaterComedy, contentDescription = null) },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable { viewModel.setSettingsSubMenu("AppTheme") }
            )
        }
        item {
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_language)) },
                supportingContent = { Text(stringResource(R.string.settings_language_desc)) },
                leadingContent = { Icon(Icons.Default.Public, contentDescription = null) },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable { viewModel.setSettingsSubMenu("AppLanguage") }
            )
        }
        item {
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_home_genres)) },
                supportingContent = { Text(stringResource(R.string.settings_home_genres_desc)) },
                leadingContent = { Icon(Icons.Default.Home, contentDescription = null) },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable { viewModel.setSettingsSubMenu("HomeGenres") }
            )
        }
        item {
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_startup_category)) },
                supportingContent = { Text(stringResource(R.string.current_category, stringResource(defaultStartupCategory.labelRes))) },
                leadingContent = { Icon(Icons.Default.PlayArrow, contentDescription = null) },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable { viewModel.setSettingsSubMenu("DefaultCategory") }
            )
        }
        item {
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_quit_confirmation)) },
                supportingContent = { Text(stringResource(R.string.settings_quit_confirmation_desc)) },
                leadingContent = { Icon(Icons.Default.Warning, contentDescription = null) },
                trailingContent = { Switch(checked = quitConfirmationEnabled, onCheckedChange = { viewModel.setQuitConfirmationEnabled(it) }) }
            )
        }
        item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }

        item {
            Text(stringResource(R.string.settings_playback).uppercase(), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_screensaver)) },
                supportingContent = { Text(if (screensaverEnabled) stringResource(R.string.screensaver_enabled_fmt, screensaverTimeout) else stringResource(R.string.screensaver_disabled)) },
                leadingContent = { Icon(Icons.Default.MusicVideo, contentDescription = null) },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable { viewModel.setSettingsSubMenu("Screensaver") }
            )
        }
        item {
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_audio_passthrough)) },
                supportingContent = { Text(stringResource(R.string.settings_audio_passthrough_desc)) },
                leadingContent = { Icon(Icons.Default.MusicNote, contentDescription = null) },
                trailingContent = { Switch(checked = audioPassthrough, onCheckedChange = { viewModel.toggleAudioPassthrough() }) }
            )
        }
        item {
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_resume_last)) },
                supportingContent = { Text(stringResource(R.string.settings_resume_last_desc)) },
                leadingContent = { Icon(Icons.Default.PlayArrow, contentDescription = null) },
                trailingContent = { Switch(checked = resumeLastStation, onCheckedChange = { viewModel.setResumeLastStation(it) }) }
            )
        }
        item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }

        item {
            Text(stringResource(R.string.settings_database).uppercase(), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            ListItem(
                headlineContent = { Text(stringResource(R.string.database_smart_filter)) },
                supportingContent = { Text(stringResource(R.string.database_smart_filter_desc)) },
                leadingContent = { Icon(Icons.Default.Public, contentDescription = null) },
                trailingContent = { Switch(checked = hideBroken, onCheckedChange = { viewModel.toggleHideBroken() }) }
            )
        }
        item {
            ListItem(
                headlineContent = { Text(stringResource(R.string.database_min_tags)) },
                supportingContent = { Text(stringResource(R.string.database_min_tags_desc)) },
                leadingContent = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                trailingContent = { Switch(checked = minTagFilter, onCheckedChange = { viewModel.toggleMinTagFilter() }) }
            )
        }
        item {
            ListItem(
                headlineContent = { Text(stringResource(R.string.database_sync)) },
                supportingContent = { Text(stringResource(R.string.database_sync_desc)) },
                leadingContent = { Icon(Icons.Default.History, contentDescription = null) },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable { viewModel.setSettingsSubMenu("AutoUpdate") }
            )
        }
        item {
            ListItem(
                headlineContent = { Text(stringResource(R.string.database_refresh)) },
                supportingContent = {
                    val dateStr = if (lastUpdate > 0) SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(lastUpdate)) else "Never"
                    Text("Last: $dateStr \u2022 ${serverStats?.stations ?: "..."} stations")
                },
                leadingContent = { Icon(Icons.Default.Radio, contentDescription = null) },
                trailingContent = { Icon(Icons.Default.History, contentDescription = null) },
                modifier = Modifier.clickable { viewModel.updateDatabase() }
            )
        }
        item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }

        item {
            Text(stringResource(R.string.data_management).uppercase(), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            ListItem(
                headlineContent = { Text(stringResource(R.string.data_backup)) },
                supportingContent = { Text(stringResource(R.string.data_backup_desc)) },
                leadingContent = { Icon(Icons.Default.CloudUpload, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                modifier = Modifier.clickable { onExportPlaylist() }
            )
        }
        item {
            ListItem(
                headlineContent = { Text(stringResource(R.string.data_restore)) },
                supportingContent = { Text(stringResource(R.string.data_restore_desc)) },
                leadingContent = { Icon(Icons.Default.CloudDownload, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                modifier = Modifier.clickable { onImportPlaylist() }
            )
        }
        item {
            ListItem(
                headlineContent = { Text(stringResource(R.string.data_permissions)) },
                supportingContent = { Text(stringResource(R.string.data_permissions_desc)) },
                leadingContent = { Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                modifier = Modifier.clickable { onPermissionRequest() }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Radio, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("PURE RADIO", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("A Premium Retro Experience", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Build: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(BuildConfig.BUILD_TIME))} \u2022 v${BuildConfig.VERSION_NAME}",
                        style = MaterialTheme.typography.labelSmall, color = Color.Gray
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
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 140.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.setSettingsSubMenu(null) }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_desc))
                }
                Text(stringResource(R.string.settings_startup_category), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        validCategories.forEach { category ->
            item {
                ListItem(
                    headlineContent = { Text(stringResource(category.labelRes)) },
                    leadingContent = {
                        RadioButton(
                            selected = category == currentCategory,
                            onClick = { viewModel.setDefaultStartupCategory(category) }
                        )
                    },
                    modifier = Modifier.clickable { viewModel.setDefaultStartupCategory(category) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneHomeGenresSettings(
    viewModel: MainViewModel, visibleGenres: Set<String>, filteredTags: List<Tag>,
    tagSearchQuery: String, localTagSearchQuery: String, genreSortMode: GenreSortMode,
    keyboardController: androidx.compose.ui.platform.SoftwareKeyboardController?,
    onTagSearchChange: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 140.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.setSettingsSubMenu(null) }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_desc))
                }
                Text(stringResource(R.string.settings_personalize_home), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(stringResource(R.string.sort_label), style = MaterialTheme.typography.bodySmall)
                GenreSortMode.entries.forEach { mode ->
                    FilterChip(
                        selected = genreSortMode == mode,
                        onClick = { viewModel.setGenreSortMode(mode) },
                        label = { Text(mode.name, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            OutlinedTextField(
                value = localTagSearchQuery,
                onValueChange = onTagSearchChange,
                label = { Text(stringResource(R.string.search_hint)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
                trailingIcon = {
                    if (localTagSearchQuery.isNotEmpty()) {
                        IconButton(onClick = { onTagSearchChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.clear_desc))
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(stringResource(R.string.genres_section), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        }
        items(filteredTags) { tag ->
            ListItem(
                headlineContent = {
                    Text(tag.name.lowercase().split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } })
                },
                supportingContent = { Text(stringResource(R.string.stations_count, tag.stationcount)) },
                trailingContent = {
                    Checkbox(checked = visibleGenres.contains(tag.name), onCheckedChange = { viewModel.toggleGenreVisibility(tag.name) })
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
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 140.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.setSettingsSubMenu(null) }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_desc))
                }
                Text(stringResource(R.string.settings_db_update_interval), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
        }
        options.forEach { (hours, labelRes) ->
            item {
                ListItem(
                    headlineContent = { Text(stringResource(labelRes)) },
                    trailingContent = {
                        if (autoUpdateInterval == hours) {
                            Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    },
                    modifier = Modifier.clickable { viewModel.setAutoUpdateInterval(hours); viewModel.setSettingsSubMenu(null) }
                )
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
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 140.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.setSettingsSubMenu(null) }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_desc))
                }
                Text(stringResource(R.string.settings_screensaver_prefs), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
        }
        item {
            ListItem(
                headlineContent = { Text(stringResource(R.string.screensaver_activate)) },
                supportingContent = { Text(stringResource(R.string.screensaver_activate_desc)) },
                trailingContent = { Switch(checked = screensaverEnabled, onCheckedChange = { viewModel.toggleScreensaver(it) }) }
            )
        }
        item {
            Text(stringResource(R.string.screensaver_display_mode), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 8.dp))
        }
        listOf(
            ScreensaverMode.StationInfo to R.string.screensaver_mode_retro,
            ScreensaverMode.BlackScreen to R.string.screensaver_mode_black
        ).forEach { (mode, labelRes) ->
            item {
                ListItem(
                    headlineContent = { Text(stringResource(labelRes)) },
                    trailingContent = {
                        if (screensaverMode == mode) {
                            Icon(Icons.Default.GraphicEq, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    },
                    modifier = Modifier.clickable { viewModel.setScreensaverMode(mode) }
                )
            }
        }
        item {
            Text(stringResource(R.string.screensaver_idle_timeout), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 8.dp))
        }
        timeouts.forEach { minutes ->
            item {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.screensaver_minutes, minutes)) },
                    trailingContent = {
                        if (screensaverTimeout == minutes) {
                            Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    },
                    modifier = Modifier.clickable { viewModel.setScreensaverTimeout(minutes); viewModel.setSettingsSubMenu(null) }
                )
            }
        }
    }
}

@Composable
fun PhoneAppThemeSettings(viewModel: MainViewModel, appTheme: AppTheme) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 140.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.setSettingsSubMenu(null) }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_desc))
                }
                Text(stringResource(R.string.settings_application_theme), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
        }
        items(AppTheme.entries) { theme ->
            val labelRes = when (theme) {
                AppTheme.RetroGold -> R.string.theme_retro_gold
                AppTheme.BlueNeon -> R.string.theme_blue_neon
                AppTheme.Violet -> R.string.theme_violet
                AppTheme.Monochrome -> R.string.theme_monochrome
                AppTheme.Forest -> R.string.theme_forest
                AppTheme.Contrast -> R.string.theme_contrast
                AppTheme.Black -> R.string.theme_black
            }
            val descRes = when (theme) {
                AppTheme.RetroGold -> R.string.theme_retro_gold_desc
                AppTheme.BlueNeon -> R.string.theme_blue_neon_desc
                AppTheme.Violet -> R.string.theme_violet_desc
                AppTheme.Monochrome -> R.string.theme_monochrome_desc
                AppTheme.Forest -> R.string.theme_forest_desc
                AppTheme.Contrast -> R.string.theme_contrast_desc
                AppTheme.Black -> R.string.theme_black_desc
            }
            ListItem(
                headlineContent = { Text(stringResource(labelRes)) },
                supportingContent = { Text(stringResource(descRes)) },
                trailingContent = {
                    if (appTheme == theme) {
                        Icon(Icons.Default.GraphicEq, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                },
                modifier = Modifier.clickable { viewModel.setAppTheme(theme) }
            )
        }
    }
}

@Composable
fun PhoneAppLanguageSettings(viewModel: MainViewModel) {
    val currentLang by viewModel.appLanguage.collectAsState()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 140.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.setSettingsSubMenu(null) }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_desc))
                }
                Text(stringResource(R.string.settings_language), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
        }
        items(AppLanguage.entries) { lang ->
            ListItem(
                headlineContent = { Text(lang.displayName) },
                trailingContent = {
                    if (currentLang == lang) {
                        Icon(Icons.Default.GraphicEq, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                },
                modifier = Modifier.clickable { viewModel.setAppLanguage(lang) }
            )
        }
    }
}

@Composable
fun WaveformAnalyzer(isPlaying: Boolean, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "Waveform")
    val barCount = 40
    Row(
        modifier = modifier.width(260.dp).height(60.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(barCount) { i ->
            val distanceFromCenter = Math.abs(i - barCount / 2).toFloat()
            val centerWeight = 1f - (distanceFromCenter / (barCount / 2))
            val duration = remember { (500..1200).random() }
            val delay = remember { (i * 35) % 800 }
            val heightScale by infiniteTransition.animateFloat(
                initialValue = 0.1f, targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(duration, delay, easing = FastOutLinearInEasing), RepeatMode.Reverse),
                label = "Height_$i"
            )
            val finalHeight = if (isPlaying) (0.1f + 0.9f * heightScale) * (0.2f + 0.8f * centerWeight) else 0.05f
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(finalHeight)
                    .background(
                        brush = Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))),
                        shape = RoundedCornerShape(1.dp)
                    )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneNowPlayingBar(viewModel: MainViewModel, onClick: () -> Unit = {}) {
    val currentStation by viewModel.currentStation.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val mediaMetadata by viewModel.mediaMetadata.collectAsState()
    val playbackTime by viewModel.playbackTime.collectAsState()
    val playbackDuration by viewModel.playbackDuration.collectAsState()
    val audioFormat by viewModel.audioFormat.collectAsState()

    currentStation?.let { station ->
        Surface(
            tonalElevation = 8.dp,
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .clickable { onClick() }
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
                    .height(64.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = if (station.favicon.isNotEmpty()) station.favicon else R.drawable.ic_radio_logo,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp).padding(4.dp),
                    contentScale = ContentScale.Fit
                )
                Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                    val title = if (!mediaMetadata?.title.isNullOrEmpty()) mediaMetadata?.title.toString() else station.name
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = station.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (!mediaMetadata?.artist.isNullOrEmpty()) {
                            Text(
                                text = " \u2022 ${mediaMetadata?.artist}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                val timeMinutes = (playbackTime / 1000) / 60
                val timeSeconds = (playbackTime / 1000) % 60
                Text(
                    text = String.format(Locale.getDefault(), "%02d:%02d", timeMinutes, timeSeconds),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = { viewModel.playPrevious() }) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = stringResource(R.string.previous_desc))
                }
                IconButton(onClick = { viewModel.togglePlayPause() }) {
                    Icon(
                        if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) stringResource(R.string.pause_desc) else stringResource(R.string.play_desc),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = { viewModel.playNext() }) {
                    Icon(Icons.Default.SkipNext, contentDescription = stringResource(R.string.next_desc))
                }
            }
        }
    }
}

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

    currentStation?.let { station ->
        val isFavorite = favorites.contains(station.stationUuid)
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .navigationBarsPadding()
                        .statusBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close_desc))
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    AsyncImage(
                        model = if (station.favicon.isNotEmpty()) station.favicon else R.drawable.ic_radio_logo,
                        contentDescription = null,
                        modifier = Modifier.size(200.dp),
                        contentScale = ContentScale.Fit,
                        error = coil.compose.rememberAsyncImagePainter(R.drawable.ic_radio_logo)
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    val title = if (!mediaMetadata?.title.isNullOrEmpty()) mediaMetadata?.title.toString() else station.name
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )

                    if (!mediaMetadata?.artist.isNullOrEmpty()) {
                        Text(
                            text = mediaMetadata?.artist.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Text(
                        text = station.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val techInfo = audioFormat?.let { format ->
                        buildString {
                            format.codecs?.let { append(it.uppercase()) }
                            if (format.bitrate > 0) {
                                if (isNotEmpty()) append(" \u2022 ")
                                append("${format.bitrate / 1000}k")
                            }
                            if (format.sampleRate > 0) {
                                if (isNotEmpty()) append(" \u2022 ")
                                append("${format.sampleRate / 1000}kHz")
                            }
                        }
                    } ?: if (station.bitrate > 0) "${station.bitrate}k" else station.codec?.uppercase() ?: ""

                    if (techInfo.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = techInfo,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    WaveformAnalyzer(isPlaying = isPlaying)
                    Spacer(modifier = Modifier.weight(1f))

                    if (playbackDuration > 0) {
                        LinearProgressIndicator(
                            progress = { playbackTime.toFloat() / playbackDuration.toFloat() },
                            modifier = Modifier.fillMaxWidth().height(4.dp),
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    val timeMinutes = (playbackTime / 1000) / 60
                    val timeSeconds = (playbackTime / 1000) % 60
                    Text(
                        text = String.format(Locale.getDefault(), "%02d:%02d", timeMinutes, timeSeconds),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.playPrevious() }) {
                            Icon(Icons.Default.SkipPrevious, contentDescription = stringResource(R.string.previous_desc), modifier = Modifier.size(48.dp))
                        }
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(72.dp)
                        ) {
                            IconButton(onClick = { viewModel.togglePlayPause() }) {
                                Icon(
                                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) stringResource(R.string.pause_desc) else stringResource(R.string.play_desc),
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                        IconButton(onClick = { viewModel.playNext() }) {
                            Icon(Icons.Default.SkipNext, contentDescription = stringResource(R.string.next_desc), modifier = Modifier.size(48.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = { viewModel.toggleFavorite(station) }) {
                        Icon(
                            if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) stringResource(R.string.remove_from_fav_desc) else stringResource(R.string.add_to_fav_desc),
                            tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            if (isFavorite) stringResource(R.string.fav_remove_full) else stringResource(R.string.fav_add_full),
                            color = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
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
    val audioFormat by viewModel.audioFormat.collectAsState()
    val mediaMetadata by viewModel.mediaMetadata.collectAsState()

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
                        modifier = Modifier.fillMaxWidth(0.9f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = if (station.favicon.isNotEmpty()) station.favicon else R.drawable.ic_radio_logo,
                            contentDescription = null,
                            modifier = Modifier.size(120.dp),
                            contentScale = ContentScale.Fit,
                            error = coil.compose.rememberAsyncImagePainter(R.drawable.ic_radio_logo)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = displayTitle,
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                        val timeMinutes = (playbackTime / 1000) / 60
                        val timeSeconds = (playbackTime / 1000) % 60
                        Text(
                            text = if (isPlaying) "Playing \u2022 ${String.format(Locale.getDefault(), "%02d:%02d", timeMinutes, timeSeconds)}" else "Paused",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
                Text(
                    text = stringResource(R.string.tap_to_return),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray.copy(alpha = 0.5f),
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
