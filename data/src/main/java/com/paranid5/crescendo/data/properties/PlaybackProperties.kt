package com.paranid5.crescendo.data.properties

import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.data.StorageRepository

val StorageRepository.tracksPlaybackPositionFlow
    get() = playbackStateDataSource.tracksPlaybackPositionFlow

suspend fun StorageRepository.storeTracksPlaybackPosition(position: Long) =
    playbackStateDataSource.storeTracksPlaybackPosition(position)

val StorageRepository.streamPlaybackPositionFlow
    get() = playbackStateDataSource.streamPlaybackPositionFlow

suspend fun StorageRepository.storeStreamPlaybackPosition(position: Long) =
    playbackStateDataSource.storeStreamPlaybackPosition(position)

val StorageRepository.isRepeatingFlow
    get() = playbackStateDataSource.isRepeatingFlow

suspend fun StorageRepository.storeRepeating(isRepeating: Boolean) =
    playbackStateDataSource.storeIsRepeating(isRepeating)

val StorageRepository.audioStatusFlow
    get() = playbackStateDataSource.audioStatusFlow

suspend fun StorageRepository.storeAudioStatus(audioStatus: AudioStatus) =
    playbackStateDataSource.storeAudioStatus(audioStatus)