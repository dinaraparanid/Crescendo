package com.paranid5.crescendo.data.properties

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.domain.media.AudioStatus

inline val StorageHandler.tracksPlaybackPositionFlow
    get() = playbackStateProvider.tracksPlaybackPositionFlow

suspend inline fun StorageHandler.storeTracksPlaybackPosition(position: Long) =
    playbackStateProvider.storeTracksPlaybackPosition(position)

inline val StorageHandler.streamPlaybackPositionFlow
    get() = playbackStateProvider.streamPlaybackPositionFlow

suspend inline fun StorageHandler.storeStreamPlaybackPosition(position: Long) =
    playbackStateProvider.storeStreamPlaybackPosition(position)

inline val StorageHandler.isRepeatingFlow
    get() = playbackStateProvider.isRepeatingFlow

suspend inline fun StorageHandler.storeIsRepeating(isRepeating: Boolean) =
    playbackStateProvider.storeIsRepeating(isRepeating)

inline val StorageHandler.audioStatusFlow
    get() = playbackStateProvider.audioStatusFlow

suspend inline fun StorageHandler.storeAudioStatus(audioStatus: AudioStatus) =
    playbackStateProvider.storeAudioStatus(audioStatus)