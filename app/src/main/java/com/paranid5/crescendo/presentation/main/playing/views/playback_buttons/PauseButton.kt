package com.paranid5.crescendo.presentation.main.playing.views.playback_buttons

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.R
import com.paranid5.crescendo.domain.media.AudioStatus
import com.paranid5.crescendo.presentation.main.playing.PlayingUIHandler
import com.paranid5.crescendo.presentation.main.playing.PlayingViewModel
import com.paranid5.crescendo.presentation.main.playing.properties.setAudioStatus
import com.paranid5.crescendo.presentation.ui.extensions.simpleShadow
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun PauseButton(
    viewModel: PlayingViewModel,
    audioStatus: AudioStatus,
    paletteColor: Color,
    modifier: Modifier = Modifier,
    playingUIHandler: PlayingUIHandler = koinInject()
) {
    val coroutineScope = rememberCoroutineScope()

    IconButton(
        modifier = modifier.simpleShadow(color = paletteColor),
        onClick = {
            coroutineScope.launch {
                viewModel.setAudioStatus(audioStatus)
            }

            playingUIHandler.sendPauseBroadcast(audioStatus)
        }
    ) {
        PauseIcon(
            paletteColor = paletteColor,
            modifier = Modifier.size(50.dp)
        )
    }
}

@Composable
private fun PauseIcon(paletteColor: Color, modifier: Modifier = Modifier) =
    Icon(
        modifier = modifier,
        painter = painterResource(R.drawable.pause),
        contentDescription = stringResource(R.string.pause),
        tint = paletteColor
    )