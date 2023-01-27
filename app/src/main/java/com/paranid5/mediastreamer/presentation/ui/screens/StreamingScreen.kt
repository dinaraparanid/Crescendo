package com.paranid5.mediastreamer.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.StorageHandler
import com.paranid5.mediastreamer.data.VideoMetadata
import com.paranid5.mediastreamer.presentation.BroadcastReceiver
import com.paranid5.mediastreamer.presentation.ui.theme.LocalAppColors
import com.paranid5.mediastreamer.presentation.ui_handlers.StreamingUIHandler
import com.paranid5.mediastreamer.utils.extensions.timeString
import com.paranid5.mediastreamer.utils.extensions.toPlaybackPosition
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.androidx.compose.get

const val Broadcast_IS_PLAYING_CHANGED =
    "com.paranid5.mediastreamer.presentation.ui.screens.IS_PLAYING_CHANGED"

const val IS_PLAYING_ARG = "is_playing"

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
private fun VideoCover(metadata: VideoMetadata?, modifier: Modifier = Modifier) = GlideImage(
    modifier = modifier.padding(horizontal = 10.dp),
    model = metadata?.cover ?: painterResource(R.drawable.cover_thumbnail),
    contentDescription = stringResource(R.string.video_cover),
)

@Composable
private fun PlaybackSlider(
    metadata: VideoMetadata?,
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = get(),
    streamingUIHandler: StreamingUIHandler = get()
) {
    val colors = LocalAppColors.current.value

    val videoLength = metadata?.lenInMillis ?: 0
    val millisInPercentage = videoLength / 100F

    val curPosition by storageHandler.playbackPosition.collectAsState()
    val curPositionPercentage = curPosition / millisInPercentage

    Column(modifier.padding(horizontal = 10.dp)) {
        Slider(
            value = curPositionPercentage,
            valueRange = 0F..100F,
            colors = SliderDefaults.colors(
                thumbColor = colors.primary,
                activeTrackColor = colors.primary
            ),
            onValueChange = { percentages ->
                streamingUIHandler.sendSeekToBroadcast(
                    percentages.toPlaybackPosition(millisInPercentage)
                )
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
    val isPlayingState = MutableStateFlow(true)
    val isPlaying by isPlayingState.collectAsState()

    BroadcastReceiver(action = Broadcast_IS_PLAYING_CHANGED) { _, intent ->
        isPlayingState.value = intent!!.getBooleanExtra(IS_PLAYING_ARG, !isPlaying)
    }

    when {
        isPlaying -> IconButton(
            modifier = modifier,
            onClick = { streamingUIHandler.sendPauseBroadcast() }
        ) {
            Icon(
                painter = painterResource(R.drawable.pause),
                contentDescription = stringResource(R.string.pause)
            )
        }

        else -> IconButton(
            modifier = modifier,
            onClick = { streamingUIHandler.sendResumeBroadcast() }
        ) {
            Icon(
                painter = painterResource(R.drawable.play),
                contentDescription = stringResource(R.string.play)
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
    Icon(
        painter = painterResource(R.drawable.prev_track),
        contentDescription = stringResource(R.string.ten_secs_back)
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
    Icon(
        painter = painterResource(R.drawable.next_track),
        contentDescription = stringResource(R.string.ten_secs_forward)
    )
}

@Composable
private fun PlaybackButtons(modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth()) {
        SeekTo10SecsBackButton()
        PlayButton()
        SeekTo10SecsForwardButton()
    }
}