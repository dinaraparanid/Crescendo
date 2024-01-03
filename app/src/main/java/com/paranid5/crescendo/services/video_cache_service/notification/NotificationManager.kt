package com.paranid5.crescendo.services.video_cache_service.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.paranid5.crescendo.R
import com.paranid5.crescendo.domain.caching.CachingStatus
import com.paranid5.crescendo.domain.caching.DownloadingStatus
import com.paranid5.crescendo.domain.metadata.VideoMetadata
import com.paranid5.crescendo.services.video_cache_service.VideoCacheService

internal const val VIDEO_CACHE_NOTIFICATION_ID = 103
internal const val VIDEO_CACHE_CHANNEL_ID = "video_cache_channel"

class NotificationManager(service: VideoCacheService) {
    private val manager by lazy {
        service.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel() = manager.createNotificationChannel(
        NotificationChannel(
            VIDEO_CACHE_CHANNEL_ID,
            "Video Cash",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setSound(null, null)
            enableVibration(false)
            enableLights(true)
        }
    )

    fun showNotification(
        service: VideoCacheService,
        downloadStatus: DownloadingStatus,
        cacheStatus: CachingStatus,
        videoMetadata: VideoMetadata,
        videoCashQueueLen: Int,
        downloadedBytes: Long,
        totalBytes: Long,
        errorCode: Int,
        errorDescription: String
    ) = when (downloadStatus) {
        DownloadingStatus.DOWNLOADING ->
            showDownloadNotification(
                service = service,
                videoTitle = service videoTitleOf videoMetadata,
                videoCashQueueLen = videoCashQueueLen,
                downloadedBytes = downloadedBytes,
                totalBytes = totalBytes
            )

        DownloadingStatus.CANCELED_CUR ->
            showCanceledNotification(service)

        DownloadingStatus.CANCELED_ALL ->
            showCanceledNotification(service)

        DownloadingStatus.ERR ->
            showDownloadErrorNotification(service, errorCode, errorDescription)

        DownloadingStatus.CONNECT_LOST ->
            showConnectionLostNotification(service)

        DownloadingStatus.NONE -> Unit

        DownloadingStatus.DOWNLOADED ->
            showCachingNotification(
                service = service,
                cacheStatus = cacheStatus,
                videoMetadata = videoMetadata,
            )
    }

    private fun showCachingNotification(
        service: VideoCacheService,
        cacheStatus: CachingStatus,
        videoMetadata: VideoMetadata,
    ) = when (cacheStatus) {
        CachingStatus.CONVERTING ->
            showConvertingNotification(
                context = service,
                videoTitle = service videoTitleOf videoMetadata
            )

        CachingStatus.CONVERTED ->
            showCachedNotification(service)

        CachingStatus.CANCELED_CUR ->
            showCanceledNotification(service)

        CachingStatus.CANCELED_ALL ->
            showCanceledNotification(service)

        else -> Unit
    }

    private fun showDownloadNotification(
        service: VideoCacheService,
        videoTitle: String,
        videoCashQueueLen: Int,
        downloadedBytes: Long,
        totalBytes: Long
    ) = service.startForeground(
        VIDEO_CACHE_NOTIFICATION_ID,
        DownloadNotificationBuilder(
            context = service,
            videoTitle = videoTitle,
            videoCashQueueLen = videoCashQueueLen,
            downloadedBytes = downloadedBytes,
            totalBytes = totalBytes
        ).build(),
    )

    private fun showCanceledNotification(context: Context) =
        manager.notify(
            VIDEO_CACHE_NOTIFICATION_ID,
            CanceledNotificationBuilder(context).build()
        )

    private fun showDownloadErrorNotification(context: Context, code: Int, description: String) =
        manager.notify(
            VIDEO_CACHE_NOTIFICATION_ID,
            DownloadErrorNotificationBuilder(context, code, description).build()
        )

    private fun showConnectionLostNotification(context: Context) =
        manager.notify(
            VIDEO_CACHE_NOTIFICATION_ID,
            ConnectionLostNotificationBuilder(context).build()
        )

    private fun showConvertingNotification(context: Context, videoTitle: String) =
        manager.notify(
            VIDEO_CACHE_NOTIFICATION_ID,
            ConvertingNotificationBuilder(context, videoTitle).build()
        )

    private fun showCachedNotification(context: Context) =
        manager.notify(
            VIDEO_CACHE_NOTIFICATION_ID,
            CachedNotificationBuilder(context).build()
        )
}

private infix fun Context.videoTitleOf(videoMetadata: VideoMetadata) =
    videoMetadata.title ?: getString(R.string.stream_no_name)