package com.paranid5.crescendo.feature.playing.presentation.ui

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import arrow.core.raise.nullable
import com.paranid5.crescendo.core.media.images.ImageSize
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.feature.playing.presentation.ui.composition_local.LocalPalette
import com.paranid5.crescendo.feature.playing.presentation.ui.kebab.KebabMenuButton
import com.paranid5.crescendo.feature.playing.view_model.PlayingState
import com.paranid5.crescendo.feature.playing.view_model.PlayingUiIntent
import com.paranid5.crescendo.ui.covers.mediaCoverModelWithPalette
import com.paranid5.crescendo.utils.extensions.getBrightDominantOrPrimary

@Composable
internal fun PlayingScreenLandscape(
    state: PlayingState,
    onUiIntent: (PlayingUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = nullable {
    val appPadding = dimensions.padding
    val audioStatus = state.screenPlaybackStatus.bind()
    var coverSize by remember { mutableStateOf(ImageSize(1, 1)) }

    val (coverModel, palette) = mediaCoverModelWithPalette(
        playbackStatus = audioStatus,
        size = coverSize,
    )

    CompositionLocalProvider(LocalPalette provides palette) {
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

            BackgroundImage(
                playbackStatus = audioStatus,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(state.coverAlpha),
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

            Cover(
                coverModel = coverModel,
                color = palette.getBrightDominantOrPrimary(),
                modifier = Modifier
                    .alpha(state.coverAlpha)
                    .aspectRatio(1F)
                    .constrainAs(cover) {
                        centerHorizontallyTo(parent)
                        top.linkTo(parent.top, margin = appPadding.small)
                        bottom.linkTo(slider.top, margin = appPadding.minimum)
                        height = Dimension.fillToConstraints
                    }
                    .onGloballyPositioned { coordinates ->
                        val width = coordinates.size.width
                        val height = coordinates.size.height

                        if (width > 0 && height > 0)
                            coverSize = ImageSize(width, height)
                    },
            )

            KebabMenuButton(
                state = state,
                onUiIntent = onUiIntent,
                tint = palette.getBrightDominantOrPrimary(),
                modifier = Modifier.constrainAs(propertiesButton) {
                    top.linkTo(parent.top, margin = appPadding.medium)
                    end.linkTo(parent.end, margin = appPadding.large)
                },
            )

            if (state.isLiveStreaming)
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
}
