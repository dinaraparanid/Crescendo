package com.paranid5.crescendo.data.playback

import com.paranid5.crescendo.data.datastore.PlaybackDataStore
import com.paranid5.crescendo.domain.playback.RepeatingPublisher

internal class RepeatingPublisherImpl(
    private val playbackDataStore: PlaybackDataStore,
) : RepeatingPublisher {
    override suspend fun updateRepeating(isRepeating: Boolean) =
        playbackDataStore.storeRepeating(isRepeating)
}
