package com.paranid5.crescendo.core.common.caching

sealed interface DownloadingStatus {
    data object Downloading : DownloadingStatus

    data object Downloaded : DownloadingStatus

    data object CanceledCurrent : DownloadingStatus

    data object CanceledAll : DownloadingStatus

    data object Error : DownloadingStatus

    data object ConnectionLost : DownloadingStatus

    data object None : DownloadingStatus
}

val DownloadingStatus.isCanceled
    get() = when (this) {
        DownloadingStatus.CanceledCurrent -> true
        DownloadingStatus.CanceledAll -> true
        else -> false
    }