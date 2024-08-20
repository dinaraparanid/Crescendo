package com.paranid5.crescendo.presentation.main

import android.content.res.Configuration
import android.widget.Toast
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import arrow.core.raise.nullable
import com.paranid5.crescendo.core.common.PlaybackStatus
import com.paranid5.crescendo.core.common.navigation.LocalNavigator
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.feature.current_playlist.presentation.CurrentPlaylistScreen
import com.paranid5.crescendo.feature.current_playlist.view_model.CurrentPlaylistScreenEffect
import com.paranid5.crescendo.feature.playing.presentation.PlayingScreen
import com.paranid5.crescendo.feature.playing.view_model.PlayingScreenEffect
import com.paranid5.crescendo.navigation.AppScreen
import com.paranid5.crescendo.navigation.requireAppNavigator
import com.paranid5.crescendo.presentation.main.appbar.AppBar
import com.paranid5.crescendo.ui.appbar.appBarHeight
import com.paranid5.crescendo.ui.composition_locals.LocalCurrentPlaylistSheetState
import com.paranid5.crescendo.ui.composition_locals.playing.LocalPlayingPagerState
import com.paranid5.crescendo.ui.composition_locals.playing.LocalPlayingSheetState
import com.paranid5.crescendo.ui.utils.PushUpButton
import com.paranid5.crescendo.utils.doNothing
import kotlinx.coroutines.launch

private const val ContentCollapsedPadding = 8F
private const val ContentExpandedPadding = 24F
private const val PushUpCollapsedPadding = 12F
private const val PushUpExpandedPadding = 32F

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
internal fun PlayingBottomSheet(
    alpha: Float,
    modifier: Modifier = Modifier,
) = nullable {
    val context = LocalContext.current
    val navigator = LocalNavigator.requireAppNavigator()
    val curPlaylistSheetState = LocalCurrentPlaylistSheetState.current.bind()
    val playingPagerState = LocalPlayingPagerState.current.bind()

    val playingSheetState = LocalPlayingSheetState.current.bind()
    val sheetState = playingSheetState.bottomSheetState
    val targetValue = sheetState.targetValue
    val currentValue = sheetState.currentValue
    val isBarNotVisible = currentValue == BottomSheetValue.Expanded
            && targetValue == BottomSheetValue.Expanded

    val coroutineScope = rememberCoroutineScope()

    fun onScreenEffect(result: PlayingScreenEffect) = when (result) {
        is PlayingScreenEffect.ShowAudioEffects -> {
            coroutineScope.launch { playingSheetState.bottomSheetState.collapse() }
            navigator.pushIfNotSame(AppScreen.Audio.AudioEffects)
        }

        is PlayingScreenEffect.ShowTrimmer ->
            navigator.pushIfNotSame(AppScreen.Audio.Trimmer(result.trackUri))

        is PlayingScreenEffect.ShowAudioEffectsNotAllowed ->
            Toast
                .makeText(context, R.string.audio_effects_init_error, Toast.LENGTH_LONG)
                .show()

        is PlayingScreenEffect.ShowMetaEditor -> doNothing() // TODO: show meta editor
    }

    ModalBottomSheetLayout(
        modifier = modifier,
        sheetState = curPlaylistSheetState,
        sheetContent = {
            CurrentPlaylistBottomSheet(
                alpha = alpha,
                state = curPlaylistSheetState,
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
            HorizontalPager(state = playingPagerState) { page ->
                when (page) {
                    0 -> PlayingScreen(
                        coverAlpha = 1 - alpha,
                        screenPlaybackStatus = PlaybackStatus.PLAYING,
                        modifier = modifier.fillMaxSize(),
                        onScreenEffect = ::onScreenEffect,
                    )

                    else -> PlayingScreen(
                        coverAlpha = 1 - alpha,
                        screenPlaybackStatus = PlaybackStatus.STREAMING,
                        modifier = modifier.fillMaxSize(),
                        onScreenEffect = ::onScreenEffect,
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CurrentPlaylistBottomSheet(
    alpha: Float,
    state: ModalBottomSheetState,
    modifier: Modifier = Modifier,
) {
    val navigator = LocalNavigator.requireAppNavigator()
    val pushUpTopPadding by animatePushUpTopPaddingAsState(state)
    val contentTopPadding by animateContentTopPaddingAsState(state)

    Box(modifier) {
        PushUpButton(
            alpha = alpha,
            modifier = Modifier
                .padding(top = pushUpTopPadding)
                .align(Alignment.TopCenter)
        )

        CurrentPlaylistScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = contentTopPadding),
        ) { result ->
            when (result) {
                is CurrentPlaylistScreenEffect.ShowTrimmer ->
                    navigator.pushIfNotSame(AppScreen.Audio.Trimmer(result.trackUri))

                is CurrentPlaylistScreenEffect.ShowMetaEditor ->
                    doNothing // TODO: show meta editor
            }
        }
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
                targetValue == ModalBottomSheetValue.Hidden -> ContentCollapsedPadding

                currentValue == ModalBottomSheetValue.Hidden &&
                        targetValue == ModalBottomSheetValue.HalfExpanded ->
                    ContentCollapsedPadding

                currentValue == ModalBottomSheetValue.HalfExpanded &&
                        targetValue == ModalBottomSheetValue.HalfExpanded ->
                    ContentCollapsedPadding

                currentValue == ModalBottomSheetValue.Expanded &&
                        targetValue == ModalBottomSheetValue.Expanded ->
                    ContentExpandedPadding + ContentCollapsedPadding

                currentValue == ModalBottomSheetValue.Expanded &&
                        targetValue == ModalBottomSheetValue.HalfExpanded ->
                    (1 - progress) * ContentExpandedPadding + ContentCollapsedPadding

                else -> progress * ContentExpandedPadding + ContentCollapsedPadding
            }.dp
        }
    }, label = ""
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun animatePushUpTopPaddingAsState(sheetState: ModalBottomSheetState) = animateDpAsState(
    when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> dimensions.padding.medium

        else -> {
            val progress = sheetState.progress
            val targetValue = sheetState.targetValue
            val currentValue = sheetState.currentValue

            when {
                targetValue == ModalBottomSheetValue.Hidden -> PushUpCollapsedPadding

                currentValue == ModalBottomSheetValue.Hidden &&
                        targetValue == ModalBottomSheetValue.HalfExpanded ->
                    PushUpCollapsedPadding

                currentValue == ModalBottomSheetValue.HalfExpanded &&
                        targetValue == ModalBottomSheetValue.HalfExpanded ->
                    PushUpCollapsedPadding

                currentValue == ModalBottomSheetValue.Expanded &&
                        targetValue == ModalBottomSheetValue.Expanded ->
                    PushUpExpandedPadding + PushUpCollapsedPadding

                currentValue == ModalBottomSheetValue.Expanded &&
                        targetValue == ModalBottomSheetValue.HalfExpanded ->
                    (1 - progress) * PushUpExpandedPadding + PushUpCollapsedPadding

                else -> progress * PushUpExpandedPadding + PushUpCollapsedPadding
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
