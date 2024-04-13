package com.paranid5.crescendo.domain.repositories

import com.paranid5.crescendo.core.common.tracks.Track
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow

interface CurrentPlaylistRepository {
    val tracksFlow: Flow<ImmutableList<Track>>

    fun replacePlaylistAsync(playlist: List<Track>): Job
}