package com.paranid5.mediastreamer.presentation.streaming

import android.graphics.Bitmap
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.StorageHandler
import com.paranid5.mediastreamer.data.VideoMetadata
import com.paranid5.mediastreamer.utils.BroadcastReceiver
import com.paranid5.mediastreamer.presentation.composition_locals.LocalStreamState
import com.paranid5.mediastreamer.presentation.composition_locals.StreamStates
import com.paranid5.mediastreamer.presentation.ui.theme.LocalAppColors
import com.paranid5.mediastreamer.utils.GlideUtils
import com.paranid5.mediastreamer.utils.OnBackPressedHandler
import com.paranid5.mediastreamer.utils.extensions.timeString
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
            metadata = metadata,
            modifier = Modifier.constrainAs(cover) {
                top.linkTo(parent.top, margin = 20.dp)
                start.linkTo(parent.start, margin = 20.dp)
                end.linkTo(parent.end, margin = 20.dp)
                bottom.linkTo(slider.top, margin = 10.dp)
                height = Dimension.fillToConstraints
                width = Dimension.fillToConstraints
            }
        )

        PlaybackSlider(
            metadata = metadata,
            modifier = Modifier.constrainAs(slider) {
                centerVerticallyTo(parent)
                start.linkTo(parent.start, margin = 20.dp)
                end.linkTo(parent.end, margin = 20.dp)
            }
        )

        TitleAndPropertiesButton(
            metadata = metadata,
            modifier = Modifier.constrainAs(titleAndPropertiesButton) {
                top.linkTo(slider.bottom, margin = 15.dp)
                start.linkTo(parent.start, margin = 20.dp)
                end.linkTo(parent.end, margin = 20.dp)
            }
        )

        PlaybackButtons(
            streamingPresenter = viewModel.presenter,
            modifier = Modifier.constrainAs(playbackButtons) {
                top.linkTo(titleAndPropertiesButton.bottom, margin = 15.dp)
                start.linkTo(parent.start, margin = 20.dp)
                end.linkTo(parent.end, margin = 20.dp)
            }
        )

        UtilsButtons(
            Modifier.constrainAs(utilsButtons) {
                top.linkTo(playbackButtons.bottom, margin = 5.dp)
                start.linkTo(parent.start, margin = 20.dp)
                end.linkTo(parent.end, margin = 20.dp)
            }
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun VideoCover(metadata: VideoMetadata?, modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current.value
    val glideUtils = GlideUtils(LocalContext.current)
    var coverModel by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(key1 = metadata) {
        coverModel = metadata?.let { glideUtils.getVideoCoverAsync(it).await() }
    }

    GlideImage(
        modifier = modifier
            .shadow(
                elevation = 80.dp,
                shape = RoundedCornerShape(5.dp),
                ambientColor = colors.primary,
                spotColor = colors.primary
            )
            .border(
                width = 30.dp,
                color = Color.Transparent,
                shape = RoundedCornerShape(30.dp)
            ),
        model = coverModel ?: painterResource(R.drawable.cover_thumbnail),
        contentDescription = stringResource(R.string.video_cover),
    ) { requestBuilder ->
        requestBuilder
            .centerCrop()
            .transition(DrawableTransitionOptions.withCrossFade(1000))
    }
}

@Composable
private fun PlaybackSlider(
    metadata: VideoMetadata?,
    modifier: Modifier = Modifier,
    streamingUIHandler: StreamingUIHandler = get(),
    storageHandler: StorageHandler = get()
) {
    val colors = LocalAppColors.current.value
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
                thumbColor = colors.primary,
                activeTrackColor = colors.primary
            ),
            onValueChange = { curPosition = it.toLong() },
            onValueChangeFinished = {
                streamingUIHandler.sendSeekToBroadcast(curPosition)
            }
        )

        Row(Modifier.fillMaxWidth()) {
            Text(curPosition.timeString)
            Spacer(Modifier.weight(1F))
            Text(videoLength.timeString)
        }
    }
}