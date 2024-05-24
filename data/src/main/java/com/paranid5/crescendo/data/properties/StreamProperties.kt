package com.paranid5.crescendo.data.properties

import com.paranid5.crescendo.core.common.metadata.VideoMetadata
import com.paranid5.crescendo.data.StorageRepository

val StorageRepository.playingUrlFlow
    get() = streamStateDataSource.playingUrlFlow

suspend fun StorageRepository.storePlayingUrl(url: String) =
    streamStateDataSource.storePlayingUrl(url)

val StorageRepository.downloadingUrlFlow
    get() = streamStateDataSource.downloadingUrlFlow

suspend fun StorageRepository.storeDownloadingUrl(url: String) =
    streamStateDataSource.storeDownloadingUrl(url)

val StorageRepository.currentMetadataFlow
    get() = streamStateDataSource.currentMetadataFlow

suspend fun StorageRepository.storeCurrentMetadata(metadata: VideoMetadata?) =
    streamStateDataSource.storeCurrentMetadata(metadata)

