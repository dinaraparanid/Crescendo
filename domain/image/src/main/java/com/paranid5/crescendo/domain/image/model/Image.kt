package com.paranid5.crescendo.domain.image.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
sealed interface Image : Parcelable {

    @Parcelize
    @Serializable
    @JvmInline
    value class Path(val value: ImagePath) : Image

    @Parcelize
    @Serializable
    @JvmInline
    value class Resource(val value: ImageResource) : Image

    @Parcelize
    @Serializable
    @JvmInline
    value class Url(val value: ImageUrl) : Image
}