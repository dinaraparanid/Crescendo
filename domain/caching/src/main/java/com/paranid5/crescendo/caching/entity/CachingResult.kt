package com.paranid5.crescendo.caching.entity

import android.os.Parcelable
import com.paranid5.crescendo.domain.files.entity.MediaFile
import kotlinx.parcelize.Parcelize

sealed interface CachingResult : Parcelable {

    sealed interface DownloadResult : CachingResult {

        @Parcelize
        data class Success(val file: MediaFile) : DownloadResult

        @Parcelize
        data object Error : DownloadResult

        @Parcelize
        data object FileCreationError : DownloadResult

        @Parcelize
        data object ConnectionLostError : DownloadResult
    }

    @Parcelize
    data object Canceled : DownloadResult

    @Parcelize
    data class Success(val file: MediaFile) : CachingResult

    @Parcelize
    data object ConversionError : CachingResult
}

inline val CachingResult.isError
    get() = when (this) {
        is CachingResult.ConversionError,
        is CachingResult.DownloadResult.ConnectionLostError,
        is CachingResult.DownloadResult.Error,
        is CachingResult.DownloadResult.FileCreationError -> true

        is CachingResult.Canceled,
        is CachingResult.DownloadResult.Success,
        is CachingResult.Success -> false
    }

inline fun CachingResult.onCanceled(block: () -> Unit) =
    apply { if (this is CachingResult.Canceled) block() }
