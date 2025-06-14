package com.paranid5.crescendo.domain.files.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@JvmInline
value class MediaFileExtension(private val value: String) : Parcelable {
    override fun toString() = value
}
