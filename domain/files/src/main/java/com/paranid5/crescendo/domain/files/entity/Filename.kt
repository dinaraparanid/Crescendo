package com.paranid5.crescendo.domain.files.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
@JvmInline
value class Filename(private val value: String) : Parcelable {
    override fun toString(): String = value
}
