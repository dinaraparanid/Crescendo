package com.paranid5.crescendo.data.properties

import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.data.datastore.DataStoreProvider

val DataStoreProvider.tracksPlaybackPositionFlow
    get() = playbackStateDataSource.tracksPlaybackPositionFlow

suspend fun DataStoreProvider.storeTracksPlaybackPosition(position: Long) =
    playbackStateDataSource.storeTracksPlaybackPosition(position)

val DataStoreProvider.streamPlaybackPositionFlow
    get() = playbackStateDataSource.streamPlaybackPositionFlow

suspend fun DataStoreProvider.storeStreamPlaybackPosition(position: Long) =
    playbackStateDataSource.storeStreamPlaybackPosition(position)

val DataStoreProvider.isRepeatingFlow
    get() = playbackStateDataSource.isRepeatingFlow

suspend fun DataStoreProvider.storeRepeating(isRepeating: Boolean) =
    playbackStateDataSource.storeIsRepeating(isRepeating)

val DataStoreProvider.audioStatusFlow
    get() = playbackStateDataSource.audioStatusFlow

suspend fun DataStoreProvider.storeAudioStatus(audioStatus: AudioStatus) =
    playbackStateDataSource.storeAudioStatus(audioStatus)