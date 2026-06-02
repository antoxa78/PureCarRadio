package com.toxa.pureradio

import android.os.Bundle
import android.net.Uri
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.graphics.drawable.Icon
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.util.Rational
import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Intent
import androidx.activity.viewModels
import kotlin.system.exitProcess
import kotlinx.coroutines.launch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.platform.LocalContext
import com.toxa.pureradio.ui.TvMainLayout
import com.toxa.pureradio.ui.SplashScreen
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
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicVideo
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.TheaterComedy
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.Settings as SystemSettings
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged

import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import android.app.UiModeManager
import androidx.compose.material3.MaterialTheme as PhoneMaterialTheme
import androidx.compose.material3.Surface as PhoneSurface
import androidx.compose.material3.Text as PhoneText
import androidx.compose.material3.Icon as PhoneIcon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue as PhoneDrawerValue
import androidx.compose.material3.rememberDrawerState as rememberPhoneDrawerState
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem as PhoneNavigationDrawerItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Card as PhoneCard
import androidx.compose.material3.CardDefaults as PhoneCardDefaults
import androidx.compose.material3.Button as PhoneButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox as PhoneCheckbox
import androidx.compose.material3.Switch as PhoneSwitch
import androidx.compose.material3.ListItem as PhoneListItem
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.tv.material3.Button
import androidx.tv.material3.Card
import androidx.tv.material3.Checkbox
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.NavigationDrawer
import androidx.tv.material3.NavigationDrawerItem
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.tv.material3.rememberDrawerState
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import androidx.tv.material3.Switch
import coil.compose.AsyncImage
import com.toxa.pureradio.R
import com.toxa.pureradio.data.model.Station
import com.toxa.pureradio.network.Country
import com.toxa.pureradio.network.Tag
import com.toxa.pureradio.ui.theme.PureRadioTheme
import com.toxa.pureradio.ui.PhoneMainScreen
import com.toxa.pureradio.ui.PhoneNowPlayingBar
import com.toxa.pureradio.ui.PhoneNowPlayingDialog
import com.toxa.pureradio.ui.viewmodel.BitrateFilter
import com.toxa.pureradio.ui.viewmodel.GenreGroup
import com.toxa.pureradio.ui.viewmodel.MainViewModel
import com.toxa.pureradio.ui.viewmodel.NavigationItem
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalTvMaterial3Api::class)
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val isInPipMode = mutableStateOf(false)

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("pure_radio_prefs", Context.MODE_PRIVATE)
        val langCode = prefs.getString("app_language", "en") ?: "en"
        val locale = Locale.forLanguageTag(langCode)
        Locale.setDefault(locale)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    fun isTv(): Boolean {
        val uiModeManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        return uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
    }

    private val stopReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "ACTION_STOP_RADIO") {
                viewModel.stopPlayback()
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(0),
            navigationBarStyle = SystemBarStyle.dark(0)
        )
        if (Build.VERSION.SDK_INT >= 34) {
            registerReceiver(stopReceiver, IntentFilter("ACTION_STOP_RADIO"), Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(stopReceiver, IntentFilter("ACTION_STOP_RADIO"))
        }
        setContent {
            val isPip by isInPipMode
            val isInitialized by viewModel.isInitialized.collectAsState()
            var splashElapsed by remember { mutableStateOf(false) }
            val context = LocalContext.current
            
            LaunchedEffect(Unit) {
                viewModel.languageChangeEvent.collect {
                    (context as? ComponentActivity)?.recreate()
                }
            }

            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (!Environment.isExternalStorageManager()) {
                        try {
                            val intent = Intent(
                                SystemSettings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                                Uri.parse("package:${context.packageName}")
                            )
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            try {
                                val intent = Intent(SystemSettings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                                context.startActivity(intent)
                            } catch (e2: Exception) {}
                        }
                    }
                } else {
                    val permission = android.Manifest.permission.READ_EXTERNAL_STORAGE
                    if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        permissionLauncher.launch(permission)
                    }
                }
            }

            LaunchedEffect(Unit) {
                delay(3000)
                splashElapsed = true
            }
            val showSplash by remember { derivedStateOf { !isInitialized || !splashElapsed } }
            val appTheme by viewModel.appTheme.collectAsState()
            PureRadioTheme(theme = appTheme) {
                if (isTv()) {
                    TvMainLayout(isPip, showSplash, viewModel)
                } else {
                    PhoneMainLayout(isPip, showSplash, viewModel)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try { unregisterReceiver(stopReceiver) } catch (_: Exception) {}
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        updatePipParams()
    }

    private fun updatePipParams() {
        if (viewModel.isPlaying.value) {
            val station = viewModel.currentStation.value ?: return
            val metadata = viewModel.mediaMetadata.value

            val stopIntent = PendingIntent.getBroadcast(
                this,
                1,
                Intent("ACTION_STOP_RADIO"),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            val stopAction = RemoteAction(
                Icon.createWithResource(this, android.R.drawable.ic_menu_close_clear_cancel),
                "Close",
                "Stop Radio",
                stopIntent
            )

            val openIntent = PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )
            val openAction = RemoteAction(
                Icon.createWithResource(this, android.R.drawable.ic_menu_revert),
                "Open",
                "Open App",
                openIntent
            )

            val builder = PictureInPictureParams.Builder()
                .setAspectRatio(Rational(239, 100))
                .setActions(listOf(openAction, stopAction))

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                builder.setAutoEnterEnabled(true)
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                val title = if (!metadata?.title.isNullOrEmpty()) metadata?.title.toString() else station.name
                builder.setTitle(title)
                builder.setSubtitle(station.name)
            }

            val params = builder.build()
            setPictureInPictureParams(params)

            if (android.os.Build.VERSION.SDK_INT >= 34) {
                 enterPictureInPictureMode(params)
            }
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        isInPipMode.value = isInPictureInPictureMode
    }
}

@kotlin.OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun PhoneMainLayout(isPip: Boolean, showSplash: Boolean, viewModel: MainViewModel) {
    if (showSplash) {
        SplashScreen()
        return
    }

    val selectedNavItem by viewModel.selectedNavItem.collectAsState()
    val quitConfirmationEnabled by viewModel.quitConfirmationEnabled.collectAsState()
    val settingsSubMenu by viewModel.settingsSubMenu.collectAsState()
    val filePickerState by viewModel.filePickerState.collectAsState()
    val pendingImportStations by viewModel.pendingImportStations.collectAsState()
    var showExitDialog by remember { mutableStateOf(false) }
    var showNowPlaying by remember { mutableStateOf(false) }

    BackHandler {
        viewModel.resetScreensaverTimer()
        when {
            pendingImportStations != null -> viewModel.cancelRestore()
            filePickerState != null -> viewModel.closeFilePicker()
            settingsSubMenu != null -> viewModel.setSettingsSubMenu(null)
            selectedNavItem != NavigationItem.Home -> viewModel.selectNavigationItem(NavigationItem.Home)
            else -> {
                if (quitConfirmationEnabled) {
                    showExitDialog = true
                } else {
                    exitProcess(0)
                }
            }
        }
    }

    Scaffold(
        topBar = { PhoneTopBar(selectedNavItem, quitConfirmationEnabled, viewModel, onShowExitDialog = { showExitDialog = true }) },
        bottomBar = {
            val currentStation by viewModel.currentStation.collectAsState()
            if (currentStation != null) {
                PhoneNowPlayingBar(viewModel, onClick = { showNowPlaying = true })
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            awaitFirstDown(requireUnconsumed = false)
                            viewModel.resetScreensaverTimer()
                        }
                    }
            ) {
                PhoneMainScreen(viewModel)
            }
        }
    }

    if (showNowPlaying) {
        PhoneNowPlayingDialog(
            viewModel = viewModel,
            onDismiss = { showNowPlaying = false }
        )
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            icon = { PhoneIcon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(40.dp), tint = PhoneMaterialTheme.colorScheme.primary) },
            title = { PhoneText(stringResource(R.string.exit_title), fontWeight = FontWeight.Bold) },
            confirmButton = {
                TextButton(onClick = { exitProcess(0) }) {
                    PhoneText(stringResource(R.string.yes), fontWeight = FontWeight.Bold, color = PhoneMaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    PhoneText(stringResource(R.string.no), fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@kotlin.OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun PhoneTopBar(
    selectedNavItem: NavigationItem,
    quitConfirmationEnabled: Boolean,
    viewModel: MainViewModel,
    onShowExitDialog: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = { PhoneText("Pure Radio") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PhoneMaterialTheme.colorScheme.surface
        ),
        navigationIcon = {
            Box(modifier = Modifier.statusBarsPadding()) {
                IconButton(onClick = { menuExpanded = true }) {
                    PhoneIcon(
                        Icons.Default.MoreVert, 
                        contentDescription = "Menu",
                        tint = PhoneMaterialTheme.colorScheme.onSurface
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    NavigationItem.entries.forEach { item ->
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
                        DropdownMenuItem(
                            text = { PhoneText(stringResource(item.labelRes), style = PhoneMaterialTheme.typography.titleLarge) },
                            leadingIcon = { PhoneIcon(icon, contentDescription = null, modifier = Modifier.size(36.dp)) },
                            modifier = Modifier.height(64.dp),
                            onClick = {
                                if (item == NavigationItem.Exit) {
                                    if (quitConfirmationEnabled) {
                                        menuExpanded = false
                                        onShowExitDialog()
                                    } else {
                                        exitProcess(0)
                                    }
                                } else {
                                    menuExpanded = false
                                    viewModel.selectNavigationItem(item)
                                }
                            }
                        )
                    }
                }
            }
        }
    )
}
