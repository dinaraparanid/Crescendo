package com.paranid5.crescendo.presentation.main.playing.views

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
import com.paranid5.crescendo.media.images.ImageSize
import com.paranid5.crescendo.presentation.main.coverModelWithPalette
import com.paranid5.crescendo.presentation.main.playing.rememberIsWaveformEnabled

@Composable
fun PlayingScreenPortrait(
    audioStatus: AudioStatus,
    durationMillis: Long,
    coverAlpha: Float,
    isLiveStreaming: Boolean,
    modifier: Modifier = Modifier,
) {
    var coverSize by remember { mutableStateOf(ImageSize(1, 1)) }
    val (coverModel, palette) = coverModelWithPalette(audioStatus, coverSize)
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
                .constrainAs(cover) {
                    top.linkTo(parent.top, margin = 40.dp)
                    bottom.linkTo(audioWave.top, margin = 10.dp)
                    start.linkTo(parent.start, margin = 15.dp)
                    end.linkTo(parent.end, margin = 15.dp)
                    height = Dimension.fillToConstraints
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
                .height(40.dp)
                .constrainAs(audioWave) {
                    bottom.linkTo(slider.top, margin = 10.dp)
                    start.linkTo(parent.start, margin = 20.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                    width = Dimension.fillToConstraints
                }
        )

        PlaybackSliderWithTimeContainer(
            durationMillis = durationMillis,
            palette = palette,
            audioStatus = audioStatus,
            isLiveStreaming = isLiveStreaming,
            modifier = Modifier.constrainAs(slider) {
                bottom.linkTo(titleAndPropertiesButton.top, margin = 15.dp)
                start.linkTo(parent.start, margin = 10.dp)
                end.linkTo(parent.end, margin = 10.dp)
                width = Dimension.fillToConstraints
            }
        )

        TitleAndPropertiesButton(
            audioStatus = audioStatus,
            palette = palette,
            modifier = Modifier.constrainAs(titleAndPropertiesButton) {
                bottom.linkTo(playbackButtons.top, margin = 15.dp)
                start.linkTo(parent.start, margin = 10.dp)
                end.linkTo(parent.end, margin = 10.dp)
                width = Dimension.fillToConstraints
            }
        )

        PlaybackButtons(
            audioStatus = audioStatus,
            palette = palette,
            modifier = Modifier.constrainAs(playbackButtons) {
                bottom.linkTo(utilsButtons.top, margin = 5.dp)
                start.linkTo(parent.start, margin = 20.dp)
                end.linkTo(parent.end, margin = 20.dp)
                width = Dimension.fillToConstraints
            }
        )

        UtilsButtons(
            audioStatus = audioStatus,
            palette = palette,
            modifier = Modifier.constrainAs(utilsButtons) {
                bottom.linkTo(parent.bottom, margin = 40.dp)
                start.linkTo(parent.start, margin = 20.dp)
                end.linkTo(parent.end, margin = 20.dp)
                width = Dimension.fillToConstraints
            }
        )
    }
}