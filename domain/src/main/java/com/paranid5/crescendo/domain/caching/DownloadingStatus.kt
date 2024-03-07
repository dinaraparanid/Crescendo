package com.paranid5.crescendo.domain.caching

enum class DownloadingStatus {
    DOWNLOADING,
    DOWNLOADED,
    CANCELED_CUR,
    CANCELED_ALL,
    ERR,
    CONNECT_LOST,
    NONE
}

inline val DownloadingStatus.isCanceled
    get() = when (this) {
        DownloadingStatus.CANCELED_CUR -> true
        DownloadingStatus.CANCELED_ALL -> true
        else -> false
    }