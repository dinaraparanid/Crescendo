package com.paranid5.mediastreamer.presentation.streaming

import android.graphics.Bitmap
import android.os.Build
import android.widget.Toast
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.StorageHandler
import com.paranid5.mediastreamer.data.VideoMetadata
import com.paranid5.mediastreamer.presentation.BroadcastReceiver
import com.paranid5.mediastreamer.presentation.ui.theme.LocalAppColors
import com.paranid5.mediastreamer.utils.GlideUtils
import com.paranid5.mediastreamer.utils.extensions.timeString
import com.paranid5.mediastreamer.video_cash_service.VideoCashResponse
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
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = get()
) {
    val metadata by storageHandler.currentMetadataState.collectAsState()

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
            Modifier.constrainAs(playbackButtons) {
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
            .error(R.drawable.cover_thumbnail)
            .fallback(R.drawable.cover_thumbnail)
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

// ----------------------------------- Title And Settings -----------------------------------

@Composable
private fun TitleAndAuthor(metadata: VideoMetadata?, modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current.value

    Column(modifier) {
        Text(
            text = metadata?.title ?: stringResource(R.string.stream_no_name),
            color = colors.primary,
            fontSize = 18.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(5.dp))

        Text(
            text = metadata?.author ?: stringResource(R.string.unknown_streamer),
            color = colors.primary,
            fontSize = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun PropertiesButton(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current.value

    IconButton(modifier = modifier, onClick = { /*TODO*/ }) {
        Icon(
            modifier = Modifier
                .height(50.dp)
                .width(25.dp),
            painter = painterResource(R.drawable.three_dots),
            contentDescription = stringResource(R.string.settings),
            tint = colors.primary
        )
    }
}

@Composable
private fun TitleAndPropertiesButton(metadata: VideoMetadata?, modifier: Modifier = Modifier) =
    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        TitleAndAuthor(
            metadata = metadata,
            modifier = Modifier.weight(1F)
        )

        Spacer(Modifier.width(10.dp))
        PropertiesButton()
    }

// ----------------------------------- Playback Buttons -----------------------------------

@Composable
private fun PlayButton(
    modifier: Modifier = Modifier,
    streamingUIHandler: StreamingUIHandler = get()
) {
    val colors = LocalAppColors.current.value
    var isPlaying by remember { mutableStateOf(false) }

    BroadcastReceiver(action = Broadcast_IS_PLAYING_CHANGED) { _, intent ->
        isPlaying = intent!!.getBooleanExtra(IS_PLAYING_ARG, !isPlaying)
    }

    when {
        isPlaying -> IconButton(
            modifier = modifier,
            onClick = { streamingUIHandler.sendPauseBroadcast() }
        ) {
            Icon(
                modifier = Modifier.size(50.dp),
                painter = painterResource(R.drawable.pause),
                contentDescription = stringResource(R.string.pause),
                tint = colors.primary
            )
        }

        else -> IconButton(
            modifier = modifier,
            onClick = { streamingUIHandler.startStreamingOrSendResumeBroadcast() }
        ) {
            Icon(
                modifier = Modifier.size(50.dp),
                painter = painterResource(R.drawable.play),
                contentDescription = stringResource(R.string.play),
                tint = colors.primary
            )
        }
    }
}

@Composable
private fun SeekTo10SecsBackButton(
    modifier: Modifier = Modifier,
    streamingUIHandler: StreamingUIHandler = get()
) {
    val colors = LocalAppColors.current.value

    IconButton(
        modifier = modifier,
        onClick = { streamingUIHandler.sendSeekTo10SecsBackBroadcast() }
    ) {
        Icon(
            modifier = Modifier
                .width(100.dp)
                .height(50.dp),
            painter = painterResource(R.drawable.prev_track),
            contentDescription = stringResource(R.string.ten_secs_back),
            tint = colors.primary
        )
    }
}

@Composable
private fun SeekTo10SecsForwardButton(
    modifier: Modifier = Modifier,
    streamingUIHandler: StreamingUIHandler = get()
) {
    val colors = LocalAppColors.current.value

    IconButton(
        modifier = modifier,
        onClick = { streamingUIHandler.sendSeekTo10SecsForwardBroadcast() }
    ) {
        Icon(
            modifier = Modifier
                .width(100.dp)
                .height(50.dp),
            painter = painterResource(R.drawable.next_track),
            contentDescription = stringResource(R.string.ten_secs_forward),
            tint = colors.primary
        )
    }
}

@Composable
private fun PlaybackButtons(modifier: Modifier = Modifier) =
    Row(modifier.fillMaxWidth()) {
        SeekTo10SecsBackButton(Modifier.weight(2F))
        PlayButton(Modifier.weight(1F))
        SeekTo10SecsForwardButton(Modifier.weight(2F))
    }

// ----------------------------------- Utils Buttons -----------------------------------

@Composable
private fun EqualizerButton(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current.value

    IconButton(modifier = modifier, onClick = { /*TODO Equalizer*/ }) {
        Icon(
            modifier = Modifier.size(30.dp),
            painter = painterResource(R.drawable.equalizer),
            contentDescription = stringResource(R.string.equalizer),
            tint = colors.primary
        )
    }
}

@Composable
private fun RepeatButton(
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = get(),
    streamingUIHandler: StreamingUIHandler = get()
) {
    val colors = LocalAppColors.current.value
    var isRepeating by remember { mutableStateOf(storageHandler.isRepeatingState.value) }

    BroadcastReceiver(action = Broadcast_IS_REPEATING_CHANGED) { _, intent ->
        isRepeating = intent!!.getBooleanExtra(IS_REPEATING_ARG, false)
    }

    IconButton(modifier = modifier, onClick = { streamingUIHandler.sendChangeRepeatBroadcast() }) {
        Icon(
            modifier = Modifier.size(30.dp),
            painter = painterResource(if (isRepeating) R.drawable.repeat else R.drawable.no_repeat),
            contentDescription = stringResource(R.string.change_repeat),
            tint = colors.primary
        )
    }
}

@Composable
private fun LikeButton(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current.value
    var isLiked by remember { mutableStateOf(false) }

    BroadcastReceiver(action = Broadcast_IS_REPEATING_CHANGED) { _, intent ->
        // TODO: favourite database
    }

    IconButton(modifier = modifier, onClick = { /** TODO: favourite database */ }) {
        Icon(
            modifier = Modifier.size(30.dp),
            painter = painterResource(if (isLiked) R.drawable.like_filled else R.drawable.like),
            contentDescription = stringResource(R.string.favourites),
            tint = colors.primary
        )
    }
}

@Composable
private fun DownloadButton(
    modifier: Modifier = Modifier,
    streamingUIHandler: StreamingUIHandler = get()
) {
    val colors = LocalAppColors.current.value
    val context = LocalContext.current

    val errorStringRes = stringResource(R.string.error)
    val successfulCashingStringRes = stringResource(R.string.video_cashed)

    BroadcastReceiver(action = Broadcast_VIDEO_CASH_COMPLETED) { ctx, intent ->
        val status = intent!!.getSerializableExtra(VIDEO_CASH_STATUS)!! as VideoCashResponse

        Toast
            .makeText(
                context,
                when (status) {
                    is VideoCashResponse.Error -> {
                        val (httpCode, description) = status
                        "$errorStringRes $httpCode: $description"
                    }

                    VideoCashResponse.Success -> successfulCashingStringRes
                },
                Toast.LENGTH_LONG
            )
            .show()
    }

    IconButton(
        modifier = modifier, // TODO: save as video dialog
        onClick = { streamingUIHandler.launchVideoCashService(isSaveAsVideo = false) }
    ) {
        Icon(
            modifier = Modifier.size(30.dp),
            painter = painterResource(R.drawable.save_icon),
            contentDescription = stringResource(R.string.download_as_mp3),
            tint = colors.primary
        )
    }
}

@Composable
private fun UtilsButtons(modifier: Modifier = Modifier) =
    Row(modifier.fillMaxWidth()) {
        EqualizerButton(Modifier.weight(1F))
        RepeatButton(Modifier.weight(1F))
        LikeButton(Modifier.weight(1F))
        DownloadButton(Modifier.weight(1F))
    }