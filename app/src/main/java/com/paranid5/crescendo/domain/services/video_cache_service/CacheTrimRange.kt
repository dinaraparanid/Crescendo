package com.paranid5.crescendo.domain.services.video_cache_service

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CacheTrimRange(val offset: Long, val endPoint: Long) : Parcelable
