package com.paranid5.crescendo.domain.genius.model

data class GeniusTrack(
    val id: Long,
    val title: String,
    val artists: String,
    val album: String?,
    val url: String,
    val covers: List<String>,
)
