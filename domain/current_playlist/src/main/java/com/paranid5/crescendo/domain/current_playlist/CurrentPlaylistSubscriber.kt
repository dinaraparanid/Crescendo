package com.paranid5.crescendo.domain.current_playlist

import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.utils.extensions.timeString
import com.paranid5.crescendo.utils.extensions.totalDurationMillis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface CurrentPlaylistSubscriber {
    val currentPlaylistFlow: Flow<List<Track>>
}

inline val CurrentPlaylistSubscriber.currentPlaylistSizeFlow
    get() = currentPlaylistFlow.map { it.size }

inline val CurrentPlaylistSubscriber.currentPlaylistDurationMillisFlow
    get() = currentPlaylistFlow.map { it.totalDurationMillis }

inline val CurrentPlaylistSubscriber.currentPlaylistDurationStrFlow
    get() = currentPlaylistDurationMillisFlow.map { it.timeString }
