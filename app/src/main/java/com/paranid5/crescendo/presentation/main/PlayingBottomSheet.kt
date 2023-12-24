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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.domain.media.AudioStatus
import com.paranid5.crescendo.presentation.composition_locals.LocalCurrentPlaylistSheetState
import com.paranid5.crescendo.presentation.composition_locals.LocalPlayingPagerState
import com.paranid5.crescendo.presentation.composition_locals.LocalPlayingSheetState
import com.paranid5.crescendo.presentation.main.appbar.AppBar
import com.paranid5.crescendo.presentation.main.current_playlist.CurrentPlaylistScreen
import com.paranid5.crescendo.presentation.main.playing.PlayingScreen
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import com.paranid5.crescendo.presentation.ui.utils.PushUpButton

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun PlayingBottomSheet(
    alpha: Float,
    modifier: Modifier = Modifier,
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
            sheetContent = { CurrentPlaylistBottomSheet(alpha) },
            sheetBackgroundColor = backgroundColor,
            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Box {
                HorizontalPager(state = playingPagerState!!) { page ->
                    when (page) {
                        0 -> PlayingScreen(
                            coverAlpha = 1 - alpha,
                            audioStatus = AudioStatus.PLAYING,
                        )

                        else -> PlayingScreen(
                            coverAlpha = 1 - alpha,
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
                    alpha = alpha,
                    modifier = Modifier
                        .padding(
                            top = when (LocalConfiguration.current.orientation) {
                                Configuration.ORIENTATION_LANDSCAPE -> 6.dp
                                else -> 12.dp
                            }
                        )
                        .align(Alignment.TopCenter)
                )
            }
        }
    }
}

@Composable
private fun CurrentPlaylistBottomSheet(alpha: Float, modifier: Modifier = Modifier) =
    Box(modifier) {
        PushUpButton(
            alpha = alpha,
            modifier = Modifier
                .padding(top = 12.dp)
                .align(Alignment.TopCenter)
        )

        CurrentPlaylistScreen(
            Modifier
                .fillMaxSize()
                .padding(top = 25.dp)
        )
    }