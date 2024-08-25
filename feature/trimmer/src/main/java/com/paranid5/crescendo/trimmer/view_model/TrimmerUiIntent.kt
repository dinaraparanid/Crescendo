package com.paranid5.crescendo.trimmer.view_model

import androidx.compose.ui.focus.FocusState
import com.paranid5.crescendo.trimmer.domain.entities.ShownEffects

sealed interface TrimmerUiIntent {

    data class LoadTrack(val trackPath: String) : TrimmerUiIntent

    data class UpdateFocusEvent(val focusEvent: FocusState) : TrimmerUiIntent

    data class ShowEffect(val shownEffects: ShownEffects) : TrimmerUiIntent

    sealed interface Lifecycle : TrimmerUiIntent {
        data object OnStart : Lifecycle
        data object OnStop : Lifecycle
    }

    sealed interface Waveform : TrimmerUiIntent {
        data object ZoomIn : Waveform
        data object ZoomOut : Waveform
        data class UpdateZoomLevel(val zoom: Int) : Waveform
        data class UpdateZoomSteps(val zoomSteps: Int) : Waveform
    }

    sealed interface Player : TrimmerUiIntent {
        data object SeekTenSecsBack : Player
        data object SeekTenSecsForward : Player
        data object UpdatePlayingState : Player
        data class UpdatePitch(val pitch: Float) : Player
        data class UpdateSpeed(val speed: Float) : Player
    }

    sealed interface Positions : TrimmerUiIntent {
        data class SeekTo(val playbackPosition: Long) : Positions
        data class UpdateStartPosition(val startPositionInMillis: Long) : Positions
        data class UpdateEndPosition(val endPositionInMillis: Long) : Positions
        data class UpdateFadeIn(val position: Long) : Positions
        data class UpdateFadeOut(val position: Long) : Positions
    }
}
