package com.paranid5.mediastreamer.presentation.streaming

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.palette.graphics.Palette
import coil.compose.AsyncImagePainter
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.data.VideoMetadata
import com.paranid5.mediastreamer.domain.StorageHandler
import com.paranid5.mediastreamer.presentation.composition_locals.LocalStreamState
import com.paranid5.mediastreamer.presentation.composition_locals.StreamStates
import com.paranid5.mediastreamer.presentation.ui.OnBackPressedHandler
import com.paranid5.mediastreamer.presentation.ui.extensions.getLightVibrantOrPrimary
import com.paranid5.mediastreamer.presentation.ui.rememberVideoCoverPainterWithPalette
import com.paranid5.mediastreamer.utils.BroadcastReceiver
import com.paranid5.mediastreamer.data.utils.extensions.timeString
import org.koin.androidx.compose.get

private const val BROADCAST_LOCATION = "com.paranid5.mediastreamer.presentation.ui.screens"
const val Broadcast_IS_PLAYING_CHANGED = "$BROADCAST_LOCATION.IS_PLAYING_CHANGED"
const val Broadcast_CUR_POSITION_CHANGED = "$BROADCAST_LOCATION.CUR_POSITION_CHANGED"
const val Broadcast_IS_REPEATING_CHANGED = "$BROADCAST_LOCATION.IS_REPEATING_CHANGED"
const val Broadcast_VIDEO_CASH_COMPLETED = "$BROADCAST_LOCATION.VIDEO_CASH_COMPLETED"

const val IS_PLAYING_ARG = "is_playing"
const val CUR_POSITION_ARG = "cur_position"
const val IS_REPEATING_ARG = "is_repeating"
const val VIDEO_CASH_STATUS = "video_cash_status"

@Composable
fun StreamingScreen(
    viewModel: StreamingViewModel,
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = get()
) {
    LocalStreamState.current.value = StreamStates.STREAMING
    val metadata by storageHandler.currentMetadataState.collectAsState()

    val (coilPainter, palette) = rememberVideoCoverPainterWithPalette(
        isPlaceholderRequired = true,
        size = 1100 to 1000
    )

    OnBackPressedHandler()

    ConstraintLayout(modifier.fillMaxSize()) {
        val (
            cover,
            slider,
            titleAndPropertiesButton,
            playbackButtons,
            utilsButtons
        ) = createRefs()

        VideoCover(
            modifier = Modifier.constrainAs(cover) {
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
        )

        TitleAndPropertiesButton(
            metadata = metadata,
            palette = palette,
            modifier = Modifier.constrainAs(titleAndPropertiesButton) {
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
private fun VideoCover(
    coilPainter: AsyncImagePainter,
    palette: Palette?,
    modifier: Modifier = Modifier
) {
    val lightVibrantColor = palette.getLightVibrantOrPrimary()

    Image(
        painter = coilPainter,
        modifier = modifier
            .aspectRatio(1F)
            .fillMaxSize()
            .padding(20.dp)
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
    streamingUIHandler: StreamingUIHandler = get(),
    storageHandler: StorageHandler = get()
) {
    val lightVibrantColor = palette.getLightVibrantOrPrimary()
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
            Text(text = curPosition.timeString, color = lightVibrantColor)
            Spacer(Modifier.weight(1F))
            Text(text = videoLength.timeString, color = lightVibrantColor)
        }
    }
}