package com.paranid5.crescendo.domain.trimming

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class FadeDurations(val fadeInSecs: Long, val fadeOutSecs: Long) : Parcelable {
    constructor() : this(0, 0)
}
