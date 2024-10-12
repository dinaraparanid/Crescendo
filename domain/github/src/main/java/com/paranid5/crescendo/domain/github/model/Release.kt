package com.paranid5.crescendo.domain.github.model

data class Release(
    val htmlUrl: String,
    val tagName: String,
    val name: String,
    val body: String,
)
