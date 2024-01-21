package com.paranid5.crescendo.services.video_cache_service

import android.content.Intent
import android.os.Build
import com.paranid5.crescendo.domain.caching.Formats
import com.paranid5.crescendo.domain.caching.VideoCacheData
import com.paranid5.crescendo.domain.trimming.TrimRange
import com.paranid5.crescendo.services.ConnectionManager
import com.paranid5.crescendo.services.SuspendService
import com.paranid5.crescendo.services.connect
import com.paranid5.crescendo.services.disconnect
import com.paranid5.crescendo.services.video_cache_service.cache.CacheManager
import com.paranid5.crescendo.services.video_cache_service.cache.cacheNewVideoAsync
import com.paranid5.crescendo.services.video_cache_service.cache.startCacheEventLoop
import com.paranid5.crescendo.services.video_cache_service.extractor.UrlExtractor
import com.paranid5.crescendo.services.video_cache_service.files.MediaFileDownloader
import com.paranid5.crescendo.services.video_cache_service.files.VideoQueueManager
import com.paranid5.crescendo.services.video_cache_service.notification.NotificationManager
import com.paranid5.crescendo.services.video_cache_service.notification.startNotificationMonitoring
import com.paranid5.crescendo.services.video_cache_service.receivers.CacheNextVideoReceiver
import com.paranid5.crescendo.services.video_cache_service.receivers.CancelAllReceiver
import com.paranid5.crescendo.services.video_cache_service.receivers.CancelCurrentVideoReceiver
import com.paranid5.crescendo.services.video_cache_service.receivers.registerReceivers
import com.paranid5.crescendo.services.video_cache_service.receivers.unregisterReceivers
import kotlinx.coroutines.launch

class VideoCacheService : SuspendService(),
    ConnectionManager by ConnectionManagerImpl() {
    companion object {
        private const val SERVICE_LOCATION = "com.paranid5.crescendo.services.video_cache_service"
        const val Broadcast_CACHE_NEXT_VIDEO = "$SERVICE_LOCATION.CACHE_NEXT_VIDEO"
        const val Broadcast_CANCEL_CUR_VIDEO = "$SERVICE_LOCATION.CANCEL_CUR_VIDEO"
        const val Broadcast_CANCEL_ALL = "$SERVICE_LOCATION.CANCEL_ALL"

        const val URL_ARG = "url"
        const val FILENAME_ARG = "filename"
        const val FORMAT_ARG = "format"
        const val TRIM_RANGE_ARG = "trim_range"
    }

    val videoQueueManager by lazy {
        VideoQueueManager()
    }

    val notificationManager by lazy {
        NotificationManager(this)
    }

    val cacheManager by lazy {
        CacheManager()
    }

    val urlExtractor by lazy {
        UrlExtractor()
    }

    val mediaFileDownloader by lazy {
        MediaFileDownloader()
    }

    val cacheNextVideoReceiver = CacheNextVideoReceiver(this)
    val cancelCurrentVideoReceiver = CancelCurrentVideoReceiver(this)
    val cancelAllReceiver = CancelAllReceiver(this)

    override fun onCreate() {
        super.onCreate()
        registerReceivers()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        connect(startId)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            notificationManager.createChannel()

        launchMonitoringTasks()

        val videoData = intent!!.videoCacheDataArg
        cacheNewVideoAsync(videoData)

        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        disconnect()
        unregisterReceivers()
    }
}

private fun VideoCacheService.launchMonitoringTasks() {
    serviceScope.launch { startCacheEventLoop() }
    serviceScope.launch { startNotificationMonitoring() }
}

internal inline val Intent.videoCacheDataArg
    get() = VideoCacheData(
        url = getStringExtra(VideoCacheService.URL_ARG)!!,
        desiredFilename = getStringExtra(VideoCacheService.FILENAME_ARG)!!,
        format = formatArg,
        trimRange = trimRangeArg
    )

@Suppress("DEPRECATION")
private inline val Intent.formatArg
    get() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
            getSerializableExtra(VideoCacheService.FORMAT_ARG, Formats::class.java)!!

        else -> getSerializableExtra(VideoCacheService.FORMAT_ARG) as Formats
    }

@Suppress("DEPRECATION")
private inline val Intent.trimRangeArg
    get() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
            getParcelableExtra(VideoCacheService.TRIM_RANGE_ARG, TrimRange::class.java)!!

        else -> getParcelableExtra(VideoCacheService.TRIM_RANGE_ARG)!!
    }