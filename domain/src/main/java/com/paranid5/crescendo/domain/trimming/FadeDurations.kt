package com.paranid5.crescendo.domain.trimming

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FadeDurations(val fadeInSecs: Long, val fadeOutSecs: Long) : Parcelable {
    constructor() : this(0, 0)
}
