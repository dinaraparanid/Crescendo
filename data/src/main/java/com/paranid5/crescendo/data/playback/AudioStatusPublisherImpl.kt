package com.paranid5.crescendo.data.playback

import com.paranid5.crescendo.core.common.PlaybackStatus
import com.paranid5.crescendo.data.datastore.PlaybackDataStore
import com.paranid5.crescendo.domain.playback.AudioStatusPublisher

internal class AudioStatusPublisherImpl(
    private val playbackDataStore: PlaybackDataStore,
) : AudioStatusPublisher {
    override suspend fun updateAudioStatus(playbackStatus: PlaybackStatus) =
        playbackDataStore.storeAudioStatus(playbackStatus)
}
