package com.paranid5.crescendo.playing.presentation

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.playing.presentation.effect.SubscribeOnBackResultEffect
import com.paranid5.crescendo.playing.presentation.properties.compose.collectAudioStatusAsState
import com.paranid5.crescendo.playing.presentation.properties.compose.collectCurrentMetadataAsState
import com.paranid5.crescendo.playing.presentation.properties.compose.collectDurationMillisAsState
import com.paranid5.crescendo.playing.presentation.ui.PlayingScreenLandscape
import com.paranid5.crescendo.playing.presentation.ui.PlayingScreenPortrait
import com.paranid5.crescendo.playing.view_model.PlayingBackResult
import com.paranid5.crescendo.playing.view_model.PlayingViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlayingScreen(
    audioStatus: AudioStatus,
    coverAlpha: Float,
    modifier: Modifier = Modifier,
    viewModel: PlayingViewModel = koinViewModel(),
    onBack: (PlayingBackResult) -> Unit,
) {
    val config = LocalConfiguration.current
    val isLiveStreaming by rememberIsLiveStreaming(audioStatus)
    val length by viewModel.collectDurationMillisAsState(audioStatus)

    SubscribeOnBackResultEffect(onBack = onBack)

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
    viewModel: PlayingViewModel = koinViewModel(),
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
    viewModel: PlayingViewModel = koinViewModel(),
): State<Boolean> {
    val actualAudioStatus by viewModel.collectAudioStatusAsState()

    return remember(actualAudioStatus, audioStatus) {
        derivedStateOf { actualAudioStatus == audioStatus }
    }
}