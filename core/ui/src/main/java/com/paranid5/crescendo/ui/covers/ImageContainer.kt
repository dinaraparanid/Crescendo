package com.paranid5.crescendo.ui.covers

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
sealed interface ImageContainer : Parcelable {
    @Parcelize
    @Immutable
    data class Uri(val value: String? = null) : ImageContainer

    @Parcelize
    @Immutable
    data class Bytes(
        @IgnoredOnParcel
        val value: ByteArray? = null,
    ) : ImageContainer

    @Parcelize
    @Immutable
    data class Bitmap(
        @IgnoredOnParcel
        val value: android.graphics.Bitmap? = null,
    ) : ImageContainer
}

inline val ImageContainer.data: Any?
    get() = when (this) {
        is ImageContainer.Bytes -> value
        is ImageContainer.Uri -> value
        is ImageContainer.Bitmap -> value
    }
