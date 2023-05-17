package com.paranid5.mediastreamer.domain.video_cash_service

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface VideoCashResponse : Parcelable {

    @Parcelize
    object Success : VideoCashResponse

    @Parcelize
    data class Error(
        @JvmField val httpCode: Int,
        @JvmField val description: String
    ) : VideoCashResponse

    @Parcelize
    object Canceled : VideoCashResponse

    @Parcelize
    object AudioConversionError : VideoCashResponse

    @Parcelize
    object FileCreationError : VideoCashResponse
}
