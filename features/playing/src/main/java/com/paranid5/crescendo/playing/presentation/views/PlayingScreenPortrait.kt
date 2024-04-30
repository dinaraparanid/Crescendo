package com.paranid5.crescendo.playing.presentation.views

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.core.media.images.ImageSize
import com.paranid5.crescendo.playing.presentation.rememberIsWaveformEnabled
import com.paranid5.crescendo.ui.covers.coverModelWithPalette

@Composable
internal fun PlayingScreenPortrait(
    audioStatus: AudioStatus,
    durationMillis: Long,
    coverAlpha: Float,
    isLiveStreaming: Boolean,
    modifier: Modifier = Modifier,
) {
    var coverSize by remember { mutableStateOf(ImageSize(1, 1)) }
    val (coverModel, palette) = coverModelWithPalette(
        audioStatus,
        coverSize
    )
    val isWaveformEnabled by rememberIsWaveformEnabled(audioStatus)

    ConstraintLayout(modifier) {
        val (
            cover,
            audioWave,
            slider,
            titleAndPropertiesButton,
            playbackButtons,
            utilsButtons
        ) = createRefs()

        BackgroundImage(
            audioStatus = audioStatus,
            modifier = Modifier
                .fillMaxSize()
                .alpha(coverAlpha)
        )

        Cover(
            coverModel = coverModel,
            palette = palette,
            modifier = Modifier
                .alpha(coverAlpha)
                .aspectRatio(1F)
                .constrainAs(cover) {
                    top.linkTo(parent.top, margin = 32.dp)
                    bottom.linkTo(audioWave.top, margin = 32.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                    width = Dimension.fillToConstraints
                }
                .onGloballyPositioned { coordinates ->
                    val width = coordinates.size.width
                    val height = coordinates.size.height

                    if (width > 0 && height > 0)
                        coverSize = ImageSize(width, height)
                }

        )

        AudioWaveform(
            enabled = isWaveformEnabled,
            palette = palette,
            modifier = Modifier
                .height(64.dp)
                .constrainAs(audioWave) {
                    bottom.linkTo(slider.top, margin = 8.dp)
                    start.linkTo(parent.start, margin = 24.dp)
                    end.linkTo(parent.end, margin = 24.dp)
                    width = Dimension.fillToConstraints
                }
        )

        PlaybackSliderWithTimeContainer(
            durationMillis = durationMillis,
            palette = palette,
            audioStatus = audioStatus,
            isLiveStreaming = isLiveStreaming,
            modifier = Modifier.constrainAs(slider) {
                bottom.linkTo(titleAndPropertiesButton.top, margin = 16.dp)
                start.linkTo(parent.start, margin = 8.dp)
                end.linkTo(parent.end, margin = 8.dp)
                width = Dimension.fillToConstraints
            }
        )

        TitleAndPropertiesButton(
            audioStatus = audioStatus,
            palette = palette,
            modifier = Modifier.constrainAs(titleAndPropertiesButton) {
                bottom.linkTo(playbackButtons.top, margin = 16.dp)
                start.linkTo(parent.start, margin = 8.dp)
                end.linkTo(parent.end, margin = 8.dp)
                width = Dimension.fillToConstraints
            }
        )

        PlaybackButtons(
            audioStatus = audioStatus,
            palette = palette,
            modifier = Modifier.constrainAs(playbackButtons) {
                bottom.linkTo(utilsButtons.top, margin = 8.dp)
                start.linkTo(parent.start, margin = 24.dp)
                end.linkTo(parent.end, margin = 24.dp)
                width = Dimension.fillToConstraints
            }
        )

        UtilsButtons(
            audioStatus = audioStatus,
            palette = palette,
            modifier = Modifier.constrainAs(utilsButtons) {
                bottom.linkTo(parent.bottom, margin = 48.dp)
                start.linkTo(parent.start, margin = 24.dp)
                end.linkTo(parent.end, margin = 24.dp)
                width = Dimension.fillToConstraints
            }
        )
    }
}