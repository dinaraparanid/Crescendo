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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.StorageHandler
import com.paranid5.mediastreamer.presentation.ui.theme.LocalAppColors
import com.paranid5.mediastreamer.presentation.view_models.StreamingViewModel
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun StreamingScreen(
    modifier: Modifier = Modifier,
    streamingViewModel: StreamingViewModel = koinViewModel(),
    storageHandler: StorageHandler = get()
) {
    val colors = LocalAppColors.current.value
    val metadata by storageHandler.currentMetadata.collectAsState()

    val videoLength = metadata?.lenInMillis ?: 0
    val percentage = videoLength / 100F

    val curPosition by storageHandler.playbackPosition.collectAsState()
    val curPositionPercentage = curPosition / percentage

    Box(modifier.fillMaxSize()) {
        Column(Modifier.align(Alignment.Center)) {
            GlideImage(
                modifier = Modifier.padding(horizontal = 10.dp),
                model = metadata?.cover ?: painterResource(id = R.drawable.cover_thumbnail),
                contentDescription = stringResource(id = R.string.video_cover)
            )

            Spacer(Modifier.height(10.dp))

            Slider(
                value = curPosition.toFloat(),
                valueRange = 0F..100F,
                colors = SliderDefaults.colors(
                    thumbColor = colors.primary,
                    activeTrackColor = colors.primary
                ),
                onValueChange = {
                    // TODO: Seek to position
                }
            )

            Spacer(Modifier.height(10.dp))

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                Text(
                    text = metadata?.title ?: stringResource(id = R.string.stream_no_name),
                    color = colors.primary,
                    fontSize = 18.sp
                )

                Spacer(Modifier.height(5.dp))

                Text(
                    text = metadata?.author ?: stringResource(id = R.string.unknown_streamer),
                    color = colors.primary,
                    fontSize = 16.sp
                )
            }

            Row {
                IconButton(onClick = { /*TODO: 10 secs back*/ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.prev_track),
                        contentDescription = stringResource(id = R.string.ten_secs_back)
                    )
                }

                IconButton(onClick = { /*TODO: play / pause*/ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.prev_track),
                        contentDescription = stringResource(id = R.string.ten_secs_back)
                    )
                }

                IconButton(onClick = { /*TODO: 10 secs forward*/ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.next_track),
                        contentDescription = stringResource(id = R.string.ten_secs_forward)
                    )
                }
            }
        }
    }
}