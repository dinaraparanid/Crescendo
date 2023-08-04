package com.paranid5.crescendo.domain.services.video_cash_service

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface VideoCashResponse : Parcelable {

    @Parcelize
    data object Success : VideoCashResponse

    @Parcelize
    data class Error(
        @JvmField val httpCode: Int,
        @JvmField val description: String
    ) : VideoCashResponse

    @Parcelize
    data object Canceled : VideoCashResponse

    @Parcelize
    data object AudioConversionError : VideoCashResponse

    @Parcelize
    data object FileCreationError : VideoCashResponse

    @Parcelize
    data object ConnectionLostError : VideoCashResponse
}
