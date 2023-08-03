package com.paranid5.mediastreamer.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.paranid5.mediastreamer.presentation.Screens
import com.paranid5.mediastreamer.presentation.UpdateCheckerDialog
import com.paranid5.mediastreamer.presentation.appbar.AppBar
import com.paranid5.mediastreamer.presentation.appbar.appBarHeight
import com.paranid5.mediastreamer.presentation.playing.PlayingScreen
import com.paranid5.mediastreamer.presentation.playing.PlayingViewModel
import com.paranid5.mediastreamer.presentation.ui.permissions.requests.externalStoragePermissionsRequestLauncher
import com.paranid5.mediastreamer.presentation.ui.theme.LocalAppColors
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
    val scaffoldState = rememberBottomSheetScaffoldState()
    val sheetState = scaffoldState.bottomSheetState
    val progress = sheetState.progress
    val targetValue = sheetState.targetValue
    val currentValue = sheetState.currentValue

    val alpha = when {
        currentValue == BottomSheetValue.Collapsed && targetValue == BottomSheetValue.Collapsed -> 1F
        currentValue == BottomSheetValue.Expanded && targetValue == BottomSheetValue.Expanded -> 0F
        currentValue == BottomSheetValue.Collapsed && targetValue == BottomSheetValue.Expanded -> 1 - progress
        else -> progress
    }

    BottomSheetScaffold(
        sheetContent = { PlayingBottomSheet(alpha) },
        content = { ContentScreen(padding = it, curScreenState) },
        modifier = modifier,
        scaffoldState = scaffoldState,
        sheetPeekHeight = appBarHeight,
        backgroundColor = backgroundColor,
        sheetBackgroundColor = backgroundColor
    )
}

@Composable
private fun PlayingBottomSheet(
    alpha: Float,
    modifier: Modifier = Modifier,
    playingViewModel: PlayingViewModel = koinViewModel(),
) {
    Box(modifier) {
        PlayingScreen(
            coverAlpha = 1 - alpha,
            viewModel = playingViewModel,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        AppBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .alpha(alpha)
        )
    }
}