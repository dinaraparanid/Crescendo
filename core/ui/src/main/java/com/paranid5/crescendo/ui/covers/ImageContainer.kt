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
    ) : ImageContainer {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Bytes

            return when {
                value != null -> when {
                    other.value == null -> false
                    value.contentEquals(other.value).not() -> false
                    else -> true
                }

                else -> other.value == null
            }
        }

        override fun hashCode(): Int = value?.contentHashCode() ?: 0
    }

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
