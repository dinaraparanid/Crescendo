package com.paranid5.crescendo.system.services.video_cache.cache

import com.paranid5.crescendo.core.common.caching.VideoCacheData
import com.paranid5.crescendo.system.services.video_cache.VideoCacheService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal fun VideoCacheService.cacheNewVideoAsync(videoCacheData: VideoCacheData) =
    serviceScope.launch {
        delay(500) // for an event loop to overcome laziness
        videoQueueManager.offerNewVideo(videoCacheData)
    }