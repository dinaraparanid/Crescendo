package com.paranid5.crescendo.presentation.main

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.presentation.composition_locals.LocalCurrentPlaylistSheetState
import com.paranid5.crescendo.presentation.composition_locals.playing.LocalPlayingPagerState
import com.paranid5.crescendo.presentation.composition_locals.playing.LocalPlayingSheetState
import com.paranid5.crescendo.presentation.main.appbar.AppBar
import com.paranid5.crescendo.presentation.main.current_playlist.CurrentPlaylistScreen
import com.paranid5.crescendo.presentation.main.playing.PlayingScreen
import com.paranid5.crescendo.presentation.main.playing.PlayingViewModel
import com.paranid5.crescendo.presentation.ui.LocalAppColors
import com.paranid5.crescendo.presentation.ui.utils.PushUpButton
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun PlayingBottomSheet(
    alpha: Float,
    modifier: Modifier = Modifier,
    playingViewModel: PlayingViewModel = koinViewModel()
) {
    val backgroundColor = LocalAppColors.current.background
    val curPlaylistSheetState = LocalCurrentPlaylistSheetState.current
    val playingPagerState = LocalPlayingPagerState.current

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
            sheetContent = {
                CurrentPlaylistBottomSheet(
                    alpha = alpha,
                    state = curPlaylistScaffoldState
                )
            },
            sheetBackgroundColor = backgroundColor,
            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Box {
                HorizontalPager(state = playingPagerState!!) { page ->
                    when (page) {
                        0 -> PlayingScreen(
                            viewModel = playingViewModel,
                            coverAlpha = 1 - alpha,
                            audioStatus = AudioStatus.PLAYING,
                            modifier = modifier.fillMaxSize()
                        )

                        else -> PlayingScreen(
                            viewModel = playingViewModel,
                            coverAlpha = 1 - alpha,
                            audioStatus = AudioStatus.STREAMING,
                            modifier = modifier.fillMaxSize()
                        )
                    }
                }

                if (!isBarNotVisible)
                    AppBar(
                        Modifier
                            .align(Alignment.TopCenter)
                            .alpha(alpha)
                    )

                PushUpButton(
                    alpha = alpha,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = pushUpPadding)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CurrentPlaylistBottomSheet(
    alpha: Float,
    state: ModalBottomSheetState,
    modifier: Modifier = Modifier,
) = Box(modifier) {
    PushUpButton(
        alpha = alpha,
        modifier = Modifier
            .padding(top = 12.dp)
            .align(Alignment.TopCenter)
    )

    CurrentPlaylistScreen(
        Modifier
            .fillMaxSize()
            .padding(top = contentTopPadding(state))
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun contentTopPadding(sheetState: ModalBottomSheetState) =
    when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> 24.dp

        else -> {
            val progress = sheetState.progress
            val targetValue = sheetState.targetValue
            val currentValue = sheetState.currentValue

            when {
                targetValue == ModalBottomSheetValue.Hidden -> 8.dp
                currentValue == ModalBottomSheetValue.Hidden && targetValue == ModalBottomSheetValue.HalfExpanded -> 8.dp
                currentValue == ModalBottomSheetValue.HalfExpanded && targetValue == ModalBottomSheetValue.HalfExpanded -> 8.dp
                currentValue == ModalBottomSheetValue.Expanded && targetValue == ModalBottomSheetValue.Expanded -> 32.dp
                currentValue == ModalBottomSheetValue.Expanded && targetValue == ModalBottomSheetValue.HalfExpanded -> ((1 - progress) * 24 + 8).dp
                else -> (progress * 24 + 8).dp
            }
        }
    }

private inline val pushUpPadding
    @Composable
    get() = when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> 6.dp
        else -> 12.dp
    }