package com.paranid5.crescendo.core.common.caching

sealed interface DownloadFilesStatus {
    data object Error : DownloadFilesStatus
    data object Canceled : DownloadFilesStatus
    data object Success : DownloadFilesStatus
}