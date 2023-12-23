package com.paranid5.crescendo.domain.metadata

import android.os.Parcelable

sealed interface Metadata : Parcelable {
    val title: String?
    val author: String?
    val covers: List<String>
    val durationMillis: Long
}