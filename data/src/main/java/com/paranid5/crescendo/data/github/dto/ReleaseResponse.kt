package com.paranid5.crescendo.data.github.dto

import com.paranid5.crescendo.domain.github.model.Release
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReleaseResponse(
    @SerialName("html_url") val htmlUrl: String,
    @SerialName("tag_name") val tagName: String,
    @SerialName("name") val name: String,
    @SerialName("body") val body: String,
)

internal fun ReleaseResponse.toModel() = Release(
    htmlUrl = htmlUrl,
    tagName = tagName,
    name = name,
    body = body,
)
