package com.paranid5.crescendo.data.playback

import com.paranid5.crescendo.domain.playback.AudioSessionIdPublisher
import com.paranid5.crescendo.domain.playback.AudioSessionIdSubscriber
import com.paranid5.crescendo.domain.playback.PlaybackRepository.Companion.UNDEFINED_AUDIO_SESSION_ID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class AudioSessionIdDataSourceImpl : AudioSessionIdSubscriber, AudioSessionIdPublisher {
    private val _audioSessionIdState = MutableStateFlow(UNDEFINED_AUDIO_SESSION_ID)

    override val audioSessionIdState = _audioSessionIdState.asStateFlow()

    override fun updateAudioSessionId(audioSessionId: Int) =
        _audioSessionIdState.update { audioSessionId }
}
