package com.paranid5.crescendo.data.properties

import com.paranid5.crescendo.data.StorageRepository

inline val StorageRepository.currentUrlFlow
    get() = streamStateDataSource.currentUrlFlow

suspend inline fun StorageRepository.storeCurrentUrl(url: String) =
    streamStateDataSource.storeCurrentUrl(url)

inline val StorageRepository.currentMetadataFlow
    get() = streamStateDataSource.currentMetadataFlow

suspend inline fun StorageRepository.storeCurrentMetadata(metadata: com.paranid5.crescendo.core.common.metadata.VideoMetadata?) =
    streamStateDataSource.storeCurrentMetadata(metadata)

