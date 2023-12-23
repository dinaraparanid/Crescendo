package com.paranid5.crescendo.domain.trimming

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrimRange(val startPointSecs: Long, val totalDurationSecs: Long) : Parcelable {
    constructor() : this(0, 0)
}
