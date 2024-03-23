package com.paranid5.crescendo.system.services.video_cache.cache

import android.util.Log
import com.paranid5.crescendo.core.common.caching.CachingStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File

private const val TAG = "CacheManager"

internal class CacheManager {
    private val _cachingStatusState by lazy {
        MutableStateFlow(CachingStatus.NONE)
    }

    val cachingStatusState by lazy {
        _cachingStatusState.asStateFlow()
    }

    fun onConversionStarted() =
        _cachingStatusState.update { CachingStatus.CONVERTING }

    fun onCanceledCurrent() =
        _cachingStatusState.update { CachingStatus.CANCELED_CUR }

    fun onCanceledAll() =
        _cachingStatusState.update { CachingStatus.CANCELED_ALL }

    fun onConverted() =
        _cachingStatusState.update { CachingStatus.CONVERTED }

    fun onCachingError(vararg videoCacheFiles: File) {
        _cachingStatusState.update { CachingStatus.ERR }
        videoCacheFiles.forEach { Log.d(TAG, "File is deleted ${it.delete()}") }
    }

    fun prepareForNewVideo() =
        _cachingStatusState.update { CachingStatus.NONE }

    fun resetCachingStatus() =
        _cachingStatusState.update { it.afterReset }
}

private inline val CachingStatus.afterReset
    get() = when (this) {
        CachingStatus.CONVERTING -> this
        else -> CachingStatus.NONE
    }