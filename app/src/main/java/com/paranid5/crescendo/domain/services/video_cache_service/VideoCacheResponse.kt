package com.paranid5.crescendo.domain.services.video_cache_service

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface VideoCacheResponse : Parcelable {

    @Parcelize
    data object Success : VideoCacheResponse

    @Parcelize
    data class Error(
        @JvmField val httpCode: Int,
        @JvmField val description: String
    ) : VideoCacheResponse

    @Parcelize
    data object Canceled : VideoCacheResponse

    @Parcelize
    data object AudioConversionError : VideoCacheResponse

    @Parcelize
    data object FileCreationError : VideoCacheResponse

    @Parcelize
    data object ConnectionLostError : VideoCacheResponse
}
