package com.paranid5.crescendo.domain.metadata.model

import com.paranid5.crescendo.domain.image.model.Image
import kotlinx.serialization.Serializable

@Serializable
sealed interface Metadata {
    val title: String?
    val author: String?
    val covers: List<Image>
    val durationMillis: Long
}
