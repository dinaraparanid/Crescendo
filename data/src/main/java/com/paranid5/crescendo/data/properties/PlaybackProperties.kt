package com.paranid5.crescendo.data.properties

import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.data.StorageRepository

inline val StorageRepository.tracksPlaybackPositionFlow
    get() = playbackStateDataSource.tracksPlaybackPositionFlow

suspend inline fun StorageRepository.storeTracksPlaybackPosition(position: Long) =
    playbackStateDataSource.storeTracksPlaybackPosition(position)

inline val StorageRepository.streamPlaybackPositionFlow
    get() = playbackStateDataSource.streamPlaybackPositionFlow

suspend inline fun StorageRepository.storeStreamPlaybackPosition(position: Long) =
    playbackStateDataSource.storeStreamPlaybackPosition(position)

inline val StorageRepository.isRepeatingFlow
    get() = playbackStateDataSource.isRepeatingFlow

suspend inline fun StorageRepository.storeRepeating(isRepeating: Boolean) =
    playbackStateDataSource.storeIsRepeating(isRepeating)

inline val StorageRepository.audioStatusFlow
    get() = playbackStateDataSource.audioStatusFlow

suspend inline fun StorageRepository.storeAudioStatus(audioStatus: AudioStatus) =
    playbackStateDataSource.storeAudioStatus(audioStatus)