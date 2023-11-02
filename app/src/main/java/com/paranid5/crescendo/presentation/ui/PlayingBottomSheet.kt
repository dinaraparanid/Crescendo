package com.paranid5.crescendo.presentation.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.presentation.appbar.AppBar
import com.paranid5.crescendo.presentation.composition_locals.LocalCurrentPlaylistSheetState
import com.paranid5.crescendo.presentation.composition_locals.LocalPlayingSheetState
import com.paranid5.crescendo.presentation.current_playlist.CurrentPlaylistScreen
import com.paranid5.crescendo.presentation.playing.PlayingScreen
import com.paranid5.crescendo.presentation.playing.PlayingViewModel
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import com.paranid5.crescendo.presentation.ui.theme.TransparentUtility
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun PlayingBottomSheet(
    alpha: Float,
    modifier: Modifier = Modifier,
    playingViewModel: PlayingViewModel = koinViewModel()
) {
    val backgroundColor = LocalAppColors.current.value.background
    val curPlaylistSheetState = LocalCurrentPlaylistSheetState.current
    val playingPagerState = rememberPagerState { 2 }

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
                HorizontalPager(state = playingPagerState) { page ->
                    when (page) {
                        0 -> PlayingScreen(
                            coverAlpha = 1 - alpha,
                            viewModel = playingViewModel,
                            audioStatus = AudioStatus.PLAYING,
                        )

                        else -> PlayingScreen(
                            coverAlpha = 1 - alpha,
                            viewModel = playingViewModel,
                            audioStatus = AudioStatus.STREAMING,
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