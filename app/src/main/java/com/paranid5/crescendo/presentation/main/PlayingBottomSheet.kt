package com.paranid5.crescendo.presentation.main

import android.content.res.Configuration
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.current_playlist.presentation.CurrentPlaylistScreen
import com.paranid5.crescendo.playing.presentation.PlayingScreen
import com.paranid5.crescendo.presentation.main.appbar.AppBar
import com.paranid5.crescendo.ui.appbar.appBarHeight
import com.paranid5.crescendo.ui.composition_locals.LocalCurrentPlaylistSheetState
import com.paranid5.crescendo.ui.composition_locals.playing.LocalPlayingPagerState
import com.paranid5.crescendo.ui.composition_locals.playing.LocalPlayingSheetState
import com.paranid5.crescendo.ui.utils.PushUpButton

private const val COLLAPSED_PADDING = 8F
private const val EXPANDED_PADDING = 24F

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun PlayingBottomSheet(
    alpha: Float,
    modifier: Modifier = Modifier,
) {
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
                    state = curPlaylistScaffoldState,
                    modifier = Modifier.background(colors.background.gradient),
                )
            },
            sheetBackgroundColor = Color.Transparent,
            sheetShape = RoundedCornerShape(
                topStart = dimensions.corners.extraMedium,
                topEnd = dimensions.corners.extraMedium,
            )
        ) {
            Box(Modifier.fillMaxWidth()) {
                HorizontalPager(state = playingPagerState!!) { page ->
                    when (page) {
                        0 -> PlayingScreen(
                            coverAlpha = 1 - alpha,
                            audioStatus = AudioStatus.PLAYING,
                            modifier = modifier.fillMaxSize(),
                        )

                        else -> PlayingScreen(
                            coverAlpha = 1 - alpha,
                            audioStatus = AudioStatus.STREAMING,
                            modifier = modifier.fillMaxSize(),
                        )
                    }
                }

                if (isBarNotVisible.not())
                    AppBar(
                        Modifier
                            .clip(
                                RoundedCornerShape(
                                    topStart = dimensions.corners.extraMedium,
                                    topEnd = dimensions.corners.extraMedium,
                                )
                            )
                            .fillMaxWidth()
                            .heightIn(min = appBarHeight)
                            .align(Alignment.TopCenter)
                            .alpha(alpha),
                    )

                PushUpButton(
                    alpha = alpha,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = pushUpTopPadding),
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
) {
    val contentTopPadding by animateContentTopPaddingAsState(state)

    Box(modifier) {
        PushUpButton(
            alpha = alpha,
            modifier = Modifier
                .padding(top = dimensions.padding.medium)
                .align(Alignment.TopCenter)
        )

        CurrentPlaylistScreen(
            Modifier
                .fillMaxSize()
                .padding(top = contentTopPadding),
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun animateContentTopPaddingAsState(sheetState: ModalBottomSheetState) = animateDpAsState(
    when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> dimensions.padding.extraBig

        else -> {
            val progress = sheetState.progress
            val targetValue = sheetState.targetValue
            val currentValue = sheetState.currentValue

            when {
                targetValue == ModalBottomSheetValue.Hidden -> COLLAPSED_PADDING

                currentValue == ModalBottomSheetValue.Hidden &&
                        targetValue == ModalBottomSheetValue.HalfExpanded -> COLLAPSED_PADDING

                currentValue == ModalBottomSheetValue.HalfExpanded &&
                        targetValue == ModalBottomSheetValue.HalfExpanded ->
                    COLLAPSED_PADDING

                currentValue == ModalBottomSheetValue.Expanded &&
                        targetValue == ModalBottomSheetValue.Expanded ->
                    EXPANDED_PADDING + COLLAPSED_PADDING

                currentValue == ModalBottomSheetValue.Expanded &&
                        targetValue == ModalBottomSheetValue.HalfExpanded ->
                    (1 - progress) * EXPANDED_PADDING + COLLAPSED_PADDING

                else -> progress * EXPANDED_PADDING + COLLAPSED_PADDING
            }.dp
        }
    }, label = ""
)

private inline val pushUpTopPadding
    @Composable
    get() = when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> dimensions.padding.small
        else -> dimensions.padding.medium
    }