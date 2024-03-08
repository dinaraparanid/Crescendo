package com.paranid5.crescendo.core.common.metadata

sealed interface Metadata {
    val title: String?
    val author: String?
    val covers: List<String>
    val durationMillis: Long
}