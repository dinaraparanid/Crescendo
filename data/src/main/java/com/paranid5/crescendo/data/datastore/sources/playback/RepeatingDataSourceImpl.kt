package com.paranid5.crescendo.data.datastore.sources.playback

import com.paranid5.crescendo.data.datastore.DataStoreProvider
import com.paranid5.crescendo.data.properties.isRepeatingFlow
import com.paranid5.crescendo.data.properties.storeRepeating
import com.paranid5.crescendo.domain.sources.playback.RepeatingPublisher
import com.paranid5.crescendo.domain.sources.playback.RepeatingSubscriber

class RepeatingSubscriberImpl(private val dataStoreProvider: DataStoreProvider) :
    RepeatingSubscriber {
    override val isRepeatingFlow by lazy {
        dataStoreProvider.isRepeatingFlow
    }
}

class RepeatingPublisherImpl(private val dataStoreProvider: DataStoreProvider) :
    RepeatingPublisher {
    override suspend fun setRepeating(isRepeating: Boolean) =
        dataStoreProvider.storeRepeating(isRepeating)
}