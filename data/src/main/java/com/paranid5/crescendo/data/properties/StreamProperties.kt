package com.paranid5.crescendo.data.properties

import com.paranid5.crescendo.core.common.metadata.VideoMetadata
import com.paranid5.crescendo.data.StorageRepository

val StorageRepository.currentUrlFlow
    get() = streamStateDataSource.currentUrlFlow

suspend fun StorageRepository.storeCurrentUrl(url: String) =
    streamStateDataSource.storeCurrentUrl(url)

val StorageRepository.currentMetadataFlow
    get() = streamStateDataSource.currentMetadataFlow

suspend fun StorageRepository.storeCurrentMetadata(metadata: VideoMetadata?) =
    streamStateDataSource.storeCurrentMetadata(metadata)

