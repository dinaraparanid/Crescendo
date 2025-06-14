package com.paranid5.crescendo.core.media.caching

import android.os.Parcelable
import com.paranid5.crescendo.core.media.files.MediaFile
import kotlinx.parcelize.Parcelize

@Deprecated("Will be removed")
sealed interface CachingResult : Parcelable {

    @Deprecated("Will be removed")
    sealed interface DownloadResult : CachingResult {

        @Deprecated("Will be removed")
        @Parcelize
        data class Success(val file: MediaFile) : DownloadResult

        @Deprecated("Will be removed")
        @Parcelize
        data object Error : DownloadResult

        @Deprecated("Will be removed")
        @Parcelize
        data object FileCreationError : DownloadResult

        @Deprecated("Will be removed")
        @Parcelize
        data object ConnectionLostError : DownloadResult
    }

    @Deprecated("Will be removed")
    @Parcelize
    data object Canceled : DownloadResult

    @Deprecated("Will be removed")
    @Parcelize
    data class Success(val file: MediaFile) : CachingResult

    @Deprecated("Will be removed")
    @Parcelize
    data object ConversionError : CachingResult
}

@Deprecated("Will be removed")
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

@Deprecated("Will be removed")
inline val CachingResult.isNotError
    get() = isError.not()

@Deprecated("Will be removed")
inline fun CachingResult.onCanceled(block: () -> Unit) =
    apply { if (this is CachingResult.Canceled) block() }