package com.paranid5.crescendo.data.properties

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.domain.tracks.DefaultTrack
import com.paranid5.crescendo.domain.tracks.TrackOrder
import kotlinx.coroutines.flow.combine

inline val StorageHandler.currentTrackIndexFlow
    get() = tracksStateProvider.currentTrackIndexFlow

suspend inline fun StorageHandler.storeCurrentTrackIndex(index: Int) =
    tracksStateProvider.storeCurrentTrackIndex(index)

inline val StorageHandler.currentPlaylistFlow
    get() = tracksStateProvider.currentPlaylistFlow

suspend inline fun StorageHandler.storeCurrentPlaylist(playlist: List<DefaultTrack>) =
    tracksStateProvider.storeCurrentPlaylist(playlist)

inline val StorageHandler.trackOrderFlow
    get() = tracksStateProvider.trackOrderFlow

suspend inline fun StorageHandler.storeTrackOrder(trackOrder: TrackOrder) =
    tracksStateProvider.storeTrackOrder(trackOrder)

inline val StorageHandler.currentTrackFlow
    get() = combine(
        currentTrackIndexFlow,
        currentPlaylistFlow
    ) { trackInd, playlist ->
        playlist.getOrNull(trackInd)
    }