package com.paranid5.crescendo.domain.metadata.model

import com.paranid5.crescendo.domain.image.model.Image
import kotlinx.serialization.Serializable

@Serializable
data class AudioMetadata(
    override val title: String? = null,
    override val author: String? = null,
    @JvmField val album: String? = null,
    override val covers: List<Image.Path> = listOf(),
    override val durationMillis: Long = 0,
) : Metadata
