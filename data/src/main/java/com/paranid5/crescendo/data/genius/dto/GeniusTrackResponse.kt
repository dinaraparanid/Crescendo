package com.paranid5.crescendo.data.genius.dto

import com.paranid5.crescendo.domain.genius.model.GeniusTrack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GeniusTrackResponse(
    @SerialName("id") val id: Long,
    @SerialName("title") val title: String,
    @SerialName("artist_names") val artists: String,
    @SerialName("url") val url: String,
    @SerialName("album") val album: AlbumResponse?,
    @SerialName("header_image_url") val headerImageUrl: String?,
    @SerialName("song_art_image_url") val songArtImageUrl: String?,
)

@Serializable
internal data class AlbumResponse(
    @SerialName("name") val title: String,
    @SerialName("cover_art_url") val cover: String?,
)

internal fun GeniusTrackResponse.toModel() = GeniusTrack(
    id = id,
    title = title,
    artists = artists,
    album = album?.title,
    url = url,
    covers = listOfNotNull(headerImageUrl, songArtImageUrl, album?.cover),
)
