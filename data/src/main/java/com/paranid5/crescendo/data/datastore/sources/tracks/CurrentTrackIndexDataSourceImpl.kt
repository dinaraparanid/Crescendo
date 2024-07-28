package com.paranid5.crescendo.data.datastore.sources.tracks

import com.paranid5.crescendo.data.datastore.DataStoreProvider
import com.paranid5.crescendo.data.properties.currentTrackIndexFlow
import com.paranid5.crescendo.data.properties.storeCurrentTrackIndex
import com.paranid5.crescendo.domain.sources.tracks.CurrentTrackIndexPublisher
import com.paranid5.crescendo.domain.sources.tracks.CurrentTrackIndexSubscriber

class CurrentTrackIndexSubscriberImpl(private val dataStoreProvider: DataStoreProvider) :
    CurrentTrackIndexSubscriber {
    override val currentTrackIndexFlow by lazy {
        dataStoreProvider.currentTrackIndexFlow
    }
}

class CurrentTrackIndexPublisherImpl(private val dataStoreProvider: DataStoreProvider) :
    CurrentTrackIndexPublisher {
    override suspend fun setCurrentTrackIndex(index: Int) =
        dataStoreProvider.storeCurrentTrackIndex(index)
}