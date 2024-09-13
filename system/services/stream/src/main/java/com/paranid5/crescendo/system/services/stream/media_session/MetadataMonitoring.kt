package com.paranid5.crescendo.system.services.stream.media_session

//internal suspend fun StreamService.startMetadataMonitoring() =
//    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
//        playerProvider
//            .currentMetadataFlow
//            .distinctUntilChanged()
//            .map { it?.toAndroidMetadata(context = this@startMetadataMonitoring) }
//            .collectLatest { metadata ->
//                metadata?.let(mediaSessionManager::updateMetadata)
//            }
//    }
//
//private suspend inline fun VideoMetadata.toAndroidMetadata(context: Context) = toAndroidMetadata(
//    getVideoCoverBitmapOrThumbnailAsync(context = context, videoCovers = covers).await()
//)
