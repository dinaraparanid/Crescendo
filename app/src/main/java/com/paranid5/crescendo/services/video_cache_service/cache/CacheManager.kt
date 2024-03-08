package com.paranid5.crescendo.services.video_cache_service.cache

import android.util.Log
import com.paranid5.crescendo.core.common.caching.CachingStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File

private const val TAG = "CacheManager"

class CacheManager {
    private val _cachingStatusState by lazy {
        MutableStateFlow(com.paranid5.crescendo.core.common.caching.CachingStatus.NONE)
    }

    val cachingStatusState by lazy {
        _cachingStatusState.asStateFlow()
    }

    fun onConversionStarted() =
        _cachingStatusState.update { com.paranid5.crescendo.core.common.caching.CachingStatus.CONVERTING }

    fun onCanceledCurrent() =
        _cachingStatusState.update { com.paranid5.crescendo.core.common.caching.CachingStatus.CANCELED_CUR }

    fun onCanceledAll() =
        _cachingStatusState.update { com.paranid5.crescendo.core.common.caching.CachingStatus.CANCELED_ALL }

    fun onConverted() =
        _cachingStatusState.update { com.paranid5.crescendo.core.common.caching.CachingStatus.CONVERTED }

    fun onCachingError(vararg videoCacheFiles: File) {
        _cachingStatusState.update { com.paranid5.crescendo.core.common.caching.CachingStatus.ERR }
        videoCacheFiles.forEach { Log.d(TAG, "File is deleted ${it.delete()}") }
    }

    fun prepareForNewVideo() =
        _cachingStatusState.update { com.paranid5.crescendo.core.common.caching.CachingStatus.NONE }

    fun resetCachingStatus() =
        _cachingStatusState.update { it.afterReset }
}

private inline val com.paranid5.crescendo.core.common.caching.CachingStatus.afterReset
    get() = when (this) {
        com.paranid5.crescendo.core.common.caching.CachingStatus.CONVERTING -> this
        else -> com.paranid5.crescendo.core.common.caching.CachingStatus.NONE
    }