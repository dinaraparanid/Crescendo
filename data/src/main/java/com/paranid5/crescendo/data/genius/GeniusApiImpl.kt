package com.paranid5.crescendo.data.genius

import arrow.core.Either
import arrow.core.flatten
import arrow.core.raise.either
import com.paranid5.crescendo.data.genius.dto.GeniusResponse
import com.paranid5.crescendo.data.genius.dto.GeniusTrackResponse
import com.paranid5.crescendo.data.genius.dto.SearchResponse
import com.paranid5.crescendo.data.genius.dto.toModel
import com.paranid5.crescendo.domain.genius.GeniusApi
import com.paranid5.crescendo.domain.genius.model.GeniusTrack
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url

internal class GeniusApiImpl(
    private val ktorClient: HttpClient,
    private val urlBuilder: GeniusApiUrlBuilder,
) : GeniusApi {
    override suspend fun findSimilarTracks(
        titleInput: String,
        artistInput: String,
    ): Either<Throwable, List<GeniusTrack>> = Either.catch {
        ktorClient
            .get {
                url(urlBuilder.buildSearchUrl())
                parameter("q", "$artistInput - $titleInput")
                bearerAuth(GeniusApiToken)
            }
            .body<GeniusResponse<SearchResponse>>()
            .response
            .songIds
            .map { requestTrackInfo(geniusTrackId = it) }
            .let { either { it.bindAll() } }
    }.flatten()

    override suspend fun requestTrackInfo(
        geniusTrackId: Long,
    ): Either<Throwable, GeniusTrack> = Either.catch {
        ktorClient
            .get {
                url(urlBuilder.buildSongsUrl(geniusTrackId))
                bearerAuth(GeniusApiToken)
            }
            .body<GeniusResponse<GeniusTrackResponse>>()
            .response
            .data
            .toModel()
    }
}
