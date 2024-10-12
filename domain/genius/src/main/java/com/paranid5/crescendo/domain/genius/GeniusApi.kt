package com.paranid5.crescendo.domain.genius

import arrow.core.Either
import com.paranid5.crescendo.domain.genius.model.GeniusTrack

interface GeniusApi {
    suspend fun findSimilarTracks(
        titleInput: String,
        artistInput: String,
    ): Either<Throwable, List<GeniusTrack>>

    suspend fun requestTrackInfo(geniusTrackId: Long): Either<Throwable, GeniusTrack>
}