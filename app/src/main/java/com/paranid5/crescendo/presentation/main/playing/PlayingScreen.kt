package com.paranid5.crescendo.presentation.main.playing

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.paranid5.crescendo.domain.media.AudioStatus
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.presentation.main.playing.properties.compose.collectAudioStatusAsState
import com.paranid5.crescendo.presentation.main.playing.properties.compose.collectCurrentMetadataAsState
import com.paranid5.crescendo.presentation.main.playing.properties.compose.collectDurationMillisAsState
import com.paranid5.crescendo.presentation.main.playing.views.PlayingScreenLandscape
import com.paranid5.crescendo.presentation.main.playing.views.PlayingScreenPortrait

@Composable
fun PlayingScreen(
    audioStatus: AudioStatus,
    coverAlpha: Float,
    modifier: Modifier = Modifier,
    viewModel: PlayingViewModel = koinActivityViewModel(),
) {
    val config = LocalConfiguration.current
    val isLiveStreaming by rememberIsLiveStreaming(audioStatus)
    val length by viewModel.collectDurationMillisAsState(audioStatus)

    when (config.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> PlayingScreenLandscape(
            audioStatus = audioStatus,
            durationMillis = length,
            coverAlpha = coverAlpha,
            isLiveStreaming = isLiveStreaming,
            modifier = modifier,
        )

        else -> PlayingScreenPortrait(
            durationMillis = length,
            coverAlpha = coverAlpha,
            audioStatus = audioStatus,
            isLiveStreaming = isLiveStreaming,
            modifier = modifier,
        )
    }
}

@Composable
internal fun rememberIsLiveStreaming(
    audioStatus: AudioStatus,
    viewModel: PlayingViewModel = koinActivityViewModel(),
): State<Boolean> {
    val currentMetadata by viewModel.collectCurrentMetadataAsState()

    return remember(audioStatus, currentMetadata?.isLiveStream) {
        derivedStateOf {
            audioStatus == AudioStatus.STREAMING && currentMetadata?.isLiveStream == true
        }
    }
}

@Composable
internal fun rememberIsWaveformEnabled(
    audioStatus: AudioStatus,
    viewModel: PlayingViewModel = koinActivityViewModel(),
): State<Boolean> {
    val actualAudioStatus by viewModel.collectAudioStatusAsState()

    return remember(actualAudioStatus, audioStatus) {
        derivedStateOf { actualAudioStatus == audioStatus }
    }
}