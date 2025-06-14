package com.paranid5.crescendo.system.services.video_cache.cache

import com.paranid5.crescendo.core.common.caching.CachingStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class CacheManager {
    private val _cachingStatusState = MutableStateFlow(CachingStatus.NONE)

    val cachingStatusState = _cachingStatusState.asStateFlow()

    fun onConversionStarted() = _cachingStatusState.update { CachingStatus.CONVERTING }

    fun onCanceledCurrent() = _cachingStatusState.update { CachingStatus.CANCELED_CUR }

    fun onCanceledAll() = _cachingStatusState.update { CachingStatus.CANCELED_ALL }

    fun onConverted() = _cachingStatusState.update { CachingStatus.CONVERTED }

    fun prepareForNewVideo() = _cachingStatusState.update { CachingStatus.NONE }

    fun resetCachingStatus() = _cachingStatusState.update { status ->
        when (status) {
            CachingStatus.CONVERTING -> status
            else -> CachingStatus.NONE
        }
    }
}
