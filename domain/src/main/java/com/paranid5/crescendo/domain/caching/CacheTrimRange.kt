package com.paranid5.crescendo.domain.caching

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CacheTrimRange(val offset: Long, val endPoint: Long) : Parcelable
