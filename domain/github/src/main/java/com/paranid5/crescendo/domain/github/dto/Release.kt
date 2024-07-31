package com.paranid5.crescendo.domain.github.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Release(
    @SerialName("html_url") val htmlUrl: String,
    @SerialName("tag_name") val tagName: String,
    @SerialName("name") val name: String,
    @SerialName("body") val body: String,
)
