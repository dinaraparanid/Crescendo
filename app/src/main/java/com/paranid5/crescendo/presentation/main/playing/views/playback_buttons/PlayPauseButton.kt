package com.paranid5.crescendo.presentation.main.playing.views.playback_buttons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.IS_PLAYING
import com.paranid5.crescendo.domain.media.AudioStatus
import com.paranid5.crescendo.presentation.main.playing.PlayingViewModel
import com.paranid5.crescendo.presentation.main.playing.properties.compose.collectAudioStatusAsState
import com.paranid5.crescendo.presentation.ui.extensions.collectLatestAsState
import com.paranid5.crescendo.presentation.ui.extensions.getLightMutedOrPrimary
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@Composable
fun PlayPauseButton(
    viewModel: PlayingViewModel,
    audioStatus: AudioStatus,
    palette: Palette?,
    modifier: Modifier = Modifier,
) {
    val paletteColor = palette.getLightMutedOrPrimary()
    val isPlaying by rememberIsPlaying(viewModel, audioStatus)

    when {
        isPlaying -> PauseButton(
            viewModel = viewModel,
            audioStatus = audioStatus,
            paletteColor = paletteColor,
            modifier = modifier
        )

        else -> PlayButton(
            viewModel = viewModel,
            audioStatus = audioStatus,
            paletteColor = paletteColor,
            modifier = modifier
        )
    }
}

@Composable
private fun rememberIsPlaying(
    viewModel: PlayingViewModel,
    audioStatus: AudioStatus,
    isPlayingState: MutableStateFlow<Boolean> = koinInject(named(IS_PLAYING)),
): State<Boolean> {
    val actualAudioStatus by viewModel.collectAudioStatusAsState()
    val isPlayerPlaying by isPlayingState.collectLatestAsState()

    return remember(isPlayerPlaying, actualAudioStatus, audioStatus) {
        derivedStateOf { isPlayerPlaying && actualAudioStatus == audioStatus }
    }
}