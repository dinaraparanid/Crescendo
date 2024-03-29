package com.paranid5.crescendo.presentation.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
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
import com.paranid5.crescendo.presentation.UpdateCheckerDialog
import com.paranid5.crescendo.core.impl.presentation.composition_locals.LocalCurrentPlaylistSheetState
import com.paranid5.crescendo.core.impl.presentation.composition_locals.playing.LocalPlayingPagerState
import com.paranid5.crescendo.core.impl.presentation.composition_locals.playing.LocalPlayingSheetState
import com.paranid5.crescendo.presentation.main.appbar.appBarHeight
import com.paranid5.crescendo.core.impl.presentation.permissions.requests.externalStoragePermissionsRequestLauncher
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors

@Composable
fun App(modifier: Modifier = Modifier) {
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

        ScreenScaffold(Modifier.fillMaxSize())
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
private fun ScreenScaffold(modifier: Modifier = Modifier) {
    val backgroundColor = LocalAppColors.current.colorScheme.background

    val playingScaffoldState = rememberBottomSheetScaffoldState()

    val curPlaylistScaffoldState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
    )

    val playingPagerState = rememberPagerState { 2 }

    val alpha = bottomSheetPushAlpha(playingScaffoldState)

    CompositionLocalProvider(
        LocalPlayingSheetState provides playingScaffoldState,
        LocalCurrentPlaylistSheetState provides curPlaylistScaffoldState,
        LocalPlayingPagerState provides playingPagerState
    ) {
        BottomSheetScaffold(
            modifier = modifier,
            scaffoldState = playingScaffoldState,
            sheetPeekHeight = appBarHeight,
            backgroundColor = backgroundColor,
            sheetBackgroundColor = backgroundColor,
            sheetContent = {
                PlayingBottomSheet(alpha)
            },
            content = { ContentScreen(padding = it) }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
private fun bottomSheetPushAlpha(state: BottomSheetScaffoldState): Float {
    val sheetState = state.bottomSheetState
    val progress = sheetState.progress
    val targetValue = sheetState.targetValue
    val currentValue = sheetState.currentValue

    return when {
        currentValue == BottomSheetValue.Collapsed && targetValue == BottomSheetValue.Collapsed -> 1F
        currentValue == BottomSheetValue.Expanded && targetValue == BottomSheetValue.Expanded -> 0F
        currentValue == BottomSheetValue.Expanded && targetValue == BottomSheetValue.Collapsed -> progress
        else -> 1 - progress
    }
}