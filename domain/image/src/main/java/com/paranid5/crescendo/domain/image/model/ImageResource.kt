package com.paranid5.crescendo.domain.image.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@JvmInline
value class ImageResource(@DrawableRes val value: Int) : Parcelable
