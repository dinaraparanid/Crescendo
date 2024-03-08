package com.paranid5.crescendo.core.common.metadata

import kotlinx.serialization.Serializable

@Serializable
data class VideoMetadata(
    override val title: String? = null,
    override val author: String? = null,
    override val covers: List<String> = listOf(),
    override val durationMillis: Long = 0,
    val isLiveStream: Boolean = false
) : Metadata