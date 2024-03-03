package com.paranid5.crescendo.presentation.main.playing.views

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
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
import com.paranid5.crescendo.domain.media.AudioStatus
import com.paranid5.crescendo.media.images.ImageSize
import com.paranid5.crescendo.presentation.main.coverModelWithPalette
import com.paranid5.crescendo.presentation.main.playing.rememberIsWaveformEnabled
import com.paranid5.crescendo.presentation.main.playing.views.properties.PropertiesButton
import com.paranid5.crescendo.presentation.ui.extensions.getLightMutedOrPrimary

@Composable
fun PlayingScreenLandscape(
    durationMillis: Long,
    coverAlpha: Float,
    audioStatus: AudioStatus,
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
            propertiesButton,
            liveSeeker,
            slider,
            playbackButtons,
            utilsButtons
        ) = createRefs()

        BackgroundImage(
            audioStatus = audioStatus,
            modifier = Modifier
                .fillMaxSize()
                .alpha(coverAlpha)
        )

        AudioWaveform(
            enabled = isWaveformEnabled,
            palette = palette,
            modifier = Modifier.constrainAs(audioWave) {
                top.linkTo(parent.top, margin = 8.dp)
                bottom.linkTo(slider.top, margin = 2.dp)
                height = Dimension.fillToConstraints

                start.linkTo(parent.start, margin = 20.dp)
                end.linkTo(parent.end, margin = 48.dp)
                width = Dimension.fillToConstraints
            }
        )

        Cover(
            coverModel = coverModel,
            palette = palette,
            modifier = Modifier
                .alpha(coverAlpha)
                .aspectRatio(1F)
                .constrainAs(cover) {
                    centerHorizontallyTo(parent)
                    top.linkTo(parent.top, margin = 8.dp)
                    bottom.linkTo(slider.top, margin = 2.dp)
                    height = Dimension.fillToConstraints
                }
                .onGloballyPositioned { coordinates ->
                    coverSize = ImageSize(coordinates.size.width, coordinates.size.height)
                },
        )

        PropertiesButton(
            palette = palette,
            audioStatus = audioStatus,
            modifier = Modifier.constrainAs(propertiesButton) {
                top.linkTo(parent.top, margin = 12.dp)
                end.linkTo(parent.end, margin = 32.dp)
            },
        )

        if (isLiveStreaming)
            LiveSeeker(
                color = palette.getLightMutedOrPrimary(),
                modifier = Modifier.constrainAs(liveSeeker) {
                    top.linkTo(parent.top, margin = 8.dp)
                    start.linkTo(parent.start, margin = 5.dp)
                }
            )

        PlaybackSliderWithLabels(
            audioStatus = audioStatus,
            durationMillis = durationMillis,
            palette = palette,
            isLiveStreaming = isLiveStreaming,
            modifier = Modifier.constrainAs(slider) {
                top.linkTo(parent.top, margin = 20.dp)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start, margin = 20.dp)
                end.linkTo(parent.end, margin = 48.dp)
                width = Dimension.fillToConstraints
            }
        )

        PlaybackButtons(
            audioStatus = audioStatus,
            palette = palette,
            modifier = Modifier.constrainAs(playbackButtons) {
                bottom.linkTo(utilsButtons.top, margin = 2.dp)
                start.linkTo(parent.start, margin = 20.dp)
                end.linkTo(parent.end, margin = 20.dp)
                width = Dimension.fillToConstraints
            }
        )

        UtilsButtons(
            palette = palette,
            audioStatus = audioStatus,
            modifier = Modifier.constrainAs(utilsButtons) {
                bottom.linkTo(parent.bottom, margin = 16.dp)
                start.linkTo(parent.start, margin = 20.dp)
                end.linkTo(parent.end, margin = 20.dp)
                width = Dimension.fillToConstraints
            }
        )
    }
}