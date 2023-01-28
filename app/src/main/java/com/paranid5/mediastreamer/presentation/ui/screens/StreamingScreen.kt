package com.paranid5.mediastreamer.presentation.ui.screens

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
import com.paranid5.mediastreamer.presentation.ui_handlers.StreamingUIHandler
import com.paranid5.mediastreamer.utils.extensions.timeString
import org.koin.androidx.compose.get

private const val BROADCAST_LOCATION = "com.paranid5.mediastreamer.presentation.ui.screens"
const val Broadcast_IS_PLAYING_CHANGED = "$BROADCAST_LOCATION.IS_PLAYING_CHANGED"
const val Broadcast_CUR_POSITION_CHANGED = "$BROADCAST_LOCATION.CUR_POSITION_CHANGED"

const val IS_PLAYING_ARG = "is_playing"
const val CUR_POSITION_ARG = "cur_position"

@Composable
fun StreamingScreen(
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = get()
) {
    val metadata by storageHandler.currentMetadata.collectAsState()

    Box(modifier.fillMaxSize()) {
        Column(Modifier.align(Alignment.Center)) {
            VideoCover(metadata)
            Spacer(Modifier.height(10.dp))
            PlaybackSlider(metadata)
            Spacer(Modifier.height(10.dp))
            TitleAndAuthor(metadata)
            Spacer(Modifier.height(10.dp))
            PlaybackButtons()
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun VideoCover(metadata: VideoMetadata?, modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current.value

    GlideImage(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .border(width = 5.dp, color = Color.Transparent, shape = RoundedCornerShape(5.dp))
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

        Spacer(Modifier.height(5.dp))

        Row(Modifier.fillMaxWidth()) {
            Text(curPosition.timeString)
            Spacer(Modifier.weight(1F))
            Text(videoLength.timeString)
        }
    }
}

@Composable
private fun TitleAndAuthor(metadata: VideoMetadata?, modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current.value

    Column(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
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
private fun PlayButton(
    modifier: Modifier = Modifier,
    streamingUIHandler: StreamingUIHandler = get()
) {
    val colors = LocalAppColors.current.value
    var isPlaying by remember { mutableStateOf(true) }

    BroadcastReceiver(action = Broadcast_IS_PLAYING_CHANGED) { _, intent ->
        isPlaying = intent!!.getBooleanExtra(IS_PLAYING_ARG, !isPlaying)
    }

    when {
        isPlaying -> IconButton(
            modifier = modifier,
            onClick = { streamingUIHandler.sendPauseBroadcast() }
        ) {
            Icon(
                modifier = Modifier.width(50.dp).height(50.dp),
                painter = painterResource(R.drawable.pause),
                contentDescription = stringResource(R.string.pause),
                tint = colors.primary
            )
        }

        else -> IconButton(
            modifier = modifier,
            onClick = { streamingUIHandler.sendResumeBroadcast() }
        ) {
            Icon(
                modifier = Modifier.width(50.dp).height(50.dp),
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
) = IconButton(
    modifier = modifier,
    onClick = { streamingUIHandler.sendSeekTo10SecsBackBroadcast() }
) {
    val colors = LocalAppColors.current.value

    Icon(
        modifier = Modifier.width(100.dp).height(50.dp),
        painter = painterResource(R.drawable.prev_track),
        contentDescription = stringResource(R.string.ten_secs_back),
        tint = colors.primary
    )
}

@Composable
private fun SeekTo10SecsForwardButton(
    modifier: Modifier = Modifier,
    streamingUIHandler: StreamingUIHandler = get()
) = IconButton(
    modifier = modifier,
    onClick = { streamingUIHandler.sendSeekTo10SecsForwardBroadcast() }
) {
    val colors = LocalAppColors.current.value

    Icon(
        modifier = Modifier.width(100.dp).height(50.dp),
        painter = painterResource(R.drawable.next_track),
        contentDescription = stringResource(R.string.ten_secs_forward),
        tint = colors.primary
    )
}

@Composable
private fun PlaybackButtons(modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth()) {
        SeekTo10SecsBackButton(Modifier.weight(1F))
        PlayButton(Modifier.weight(1F))
        SeekTo10SecsForwardButton(Modifier.weight(1F))
    }
}