package com.paranid5.crescendo.domain.tracks

import com.paranid5.crescendo.core.common.tracks.Track

interface TracksMediaStoreSubscriber {
    suspend fun getAllTracksFromMediaStore(): List<Track>

    suspend fun getTrackFromMediaStore(trackPath: String): Track?
}
