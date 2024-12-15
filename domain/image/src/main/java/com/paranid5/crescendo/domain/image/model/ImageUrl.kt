package com.paranid5.crescendo.domain.image.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@JvmInline
value class ImageUrl(val value: String) : Parcelable
