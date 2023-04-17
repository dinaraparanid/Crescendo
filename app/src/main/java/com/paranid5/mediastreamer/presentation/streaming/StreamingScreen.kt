package com.paranid5.mediastreamer.presentation.streaming

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.palette.graphics.Palette
import coil.compose.AsyncImagePainter
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.data.VideoMetadata
import com.paranid5.mediastreamer.data.utils.extensions.timeString
import com.paranid5.mediastreamer.domain.StorageHandler
import com.paranid5.mediastreamer.presentation.Screens
import com.paranid5.mediastreamer.presentation.ui.BroadcastReceiver
import com.paranid5.mediastreamer.presentation.ui.extensions.getLightMutedOrPrimary
import com.paranid5.mediastreamer.presentation.ui.rememberVideoCoverPainterWithPalette
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.koin.compose.koinInject

private const val BROADCAST_LOCATION = "com.paranid5.mediastreamer.presentation.streaming"
const val Broadcast_CUR_POSITION_CHANGED = "$BROADCAST_LOCATION.CUR_POSITION_CHANGED"
const val Broadcast_IS_REPEATING_CHANGED = "$BROADCAST_LOCATION.IS_REPEATING_CHANGED"

const val CUR_POSITION_ARG = "cur_position"
const val IS_REPEATING_ARG = "is_repeating"
const val VIDEO_CASH_STATUS_ARG = "video_cash_status"

@Composable
fun StreamingScreen(
    viewModel: StreamingViewModel,
    curScreenState: MutableStateFlow<Screens>,
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = koinInject()
) {
    curScreenState.update { Screens.StreamScreen.Streaming }

    when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE ->
            StreamingScreenLandscape(viewModel, modifier, storageHandler)

        else -> StreamingScreenPortrait(viewModel, modifier, storageHandler)
    }
}

@Composable
private fun StreamingScreenPortrait(
    viewModel: StreamingViewModel,
    modifier: Modifier,
    storageHandler: StorageHandler
) {
    val metadata by storageHandler.currentMetadataState.collectAsState()

    val (coilPainter, palette) = rememberVideoCoverPainterWithPalette(
        isPlaceholderRequired = true,
        size = 1100 to 1000
    )

    ConstraintLayout(modifier.fillMaxSize()) {
        val (
            cover,
            slider,
            titleAndPropertiesButton,
            playbackButtons,
            utilsButtons
        ) = createRefs()

        VideoCover(
            modifier = Modifier
                .padding(20.dp)
                .constrainAs(cover) {
                    top.linkTo(parent.top, margin = 20.dp)
                    start.linkTo(parent.start, margin = 20.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                    bottom.linkTo(slider.top, margin = 10.dp)
                    height = Dimension.fillToConstraints
                    width = Dimension.fillToConstraints
                },
            coilPainter = coilPainter,
            palette = palette
        )

        PlaybackSlider(
            metadata = metadata,
            palette = palette,
            modifier = Modifier.constrainAs(slider) {
                centerVerticallyTo(parent)
                start.linkTo(parent.start, margin = 20.dp)
                end.linkTo(parent.end, margin = 20.dp)
            }
        ) { curPosition, videoLength, color ->
            TimeText(curPosition, color)
            Spacer(Modifier.weight(1F))
            TimeText(videoLength, color)
        }

        TitleAndPropertiesButton(
            metadata = metadata,
            palette = palette,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .constrainAs(titleAndPropertiesButton) {
                    top.linkTo(slider.bottom, margin = 15.dp)
                    start.linkTo(parent.start, margin = 20.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                }
        )

        PlaybackButtons(
            streamingPresenter = viewModel.presenter,
            palette = palette,
            modifier = Modifier.constrainAs(playbackButtons) {
                top.linkTo(titleAndPropertiesButton.bottom, margin = 15.dp)
                start.linkTo(parent.start, margin = 20.dp)
                end.linkTo(parent.end, margin = 20.dp)
            }
        )

        UtilsButtons(
            palette = palette,
            modifier = Modifier.constrainAs(utilsButtons) {
                top.linkTo(playbackButtons.bottom, margin = 5.dp)
                start.linkTo(parent.start, margin = 20.dp)
                end.linkTo(parent.end, margin = 20.dp)
            }
        )
    }
}

@Composable
private fun StreamingScreenLandscape(
    viewModel: StreamingViewModel,
    modifier: Modifier,
    storageHandler: StorageHandler
) {
    val metadata by storageHandler.currentMetadataState.collectAsState()

    val (coilPainter, palette) = rememberVideoCoverPainterWithPalette(
        isPlaceholderRequired = true,
        size = 1100 to 1000
    )

    ConstraintLayout(modifier.fillMaxSize()) {
        val (
            cover,
            propertiesButton,
            slider,
            playbackButtons,
            utilsButtons
        ) = createRefs()

        VideoCover(
            modifier = Modifier.constrainAs(cover) {
                centerHorizontallyTo(parent)
                top.linkTo(parent.top, margin = 8.dp)
                bottom.linkTo(slider.top, margin = 2.dp)
                height = Dimension.fillToConstraints
            },
            coilPainter = coilPainter,
            palette = palette
        )

        PropertiesButton(
            modifier = Modifier.constrainAs(propertiesButton) {
                top.linkTo(parent.top, margin = 8.dp)
                end.linkTo(parent.end, margin = 5.dp)
            },
            palette = palette
        )

        PlaybackSlider(
            metadata = metadata,
            palette = palette,
            modifier = Modifier.constrainAs(slider) {
                top.linkTo(parent.top, margin = 0.dp)
                bottom.linkTo(parent.bottom, margin = 25.dp)
                start.linkTo(parent.start, margin = 20.dp)
                end.linkTo(parent.end, margin = 20.dp)
            }
        ) { curPosition, videoLength, color ->
            TimeText(curPosition, color)

            TitleAndAuthor(
                metadata,
                palette,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .weight(1F, fill = true),
                textAlignment = Alignment.CenterHorizontally
            )

            TimeText(videoLength, color)
        }

        PlaybackButtons(
            streamingPresenter = viewModel.presenter,
            palette = palette,
            modifier = Modifier.constrainAs(playbackButtons) {
                top.linkTo(slider.bottom, margin = 5.dp)
                start.linkTo(parent.start, margin = 20.dp)
                end.linkTo(parent.end, margin = 20.dp)
            }
        )

        UtilsButtons(
            palette = palette,
            modifier = Modifier.constrainAs(utilsButtons) {
                top.linkTo(playbackButtons.bottom, margin = 2.dp)
                start.linkTo(parent.start, margin = 20.dp)
                end.linkTo(parent.end, margin = 20.dp)
            }
        )
    }
}

@Composable
private fun VideoCover(
    coilPainter: AsyncImagePainter,
    palette: Palette?,
    modifier: Modifier = Modifier
) {
    val lightVibrantColor = palette.getLightMutedOrPrimary()

    Image(
        painter = coilPainter,
        modifier = modifier
            .aspectRatio(1F)
            .fillMaxSize()
            .shadow(
                elevation = 80.dp,
                shape = RoundedCornerShape(5.dp),
                ambientColor = lightVibrantColor,
                spotColor = lightVibrantColor
            )
            .border(
                width = 50.dp,
                color = Color.Transparent,
                shape = RoundedCornerShape(50.dp)
            ),
        contentDescription = stringResource(R.string.video_cover),
        contentScale = ContentScale.Crop,
        alignment = Alignment.Center,
    )
}

@Composable
private fun PlaybackSlider(
    metadata: VideoMetadata?,
    palette: Palette?,
    modifier: Modifier = Modifier,
    streamingUIHandler: StreamingUIHandler = koinInject(),
    storageHandler: StorageHandler = koinInject(),
    content: @Composable RowScope.(curPosition: Long, videoLength: Long, color: Color) -> Unit
) {
    val lightVibrantColor = palette.getLightMutedOrPrimary()
    val videoLength = metadata?.lenInMillis ?: 0
    var curPosition by remember { mutableStateOf(0L) }

    LaunchedEffect(key1 = true) {
        curPosition = storageHandler.playbackPositionState.value
    }

    BroadcastReceiver(action = Broadcast_CUR_POSITION_CHANGED) { _, intent ->
        curPosition = intent!!.getLongExtra(CUR_POSITION_ARG, 0)
    }

    Column(modifier.padding(horizontal = 10.dp)) {
        Slider(
            value = curPosition.toFloat(),
            valueRange = 0F..videoLength.toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = lightVibrantColor,
                activeTrackColor = lightVibrantColor
            ),
            onValueChange = { curPosition = it.toLong() },
            onValueChangeFinished = {
                streamingUIHandler.sendSeekToBroadcast(curPosition)
            }
        )

        Row(Modifier.fillMaxWidth()) {
            content(curPosition, videoLength, lightVibrantColor)
        }
    }
}

@Composable
private fun TimeText(time: Long, color: Color, modifier: Modifier = Modifier) =
    Text(text = time.timeString, color = color, modifier = modifier)