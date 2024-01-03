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