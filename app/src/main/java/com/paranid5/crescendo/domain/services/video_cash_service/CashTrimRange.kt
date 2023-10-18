package com.paranid5.crescendo.domain.services.video_cash_service

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CashTrimRange(val offset: Long, val endPoint: Long) : Parcelable
