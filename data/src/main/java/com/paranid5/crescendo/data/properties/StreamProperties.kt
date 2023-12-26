package com.paranid5.crescendo.data.properties

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.domain.metadata.VideoMetadata

inline val StorageHandler.currentUrlFlow
    get() = streamStateProvider.currentUrlFlow

suspend inline fun StorageHandler.storeCurrentUrl(url: String) =
    streamStateProvider.storeCurrentUrl(url)

inline val StorageHandler.currentMetadataFlow
    get() = streamStateProvider.currentMetadataFlow

suspend inline fun StorageHandler.storeCurrentMetadata(metadata: VideoMetadata?) =
    streamStateProvider.storeCurrentMetadata(metadata)

