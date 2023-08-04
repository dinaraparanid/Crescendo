package com.paranid5.crescendo.presentation.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.presentation.Screens
import com.paranid5.crescendo.presentation.UpdateCheckerDialog
import com.paranid5.crescendo.presentation.appbar.AppBar
import com.paranid5.crescendo.presentation.appbar.appBarHeight
import com.paranid5.crescendo.presentation.composition_locals.LocalCurrentPlaylistSheetState
import com.paranid5.crescendo.presentation.composition_locals.LocalPlayingSheetState
import com.paranid5.crescendo.presentation.current_playlist.CurrentPlaylistScreen
import com.paranid5.crescendo.presentation.playing.PlayingScreen
import com.paranid5.crescendo.presentation.playing.PlayingViewModel
import com.paranid5.crescendo.presentation.ui.permissions.requests.externalStoragePermissionsRequestLauncher
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import com.paranid5.crescendo.presentation.ui.theme.TransparentUtility
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.androidx.compose.koinViewModel

@Composable
fun App(curScreenState: MutableStateFlow<Screens>, modifier: Modifier = Modifier) {
    val isExternalStoragePermissionDialogShownState = remember { mutableStateOf(true) }

    Box(modifier) {
        UpdateCheckerDialog(Modifier.align(Alignment.Center))

        val (areStoragePermissionsGranted, launchStoragePermissions) = externalStoragePermissionsRequestLauncher(
            isExternalStoragePermissionDialogShownState,
            modifier = Modifier.align(Alignment.Center)
        )

        LaunchedEffect(Unit) {
            if (!areStoragePermissionsGranted)
                launchStoragePermissions()
        }

        ScreenScaffold(
            curScreenState = curScreenState,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ScreenScaffold(
    curScreenState: MutableStateFlow<Screens>,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = LocalAppColors.current.value.background

    val playingScaffoldState = rememberBottomSheetScaffoldState()
    val sheetState = playingScaffoldState.bottomSheetState
    val progress = sheetState.progress
    val targetValue = sheetState.targetValue
    val currentValue = sheetState.currentValue

    val curPlaylistScaffoldState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
    )

    val alpha = when {
        currentValue == BottomSheetValue.Collapsed && targetValue == BottomSheetValue.Collapsed -> 1F
        currentValue == BottomSheetValue.Expanded && targetValue == BottomSheetValue.Expanded -> 0F
        currentValue == BottomSheetValue.Expanded && targetValue == BottomSheetValue.Collapsed -> progress
        else -> 1 - progress
    }

    CompositionLocalProvider(
        LocalPlayingSheetState provides playingScaffoldState,
        LocalCurrentPlaylistSheetState provides curPlaylistScaffoldState
    ) {
        BottomSheetScaffold(
            modifier = modifier,
            scaffoldState = playingScaffoldState,
            sheetPeekHeight = appBarHeight,
            backgroundColor = backgroundColor,
            sheetBackgroundColor = backgroundColor,
            sheetContent = { PlayingBottomSheet(alpha) },
            content = { ContentScreen(padding = it, curScreenState) }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PlayingBottomSheet(
    alpha: Float,
    modifier: Modifier = Modifier,
    playingViewModel: PlayingViewModel = koinViewModel(),
) {
    val backgroundColor = LocalAppColors.current.value.background
    val curPlaylistSheetState = LocalCurrentPlaylistSheetState.current

    val playingSheetState = LocalPlayingSheetState.current
    val sheetState = playingSheetState?.bottomSheetState
    val targetValue = sheetState?.targetValue
    val currentValue = sheetState?.currentValue
    val isBarNotVisible = currentValue == BottomSheetValue.Expanded
            && targetValue == BottomSheetValue.Expanded

    curPlaylistSheetState?.let { curPlaylistScaffoldState ->
        ModalBottomSheetLayout(
            modifier = modifier,
            sheetState = curPlaylistScaffoldState,
            sheetContent = { CurrentPlaylistBottomSheet() },
            sheetBackgroundColor = backgroundColor,
            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Box {
                PlayingScreen(
                    coverAlpha = 1 - alpha,
                    viewModel = playingViewModel,
                    modifier = Modifier.align(Alignment.TopCenter)
                )

                if (!isBarNotVisible)
                    AppBar(
                        Modifier
                            .align(Alignment.TopCenter)
                            .alpha(alpha)
                    )

                PushUpButton(
                    Modifier
                        .padding(top = 12.dp)
                        .align(Alignment.TopCenter)
                )
            }
        }
    }
}

@Composable
private fun CurrentPlaylistBottomSheet(modifier: Modifier = Modifier) =
    Box(modifier) {
        CurrentPlaylistScreen(
            Modifier
                .fillMaxSize()
                .padding(top = 25.dp)
        )

        PushUpButton(
            Modifier
                .padding(top = 12.dp)
                .align(Alignment.TopCenter)
        )
    }

@Composable
private fun PushUpButton(modifier: Modifier = Modifier) = Canvas(
    onDraw = { drawRect(TransparentUtility) },
    modifier = modifier
        .width(35.dp)
        .height(4.dp)
        .clip(RoundedCornerShape(10.dp))
)