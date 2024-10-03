package com.paranid5.crescendo.feature.playing.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.palette.graphics.Palette
import arrow.core.raise.nullable
import coil.request.ImageRequest
import com.paranid5.crescendo.core.common.PlaybackStatus
import com.paranid5.crescendo.core.media.images.ImageSize
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.feature.playing.presentation.ui.composition_local.LocalCoverAlpha
import com.paranid5.crescendo.feature.playing.presentation.ui.composition_local.LocalPalette
import com.paranid5.crescendo.feature.playing.view_model.PlayingState
import com.paranid5.crescendo.feature.playing.view_model.PlayingUiIntent
import com.paranid5.crescendo.ui.covers.mediaCoverModelWithPalette
import com.paranid5.crescendo.utils.extensions.getBrightDominantOrPrimary
import com.paranid5.crescendo.utils.extensions.orNil

private val AudioWaveformHeight = 64.dp

@Composable
internal fun PlayingScreenPortrait(
    screenPlaybackStatus: PlaybackStatus,
    state: PlayingState,
    onUiIntent: (PlayingUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = nullable {
    val context = LocalContext.current
    val coverAlpha = LocalCoverAlpha.current
    val appPadding = dimensions.padding
    var coverSize by rememberSaveable { mutableStateOf(ImageSize(1, 1)) }

    var coverModel by remember { mutableStateOf<ImageRequest?>(null) }
    var palette by remember { mutableStateOf<Palette?>(null) }

    val videoCovers = remember(state.currentMetadata) {
        state.currentMetadata?.coversPaths.orNil()
    }

    val trackPath = remember(state.currentTrack) {
        state.currentTrack?.path
    }

    LaunchedEffect(context, trackPath, screenPlaybackStatus, videoCovers, coverSize) {
        val (model, plt) = mediaCoverModelWithPalette(
            context = context,
            playbackStatus = screenPlaybackStatus,
            videoCovers = videoCovers,
            trackPath = trackPath,
            size = coverSize,
        )

        coverModel = model
        palette = plt
    }

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
                playbackStatus = screenPlaybackStatus,
                videoCovers = videoCovers,
                trackPath = trackPath,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(coverAlpha),
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
                coverModel?.let { model ->
                    Cover(
                        coverModel = model,
                        color = palette.getBrightDominantOrPrimary(),
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
                screenPlaybackStatus = screenPlaybackStatus,
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
                screenPlaybackStatus = screenPlaybackStatus,
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
