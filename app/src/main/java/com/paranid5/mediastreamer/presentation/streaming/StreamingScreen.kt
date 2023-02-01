package com.paranid5.mediastreamer.presentation.streaming

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.StorageHandler
import com.paranid5.mediastreamer.data.VideoMetadata
import com.paranid5.mediastreamer.presentation.BroadcastReceiver
import com.paranid5.mediastreamer.presentation.ui.theme.LocalAppColors
import com.paranid5.mediastreamer.utils.extensions.timeString
import org.koin.androidx.compose.get

private const val BROADCAST_LOCATION = "com.paranid5.mediastreamer.presentation.ui.screens"
const val Broadcast_IS_PLAYING_CHANGED = "$BROADCAST_LOCATION.IS_PLAYING_CHANGED"
const val Broadcast_CUR_POSITION_CHANGED = "$BROADCAST_LOCATION.CUR_POSITION_CHANGED"
const val Broadcast_IS_REPEATING_CHANGED = "$BROADCAST_LOCATION.IS_REPEATING_CHANGED"

const val IS_PLAYING_ARG = "is_playing"
const val CUR_POSITION_ARG = "cur_position"
const val IS_REPEATING_ARG = "is_repeating"

@Composable
fun StreamingScreen(
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = get()
) {
    val metadata by storageHandler.currentMetadataState.collectAsState()

    Box(modifier.fillMaxSize()) {
        Column(Modifier.align(Alignment.Center)) {
            VideoCover(
                metadata = metadata,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(10.dp))
            PlaybackSlider(metadata)
            Spacer(Modifier.height(15.dp))
            TitleAndSettings(metadata)
            Spacer(Modifier.height(15.dp))
            PlaybackButtons()
            Spacer(Modifier.height(5.dp))
            UtilsButtons()
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun VideoCover(metadata: VideoMetadata?, modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current.value

    GlideImage(
        modifier = modifier
            .padding(start = 20.dp, end = 20.dp, top = 20.dp)
            .border(width = 5.dp, color = Color.Transparent, shape = RoundedCornerShape(5.dp))
            .sizeIn(maxHeight = 300.dp)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(5.dp),
                ambientColor = colors.primary,
                spotColor = colors.primary
            ),
        model = metadata?.cover ?: painterResource(R.drawable.cover_thumbnail),
        contentDescription = stringResource(R.string.video_cover),
    ) { requestBuilder ->
        requestBuilder
            .error(R.drawable.cover_thumbnail)
            .placeholder(R.drawable.cover_thumbnail)
            .fallback(R.drawable.cover_thumbnail)
            .transition(DrawableTransitionOptions.withCrossFade())
    }
}

@Composable
private fun PlaybackSlider(
    metadata: VideoMetadata?,
    modifier: Modifier = Modifier,
    streamingUIHandler: StreamingUIHandler = get()
) {
    val colors = LocalAppColors.current.value
    val videoLength = metadata?.lenInMillis ?: 0
    var curPosition by remember { mutableStateOf(0L) }

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
private fun SettingsButton(modifier: Modifier = Modifier) {
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
private fun TitleAndSettings(metadata: VideoMetadata?, modifier: Modifier = Modifier) =
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
        SettingsButton()
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
private fun DownloadButton(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current.value

    IconButton(modifier = modifier, onClick = { /** TODO: download as mp3 */ }) {
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