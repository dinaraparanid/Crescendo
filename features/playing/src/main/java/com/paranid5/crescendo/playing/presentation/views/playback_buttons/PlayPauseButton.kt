package com.paranid5.crescendo.playing.presentation.views.playback_buttons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.core.impl.di.IS_PLAYING
import com.paranid5.crescendo.playing.presentation.PlayingViewModel
import com.paranid5.crescendo.playing.presentation.properties.compose.collectAudioStatusAsState
import com.paranid5.crescendo.utils.extensions.collectLatestAsState
import com.paranid5.crescendo.utils.extensions.getLightMutedOrPrimary
import com.paranid5.crescendo.utils.extensions.getVibrantOrBackground
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@Composable
internal fun PlayPauseButton(
    audioStatus: AudioStatus,
    palette: Palette?,
    modifier: Modifier = Modifier,
) {
    val primaryPaletteColor = palette.getLightMutedOrPrimary()
    val backgroundPaletteColor = palette.getVibrantOrBackground()
    val isPlaying by rememberIsPlaying(audioStatus)

    when {
        isPlaying -> PauseButton(
            audioStatus = audioStatus,
            primaryPaletteColor = primaryPaletteColor,
            backgroundPaletteColor = backgroundPaletteColor,
            modifier = modifier
        )

        else -> PlayButton(
            audioStatus = audioStatus,
            primaryPaletteColor = primaryPaletteColor,
            backgroundPaletteColor = backgroundPaletteColor,
            modifier = modifier
        )
    }
}

@Composable
private fun rememberIsPlaying(
    audioStatus: AudioStatus,
    viewModel: PlayingViewModel = koinViewModel(),
    isPlayingState: MutableStateFlow<Boolean> = koinInject(named(IS_PLAYING)),
): State<Boolean> {
    val actualAudioStatus by viewModel.collectAudioStatusAsState()
    val isPlayerPlaying by isPlayingState.collectLatestAsState()

    return remember(isPlayerPlaying, actualAudioStatus, audioStatus) {
        derivedStateOf { isPlayerPlaying && actualAudioStatus == audioStatus }
    }
}