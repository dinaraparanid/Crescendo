package com.paranid5.crescendo.domain.trimming

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class TrimRange(val startPointMillis: Long, val totalDurationMillis: Long) : Parcelable {
    constructor() : this(0, 0)
}

inline val TrimRange.startPointSecs
    get() = startPointMillis / 1000

inline val TrimRange.totalDurationSecs
    get() = totalDurationMillis / 1000