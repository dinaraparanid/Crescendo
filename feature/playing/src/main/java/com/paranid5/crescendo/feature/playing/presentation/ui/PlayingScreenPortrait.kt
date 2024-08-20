package com.paranid5.crescendo.feature.playing.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import arrow.core.raise.nullable
import com.paranid5.crescendo.core.media.images.ImageSize
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme
import com.paranid5.crescendo.feature.playing.presentation.ui.composition_local.LocalPalette
import com.paranid5.crescendo.feature.playing.view_model.PlayingState
import com.paranid5.crescendo.feature.playing.view_model.PlayingUiIntent
import com.paranid5.crescendo.ui.covers.mediaCoverModelWithPalette
import com.paranid5.crescendo.utils.extensions.getBrightDominantOrPrimary

private val AudioWaveformHeight = 64.dp

@Composable
internal fun PlayingScreenPortrait(
    state: PlayingState,
    onUiIntent: (PlayingUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = nullable {
    val appPadding = AppTheme.dimensions.padding
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
                slider,
                titleAndPropertiesButton,
                playbackButtons,
                utilsButtons,
            ) = createRefs()

            BackgroundImage(
                playbackStatus = audioStatus,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(state.coverAlpha),
            )

            Box(
                Modifier.constrainAs(cover) {
                    top.linkTo(parent.top, margin = appPadding.large)
                    bottom.linkTo(audioWave.top, margin = appPadding.large)
                    start.linkTo(parent.start, margin = appPadding.extraMedium)
                    end.linkTo(parent.end, margin = appPadding.extraMedium)
                    height = Dimension.fillToConstraints
                    width = Dimension.fillToConstraints
                }
            ) {
                Cover(
                    coverModel = coverModel,
                    color = palette.getBrightDominantOrPrimary(),
                    modifier = Modifier
                        .alpha(state.coverAlpha)
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

            AudioWaveform(
                state = state,
                color = palette.getBrightDominantOrPrimary(),
                modifier = Modifier
                    .height(AudioWaveformHeight)
                    .constrainAs(audioWave) {
                        bottom.linkTo(slider.top, margin = appPadding.small)
                        start.linkTo(parent.start, margin = appPadding.extraBig)
                        end.linkTo(parent.end, margin = appPadding.extraBig)
                        width = Dimension.fillToConstraints
                    },
            )

            PlaybackSliderWithTimeContainer(
                state = state,
                seekTo = { onUiIntent(PlayingUiIntent.Playback.SeekTo(position = it)) },
                onLiveSeekerClick = { onUiIntent(PlayingUiIntent.Playback.SeekToLiveStreamRealPosition) },
                modifier = Modifier.constrainAs(slider) {
                    bottom.linkTo(titleAndPropertiesButton.top, margin = appPadding.extraMedium)
                    start.linkTo(parent.start, margin = appPadding.small)
                    end.linkTo(parent.end, margin = appPadding.small)
                    width = Dimension.fillToConstraints
                },
            )

            TitleAndPropertiesButton(
                state = state,
                onUiIntent = onUiIntent,
                modifier = Modifier.constrainAs(titleAndPropertiesButton) {
                    bottom.linkTo(playbackButtons.top, margin = appPadding.extraMedium)
                    start.linkTo(parent.start, margin = appPadding.small)
                    end.linkTo(parent.end, margin = appPadding.small)
                    width = Dimension.fillToConstraints
                },
            )

            PlaybackButtons(
                state = state,
                onUiIntent = onUiIntent,
                modifier = Modifier.constrainAs(playbackButtons) {
                    bottom.linkTo(utilsButtons.top, margin = appPadding.small)
                    start.linkTo(parent.start, margin = appPadding.extraBig)
                    end.linkTo(parent.end, margin = appPadding.extraBig)
                    width = Dimension.fillToConstraints
                },
            )

            UtilsButtons(
                state = state,
                onUiIntent = onUiIntent,
                modifier = Modifier.constrainAs(utilsButtons) {
                    bottom.linkTo(parent.bottom, margin = appPadding.extraLarge)
                    start.linkTo(parent.start, margin = appPadding.extraBig)
                    end.linkTo(parent.end, margin = appPadding.extraBig)
                    width = Dimension.fillToConstraints
                },
            )
        }
    }
}
