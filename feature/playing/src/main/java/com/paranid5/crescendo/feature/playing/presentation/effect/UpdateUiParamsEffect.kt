package com.paranid5.crescendo.feature.playing.presentation.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.feature.playing.view_model.PlayingUiIntent

@Composable
internal fun UpdateUiParamsEffect(
    screenAudioStatus: AudioStatus,
    coverAlpha: Float,
    onUiIntent: (PlayingUiIntent) -> Unit,
) = LaunchedEffect(screenAudioStatus, coverAlpha) {
    onUiIntent(
        PlayingUiIntent.UpdateState.UpdateUiParams(
            screenAudioStatus = screenAudioStatus,
            coverAlpha = coverAlpha,
        )
    )
}
