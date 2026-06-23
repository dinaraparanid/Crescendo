package com.paranid5.crescendo.feature.playing.presentation.ui

import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import arrow.core.raise.nullable
import com.paranid5.crescendo.core.common.PlaybackStatus
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.domain.image.model.ImageSize
import com.paranid5.crescendo.feature.playing.presentation.ui.composition_local.LocalCoverAlpha
import com.paranid5.crescendo.feature.playing.presentation.ui.composition_local.LocalPalette
import com.paranid5.crescendo.feature.playing.presentation.ui.kebab.KebabMenuButton
import com.paranid5.crescendo.feature.playing.view_model.PlayingState
import com.paranid5.crescendo.feature.playing.view_model.PlayingUiIntent
import com.paranid5.crescendo.utils.extensions.getBrightDominantOrPrimary

@NonRestartableComposable
@Composable
internal fun PlayingScreenLandscape(
    screenPlaybackStatus: PlaybackStatus,
    state: PlayingState,
    coverBitmap: BitmapDrawable?,
    onUiIntent: (PlayingUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = nullable {
    val coverAlpha = LocalCoverAlpha.current
    val appPadding = dimensions.padding
    var coverSize by remember { mutableStateOf(ImageSize(width = 1, height = 1)) }

    val isLiveStreaming by remember(screenPlaybackStatus, state.isLiveStreaming) {
        derivedStateOf { screenPlaybackStatus == PlaybackStatus.STREAMING && state.isLiveStreaming }
    }

    ConstraintLayout(modifier) {
        val (
            cover,
            audioWave,
            propertiesButton,
            liveSeeker,
            slider,
            playbackButtons,
            utilsButtons,
        ) = createRefs()

        val palette = LocalPalette.current

        BackgroundImage(
            playbackStatus = screenPlaybackStatus,
            videoCovers = state.videoCovers,
            trackPath = state.currentTrackCoverPath,
            modifier = Modifier
                .fillMaxSize()
                .alpha(coverAlpha),
        )

        AudioWaveform(
            state = state,
            color = palette.getBrightDominantOrPrimary(),
            modifier = Modifier.constrainAs(audioWave) {
                top.linkTo(parent.top, margin = appPadding.small)
                bottom.linkTo(slider.top, margin = appPadding.minimum)
                height = Dimension.fillToConstraints

                start.linkTo(parent.start, margin = appPadding.big)
                end.linkTo(parent.end, margin = appPadding.extraLarge)
                width = Dimension.fillToConstraints
            },
        )

        Box(
            Modifier.constrainAs(cover) {
                centerHorizontallyTo(parent)
                top.linkTo(parent.top, margin = appPadding.small)
                bottom.linkTo(slider.top, margin = appPadding.minimum)
                height = Dimension.fillToConstraints
            }
        ) {
            if (coverBitmap != null)
                Cover(
                    cover = coverBitmap,
                    modifier = Modifier
                        .alpha(coverAlpha)
                        .aspectRatio(1F)
                        .fillMaxSize()
                        .align(Alignment.Center)
                        .onGloballyPositioned { coordinates ->
                            val width = coordinates.size.width
                            val height = coordinates.size.height

                            if (width > 0 && height > 0)
                                coverSize = ImageSize(width, height)
                        },
                )
        }

        KebabMenuButton(
            screenPlaybackStatus = screenPlaybackStatus,
            state = state,
            onUiIntent = onUiIntent,
            tint = palette.getBrightDominantOrPrimary(),
            modifier = Modifier.constrainAs(propertiesButton) {
                top.linkTo(parent.top, margin = appPadding.medium)
                end.linkTo(parent.end, margin = appPadding.large)
            },
        )

        if (isLiveStreaming)
            LiveSeeker(
                color = palette.getBrightDominantOrPrimary(),
                modifier = Modifier.constrainAs(liveSeeker) {
                    top.linkTo(parent.top, margin = appPadding.small)
                    start.linkTo(parent.start, margin = appPadding.extraSmall)
                },
            ) {
                onUiIntent(PlayingUiIntent.Playback.SeekToLiveStreamRealPosition)
            }

        PlaybackSliderWithLabels(
            isLiveStreaming = isLiveStreaming,
            screenPlaybackStatus = screenPlaybackStatus,
            state = state,
            modifier = Modifier.constrainAs(slider) {
                top.linkTo(parent.top, margin = appPadding.big)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start, margin = appPadding.big)
                end.linkTo(parent.end, margin = appPadding.extraLarge)
                width = Dimension.fillToConstraints
            },
        ) {
            onUiIntent(PlayingUiIntent.Playback.SeekTo(position = it))
        }

        PlaybackButtons(
            state = state,
            onUiIntent = onUiIntent,
            modifier = Modifier.constrainAs(playbackButtons) {
                bottom.linkTo(utilsButtons.top, margin = appPadding.minimum)
                start.linkTo(parent.start, margin = appPadding.big)
                end.linkTo(parent.end, margin = appPadding.big)
                width = Dimension.fillToConstraints
            },
        )

        UtilsButtons(
            screenPlaybackStatus = screenPlaybackStatus,
            state = state,
            onUiIntent = onUiIntent,
            modifier = Modifier.constrainAs(utilsButtons) {
                bottom.linkTo(parent.bottom, margin = appPadding.extraMedium)
                start.linkTo(parent.start, margin = appPadding.big)
                end.linkTo(parent.end, margin = appPadding.big)
                width = Dimension.fillToConstraints
            },
        )
    }
}
