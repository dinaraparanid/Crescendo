package com.paranid5.crescendo.system.services.video_cache.cache

import com.paranid5.crescendo.caching.entity.VideoCacheData
import com.paranid5.crescendo.system.services.video_cache.VideoCacheService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val EVENT_LOOP_LAZINESS_DELAY = 500L

internal fun VideoCacheService.cacheNewVideoAsync(videoCacheData: VideoCacheData) =
    serviceScope.launch {
        delay(EVENT_LOOP_LAZINESS_DELAY)
        videoQueueManager.offerNewVideo(videoCacheData)
    }
