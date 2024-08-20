package com.paranid5.crescendo.feature.playing.view_model

import com.paranid5.crescendo.core.common.PlaybackStatus
import com.paranid5.crescendo.core.common.tracks.Track

sealed interface PlayingUiIntent {

    sealed interface UpdateState : PlayingUiIntent {
        data class UpdateUiParams(
            val screenPlaybackStatus: PlaybackStatus,
            val coverAlpha: Float,
        ) : UpdateState

        data object LikeClick : UpdateState

        data class AddTrackToPlaylist(val track: Track) : UpdateState
    }

    sealed interface Lifecycle : PlayingUiIntent {
        data object OnStart : Lifecycle
        data object OnStop : Lifecycle
    }

    sealed interface Playback : PlayingUiIntent {
        data object SeekToLiveStreamRealPosition : Playback
        data class SeekTo(val position: Long) : Playback
        data object PrevButtonClick : Playback
        data object PauseButtonClick : Playback
        data object PlayButtonClick : Playback
        data object NextButtonClick : Playback
        data object RepeatClick : Playback
    }

    sealed interface ScreenEffect : PlayingUiIntent {
        data class ShowTrimmer(val trackUri: String) : ScreenEffect
        data object ShowAudioEffects : ScreenEffect
        data object ShowMetaEditor : ScreenEffect
        data object ClearScreenEffect : ScreenEffect
    }
}
