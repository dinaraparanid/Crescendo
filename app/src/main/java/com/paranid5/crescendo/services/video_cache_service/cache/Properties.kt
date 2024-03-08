package com.paranid5.crescendo.services.video_cache_service.cache

import com.paranid5.crescendo.core.common.caching.VideoCacheData
import com.paranid5.crescendo.services.video_cache_service.VideoCacheService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun VideoCacheService.cacheNewVideoAsync(videoCacheData: com.paranid5.crescendo.core.common.caching.VideoCacheData) =
    serviceScope.launch {
        delay(500) // for an event loop to overcome laziness
        videoQueueManager.offerNewVideo(videoCacheData)
    }