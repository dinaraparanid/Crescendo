package com.paranid5.crescendo.domain.caching

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CacheTrimRange(val startPointSecs: Long, val totalDurationSecs: Long) : Parcelable
