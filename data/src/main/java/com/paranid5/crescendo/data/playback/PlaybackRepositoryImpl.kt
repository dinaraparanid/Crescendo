package com.paranid5.crescendo.data.playback

import com.paranid5.crescendo.domain.playback.PlaybackRepository
import com.paranid5.crescendo.domain.playback.PlaybackRepository.Companion.UNDEFINED_AUDIO_SESSION_ID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class PlaybackRepositoryImpl : PlaybackRepository {
    private val _isPlayingState = MutableStateFlow(false)

    override val isPlayingState = _isPlayingState.asStateFlow()

    private val _audioSessionIdState = MutableStateFlow(UNDEFINED_AUDIO_SESSION_ID)

    override val audioSessionIdState = _audioSessionIdState.asStateFlow()

    override fun updatePlaying(isPlaying: Boolean) =
        _isPlayingState.update { isPlaying }

    override fun updateAudioSessionId(audioSessionId: Int) =
        _audioSessionIdState.update { audioSessionId }
}
