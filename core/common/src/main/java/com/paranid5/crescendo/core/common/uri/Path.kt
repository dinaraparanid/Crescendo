package com.paranid5.crescendo.core.common.uri

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
@Immutable
@JvmInline
value class Path(private val value: String) : Parcelable {
    override fun toString(): String = value
}
