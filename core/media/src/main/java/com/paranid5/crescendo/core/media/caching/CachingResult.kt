package com.paranid5.crescendo.core.media.caching

import android.os.Parcelable
import com.paranid5.crescendo.core.media.files.MediaFile
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
        CachingResult.ConversionError -> true
        CachingResult.Canceled -> false
        CachingResult.DownloadResult.ConnectionLostError -> true
        is CachingResult.DownloadResult.Error -> true
        CachingResult.DownloadResult.FileCreationError -> true
        is CachingResult.DownloadResult.Success -> false
        is CachingResult.Success -> false
    }

inline val CachingResult.isNotError
    get() = isError.not()

inline fun CachingResult.onCanceled(block: () -> Unit) =
    apply { if (this is CachingResult.Canceled) block() }