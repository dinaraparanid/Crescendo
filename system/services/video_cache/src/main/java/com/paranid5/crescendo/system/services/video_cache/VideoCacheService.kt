package com.paranid5.crescendo.system.services.video_cache

import android.content.Intent
import android.os.Build
import com.paranid5.crescendo.caching.entity.VideoCacheData
import com.paranid5.crescendo.core.impl.trimmer.TrimRangeModel
import com.paranid5.crescendo.core.impl.trimmer.toEntity
import com.paranid5.crescendo.domain.files.entity.Formats
import com.paranid5.crescendo.domain.tags.ConvertToAudioFileAndSetTagsUseCase
import com.paranid5.crescendo.domain.tags.TagsRepository
import com.paranid5.crescendo.system.common.broadcast.VideoCacheServiceBroadcasts
import com.paranid5.crescendo.system.services.video_cache.cache.CacheManager
import com.paranid5.crescendo.system.services.video_cache.cache.cacheNewVideoAsync
import com.paranid5.crescendo.system.services.video_cache.cache.startCacheEventLoop
import com.paranid5.crescendo.system.services.video_cache.extractor.UrlExtractor
import com.paranid5.crescendo.system.services.video_cache.files.InitMediaFileUseCase
import com.paranid5.crescendo.system.services.video_cache.files.MediaFileDownloader
import com.paranid5.crescendo.system.services.video_cache.files.VideoQueueManager
import com.paranid5.crescendo.system.services.video_cache.notification.NotificationManager
import com.paranid5.crescendo.system.services.video_cache.notification.startNotificationMonitoring
import com.paranid5.crescendo.system.services.video_cache.receivers.CacheNextVideoReceiver
import com.paranid5.crescendo.system.services.video_cache.receivers.CancelAllReceiver
import com.paranid5.crescendo.system.services.video_cache.receivers.CancelCurrentVideoReceiver
import com.paranid5.crescendo.system.services.video_cache.receivers.registerReceivers
import com.paranid5.crescendo.system.services.video_cache.receivers.unregisterReceivers
import com.paranid5.crescendo.utils.extensions.notNull
import com.paranid5.system.services.common.ConnectionManager
import com.paranid5.system.services.common.PlaybackForegroundService
import com.paranid5.system.services.common.SuspendService
import com.paranid5.system.services.common.connect
import com.paranid5.system.services.common.disconnect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class VideoCacheService : SuspendService(),
    PlaybackForegroundService,
    ConnectionManager by ConnectionManagerImpl() {

    internal val videoQueueManager by inject<VideoQueueManager>()
    internal val notificationManager by inject<NotificationManager> { parametersOf(this) }
    internal val cacheManager by inject<CacheManager>()
    internal val urlExtractor by inject<UrlExtractor>()
    internal val mediaFileDownloader by inject<MediaFileDownloader>()
    internal val tagsRepository by inject<TagsRepository>()
    internal val initMediaFileUseCase by inject<InitMediaFileUseCase>()
    internal val convertToAudioFileAndSetTagsUseCase by inject<ConvertToAudioFileAndSetTagsUseCase>()

    internal val cacheNextVideoReceiver = CacheNextVideoReceiver(this)
    internal val cancelCurrentVideoReceiver = CancelCurrentVideoReceiver(this)
    internal val cancelAllReceiver = CancelAllReceiver(this)

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

        val videoData = requireNotNull(intent?.videoCacheDataArg)
        cacheNewVideoAsync(videoData)

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        disconnect()
        unregisterReceivers()
    }

    private fun launchMonitoringTasks() {
        serviceScope.launch { startCacheEventLoop() }
        serviceScope.launch { startNotificationMonitoring() }
    }
}

internal inline val Intent.videoCacheDataArg
    get() = VideoCacheData(
        url = getStringExtra(VideoCacheServiceBroadcasts.URL_ARG).notNull,
        desiredFilename = getStringExtra(VideoCacheServiceBroadcasts.FILENAME_ARG).notNull,
        format = formatArg,
        trimRange = trimRangeArg.toEntity(),
    )

@Suppress("DEPRECATION")
internal inline val Intent.formatArg
    get() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(
            VideoCacheServiceBroadcasts.FORMAT_ARG,
            Formats::class.java
        )

        else -> getSerializableExtra(VideoCacheServiceBroadcasts.FORMAT_ARG) as Formats
    }.notNull

@Suppress("DEPRECATION")
internal inline val Intent.trimRangeArg
    get() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelableExtra(
            VideoCacheServiceBroadcasts.TRIM_RANGE_ARG,
            TrimRangeModel::class.java,
        )

        else -> getParcelableExtra(VideoCacheServiceBroadcasts.TRIM_RANGE_ARG)
    }.notNull
