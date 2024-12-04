package com.paranid5.crescendo.data.genius.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SearchResponse(@SerialName("hits") val hits: List<SearchHitResponse>) {
    val songIds = hits.map { it.result.id }
}

@Serializable
internal data class SearchHitResponse(@SerialName("result") val result: SongIdResponse)

@Serializable
internal data class SongIdResponse(@SerialName("id") val id: Long)
