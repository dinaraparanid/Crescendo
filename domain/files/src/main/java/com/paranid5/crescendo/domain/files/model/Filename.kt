package com.paranid5.crescendo.domain.files.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@JvmInline
value class Filename(private val value: String) : Parcelable {
    override fun toString(): String = value
}
