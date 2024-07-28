package com.paranid5.crescendo.data.properties

import com.paranid5.crescendo.core.common.metadata.VideoMetadata
import com.paranid5.crescendo.data.datastore.DataStoreProvider

val DataStoreProvider.playingUrlFlow
    get() = streamStateDataSource.playingUrlFlow

suspend fun DataStoreProvider.storePlayingUrl(url: String) =
    streamStateDataSource.storePlayingUrl(url)

val DataStoreProvider.downloadingUrlFlow
    get() = streamStateDataSource.downloadingUrlFlow

suspend fun DataStoreProvider.storeDownloadingUrl(url: String) =
    streamStateDataSource.storeDownloadingUrl(url)

val DataStoreProvider.currentMetadataFlow
    get() = streamStateDataSource.currentMetadataFlow

suspend fun DataStoreProvider.storeCurrentMetadata(metadata: VideoMetadata?) =
    streamStateDataSource.storeCurrentMetadata(metadata)

