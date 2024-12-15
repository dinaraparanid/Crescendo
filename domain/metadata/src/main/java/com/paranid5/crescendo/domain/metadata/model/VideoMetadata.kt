package com.paranid5.crescendo.domain.metadata.model

import com.paranid5.crescendo.domain.image.model.Image
import kotlinx.serialization.Serializable

@Serializable
data class VideoMetadata(
    override val title: String? = null,
    override val author: String? = null,
    override val covers: List<Image.Url> = listOf(),
    override val durationMillis: Long = 0,
    val isLiveStream: Boolean = false,
) : Metadata
