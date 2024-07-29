package com.paranid5.crescendo.data.playback

import com.paranid5.crescendo.domain.playback.PlayingStatePublisher
import com.paranid5.crescendo.domain.playback.PlayingStateSubscriber
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class PlayingStateDataStoreImpl : PlayingStateSubscriber, PlayingStatePublisher {
    private val _isPlayingState = MutableStateFlow(false)

    override val isPlayingState = _isPlayingState.asStateFlow()

    override fun updatePlaying(isPlaying: Boolean) =
        _isPlayingState.update { isPlaying }
}
