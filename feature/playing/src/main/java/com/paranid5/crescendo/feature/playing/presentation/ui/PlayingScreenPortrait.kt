package com.paranid5.crescendo.feature.playing.presentation.ui

import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import arrow.core.raise.nullable
import com.paranid5.crescendo.core.common.PlaybackStatus
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.domain.image.model.ImageSize
import com.paranid5.crescendo.feature.playing.presentation.ui.composition_local.LocalCoverAlpha
import com.paranid5.crescendo.feature.playing.presentation.ui.composition_local.LocalPalette
import com.paranid5.crescendo.feature.playing.view_model.PlayingState
import com.paranid5.crescendo.feature.playing.view_model.PlayingUiIntent
import com.paranid5.crescendo.utils.extensions.getBrightDominantOrPrimary

private val AudioWaveformHeight = 64.dp

@NonRestartableComposable
@Composable
internal fun PlayingScreenPortrait(
    screenPlaybackStatus: PlaybackStatus,
    state: PlayingState,
    coverBitmap: BitmapDrawable?,
    onUiIntent: (PlayingUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = nullable {
    val coverAlpha = LocalCoverAlpha.current
    val appPadding = dimensions.padding
    var coverSize by rememberSaveable { mutableStateOf(ImageSize(1, 1)) }

    ConstraintLayout(modifier) {
        val (
            cover,
            audioWave,
            slider,
            titleAndPropertiesButton,
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

        Box(
            Modifier.constrainAs(cover) {
                top.linkTo(parent.top, margin = appPadding.extraLarge)
                bottom.linkTo(audioWave.top, margin = appPadding.large)
                start.linkTo(parent.start, margin = appPadding.extraMedium)
                end.linkTo(parent.end, margin = appPadding.extraMedium)
                height = Dimension.fillToConstraints
                width = Dimension.fillToConstraints
            }
        ) {
            if (coverBitmap != null)
                Cover(
                    cover = coverBitmap,
                    modifier = Modifier
                        .alpha(coverAlpha)
                        .aspectRatio(1F)
                        .align(Alignment.Center)
                        .fillMaxSize()
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
            screenPlaybackStatus = screenPlaybackStatus,
            state = state,
            seekTo = { onUiIntent(PlayingUiIntent.Playback.SeekTo(position = it)) },
            onLiveSeekerClick = {
                onUiIntent(PlayingUiIntent.Playback.SeekToLiveStreamRealPosition)
            },
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
