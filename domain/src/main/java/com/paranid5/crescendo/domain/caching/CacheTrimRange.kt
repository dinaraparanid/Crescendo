package com.paranid5.crescendo.domain.caching

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CacheTrimRange(val startPoint: Long, val endPoint: Long) : Parcelable
