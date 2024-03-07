package com.paranid5.crescendo.domain.caching

import io.ktor.http.HttpStatusCode

sealed interface DownloadingStatus {
    data object Downloading : DownloadingStatus

    data object Downloaded : DownloadingStatus

    data object CanceledCurrent : DownloadingStatus

    data object CanceledAll : DownloadingStatus

    data class Error(val status: HttpStatusCode) : DownloadingStatus

    data object ConnectionLost : DownloadingStatus

    data object None : DownloadingStatus
}

inline val DownloadingStatus.isCanceled
    get() = when (this) {
        DownloadingStatus.CanceledCurrent -> true
        DownloadingStatus.CanceledAll -> true
        else -> false
    }