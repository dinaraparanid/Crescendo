package com.paranid5.crescendo.presentation.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.presentation.UpdateCheckerDialog
import com.paranid5.crescendo.ui.appbar.appBarHeight
import com.paranid5.crescendo.ui.composition_locals.LocalCurrentPlaylistSheetState
import com.paranid5.crescendo.ui.composition_locals.playing.LocalPlayingPagerState
import com.paranid5.crescendo.ui.composition_locals.playing.LocalPlayingSheetState
import com.paranid5.crescendo.ui.permissions.requests.externalStoragePermissionsRequestLauncher
import com.paranid5.crescendo.utils.extensions.collectLatestAsState
import com.paranid5.crescendo.view_model.MainViewModel

private const val PLAYING_PAGER_SCREENS_AMOUNT = 2

@Composable
internal fun App(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.stateFlow.collectLatestAsState()
    val onUiIntent = viewModel::onUiIntent
    val isExternalStoragePermissionDialogShownState = remember { mutableStateOf(true) }

    Box(modifier) {
        UpdateCheckerDialog(
            state = state,
            onUiIntent = onUiIntent,
            modifier = Modifier.align(Alignment.Center),
        )

        val (areStoragePermissionsGranted, launchStoragePermissions) =
            externalStoragePermissionsRequestLauncher(
                isExternalStoragePermissionDialogShownState,
                modifier = Modifier.align(Alignment.Center),
            )

        LaunchedEffect(areStoragePermissionsGranted) {
            if (areStoragePermissionsGranted.not())
                launchStoragePermissions()
        }

        ScreenScaffold(Modifier.fillMaxSize())
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
private fun ScreenScaffold(modifier: Modifier = Modifier) {
    val playingScaffoldState = rememberBottomSheetScaffoldState()

    val curPlaylistScaffoldState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
    )

    val playingPagerState = rememberPagerState { PLAYING_PAGER_SCREENS_AMOUNT }

    val alpha = bottomSheetPushAlpha(playingScaffoldState)

    CompositionLocalProvider(
        LocalPlayingSheetState provides playingScaffoldState,
        LocalCurrentPlaylistSheetState provides curPlaylistScaffoldState,
        LocalPlayingPagerState provides playingPagerState,
    ) {
        BottomSheetScaffold(
            modifier = modifier.background(colors.background.gradient),
            scaffoldState = playingScaffoldState,
            sheetPeekHeight = appBarHeight,
            backgroundColor = Color.Transparent,
            sheetBackgroundColor = Color.Transparent,
            sheetContent = { PlayingBottomSheet(alpha) },
            content = { padding ->
                val layoutDirection = LocalLayoutDirection.current

                ContentScreen(
                    Modifier.padding(
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding(),
                        start = padding.calculateStartPadding(layoutDirection),
                        end = padding.calculateEndPadding(layoutDirection),
                    )
                )
            },
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
