package com.paranid5.crescendo.playing.presentation.views.playback_buttons

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.playing.domain.PlayingInteractor
import com.paranid5.crescendo.playing.presentation.PlayingViewModel
import com.paranid5.crescendo.utils.extensions.simpleShadow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
internal fun PlayButton(
    audioStatus: AudioStatus,
    paletteColor: Color,
    modifier: Modifier = Modifier,
    viewModel: PlayingViewModel = koinViewModel(),
    interactor: PlayingInteractor = koinInject()
) {
    val coroutineScope = rememberCoroutineScope()

    IconButton(
        modifier = modifier.simpleShadow(color = paletteColor),
        onClick = {
            coroutineScope.launch {
                viewModel.setAudioStatus(audioStatus)
            }

            interactor.startStreamingOrSendResumeBroadcast(audioStatus)
        }
    ) {
        PlayIcon(
            color = paletteColor,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )
    }
}

@Composable
private fun PlayIcon(color: Color, modifier: Modifier = Modifier) =
    Icon(
        modifier = modifier,
        painter = painterResource(R.drawable.play),
        contentDescription = stringResource(R.string.play),
        tint = color
    )