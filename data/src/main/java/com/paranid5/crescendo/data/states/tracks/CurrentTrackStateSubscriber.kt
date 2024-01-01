package com.paranid5.crescendo.data.states.tracks

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.currentTrackFlow
import com.paranid5.crescendo.domain.tracks.Track
import kotlinx.coroutines.flow.Flow

interface CurrentTrackStateSubscriber {
    val currentTrackFlow: Flow<Track?>
}

class CurrentTrackStateSubscriberImpl(storageHandler: StorageHandler) :
    CurrentTrackStateSubscriber {
    override val currentTrackFlow by lazy {
        storageHandler.currentTrackFlow
    }
}