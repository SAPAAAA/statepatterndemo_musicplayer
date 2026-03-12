package com.example.musicplayer

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.musicplayer.R
import com.example.musicplayer.domain.SimpleState
import kotlin.math.hypot
import kotlin.math.roundToInt

enum class MusicPlayerScreen(@field:StringRes val title: Int) {
    Main(title = R.string.app_name),
    NowPlaying(title = R.string.now_playing)
}

@Composable
fun MusicPlayerApp(
    onLaunchFilePicker: ((Uri) -> Unit) -> Unit,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current.applicationContext
    val viewModel: com.example.musicplayer.ui.PlayerViewModel = viewModel(
        factory = _root_ide_package_.com.example.musicplayer.ui.PlayerViewModelFactory(context)
    )
    val uiState by viewModel.uiState.collectAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = MusicPlayerScreen.valueOf(
        backStackEntry?.destination?.route ?: MusicPlayerScreen.Main.name
    )

    Scaffold(
        topBar = {
            MusicPlayerAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                onLockClick = { viewModel.onLockClick() },
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MusicPlayerScreen.Main.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            val playerActions = _root_ide_package_.com.example.musicplayer.ui.PlayerActions(
                onPlayPauseClick = { viewModel.onPlayPauseClick() },
                onNextClick = { viewModel.onNextClick() },
                onPrevClick = { viewModel.onPrevClick() },
                onSeekClick = { viewModel.onSeekClick(it) },
                onLockClick = { viewModel.onLockClick() }
            )
            composable(route = MusicPlayerScreen.Main.name) { _ ->
                _root_ide_package_.com.example.musicplayer.ui.SongListScreen(
                    modifier = Modifier
                        .fillMaxHeight(),
                    uiState = uiState,
                    onSongSelected = { viewModel.onSongSelected(uriString = it) },
                    onAddSongClick = { onLaunchFilePicker { viewModel.onNewSongSelected(it.toString()) } },
                    playerActions = playerActions,
                    navigateToNowPlaying = { navController.navigate(MusicPlayerScreen.NowPlaying.name) }
                )
            }
            composable(route = MusicPlayerScreen.NowPlaying.name) {
                _root_ide_package_.com.example.musicplayer.ui.NowPlayingScreen(
                    modifier = Modifier
                        .fillMaxHeight(),
                    uiState = uiState,
                    playerActions = playerActions
                )
            }
        }
    }

    if (uiState.currentState == SimpleState.LOCKED) {
        LockedVisualOverlay(
            modifier = Modifier.fillMaxSize(),
            onUnlockClick = { viewModel.onLockClick() }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicPlayerAppBar(
    modifier: Modifier = Modifier,
    currentScreen: MusicPlayerScreen,
    canNavigateBack: Boolean,
    onLockClick: () -> Unit,
    navigateUp: () -> Unit
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onLockClick) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Lock App",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    )
}

@Composable
fun LockedVisualOverlay(
    modifier: Modifier = Modifier,
    onUnlockClick: () -> Unit = { }
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                shape = MaterialTheme.shapes.large
            )
            .pointerInput(Unit) {
                detectTapGestures {  }
            },
        contentAlignment = Alignment.Center
    ) {
        val trackWidth = 250.dp
        val thumbSize = 64.dp

        // Convert dp to pixels
        val maxDragPx = with(LocalDensity.current) { (trackWidth - thumbSize).toPx() }
        var offsetY by remember { mutableFloatStateOf(0F) }

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(50))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            if (offsetY < -maxDragPx * 0.8F) {
                                onUnlockClick()
                            }
                            offsetY = 0F
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        offsetY = (offsetY + dragAmount.y).coerceIn(-maxDragPx, 0F)
                    }
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.small_padding)),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "App Locked",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = "Swipe to unlock",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )
                Box(
                    modifier = Modifier
                        .offset { IntOffset(0, offsetY.roundToInt()) }
                        .size(thumbSize)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Swipe arrow",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}
