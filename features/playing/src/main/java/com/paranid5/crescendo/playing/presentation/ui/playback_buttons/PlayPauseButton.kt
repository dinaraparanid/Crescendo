package com.paranid5.crescendo.playing.presentation.ui.playback_buttons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.playing.presentation.properties.compose.collectAudioStatusAsState
import com.paranid5.crescendo.playing.view_model.PlayingViewModel
import com.paranid5.crescendo.utils.extensions.collectLatestAsState
import com.paranid5.crescendo.utils.extensions.getBrightDominantOrPrimary
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun PlayPauseButton(
    audioStatus: AudioStatus,
    palette: Palette?,
    modifier: Modifier = Modifier,
) {
    val paletteColor = palette.getBrightDominantOrPrimary()
    val isPlaying by rememberIsPlaying(audioStatus)

    when {
        isPlaying -> PauseButton(
            audioStatus = audioStatus,
            paletteColor = paletteColor,
            modifier = modifier
        )

        else -> PlayButton(
            audioStatus = audioStatus,
            paletteColor = paletteColor,
            modifier = modifier
        )
    }
}

@Composable
private fun rememberIsPlaying(
    audioStatus: AudioStatus,
    viewModel: PlayingViewModel = koinViewModel(),
): State<Boolean> {
    val actualAudioStatus by viewModel.collectAudioStatusAsState()
    val isPlayerPlaying by viewModel.isPlayingState.collectLatestAsState()

    return remember(isPlayerPlaying, actualAudioStatus, audioStatus) {
        derivedStateOf { isPlayerPlaying && actualAudioStatus == audioStatus }
    }
}