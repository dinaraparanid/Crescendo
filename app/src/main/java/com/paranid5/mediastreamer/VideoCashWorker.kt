package com.paranid5.mediastreamer

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.work.*
import com.dinaraparanid.ytdlp_kt.YtDlp
import com.dinaraparanid.ytdlp_kt.YtDlpRequest
import com.dinaraparanid.ytdlp_kt.YtDlpRequestStatus
import com.paranid5.mediastreamer.presentation.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class VideoCashWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    companion object : KoinComponent {
        private const val URL_ARG = "url"
        private const val VIDEO_TITLE_ARG = "video_title"
        private const val SAVE_AS_VIDEO_ARG = "save_as_video"

        private const val NOTIFICATION_ID = 102
        private const val VIDEO_CASH_CHANNEL_ID = "video_cash_channel"

        private val TAG = VideoCashWorker::class.simpleName!!

        fun launch(url: String, videoTitle: String, saveAsVideo: Boolean) =
            WorkManager.getInstance(get<MainApplication>())
                .enqueue(
                    OneTimeWorkRequestBuilder<VideoCashWorker>()
                        .setInputData(
                            workDataOf(
                                URL_ARG to url,
                                VIDEO_TITLE_ARG to videoTitle,
                                SAVE_AS_VIDEO_ARG to saveAsVideo
                            )
                        )
                        .build()
                )
    }

    private lateinit var url: String
    private lateinit var videoTitle: String

    private fun YtDlpRequestStatus.respondToUser() = Toast.makeText(
        applicationContext,
        when (this) {
            is YtDlpRequestStatus.Error.GeoRestricted -> R.string.geo_restricted

            is YtDlpRequestStatus.Error.IncorrectUrl -> {
                Log.e(TAG, "Incorrect url while cashing")
                R.string.something_went_wrong
            }

            is YtDlpRequestStatus.Error.NoInternet -> R.string.no_internet

            is YtDlpRequestStatus.Error.StreamConversion -> {
                Log.e(TAG, "Stream conversion error")
                R.string.something_went_wrong
            }

            is YtDlpRequestStatus.Error.UnknownError -> {
                Log.e(TAG, "Unknown error")
                R.string.something_went_wrong
            }

            is YtDlpRequestStatus.Success<*> -> R.string.video_cashed
        },
        Toast.LENGTH_LONG
    ).show()

    private suspend fun cashFileAsync(isSaveAsVideo: Boolean) =
        YtDlp.executeAsync(
            request = YtDlpRequest(url).apply {
                if (isSaveAsVideo) setOption("--recode-video", "mp4")
                if (!isSaveAsVideo) setOption("--audio-format", "mp3")
                if (!isSaveAsVideo) setOption("--extract-audio")
                setOption("--socket-timeout", "1")
                setOption("--retries", "infinite")
                setOption("--audio-quality", "10")
                setOption("--format", "best")
            },
            isPythonExecutable = false
        ).await().respondToUser()

    override suspend fun doWork() = withContext(Dispatchers.IO) {
        url = inputData.getString(URL_ARG)!!
        videoTitle = inputData.getString(VIDEO_TITLE_ARG)!!
        showNotification(isCashing = true)
        cashFileAsync(isSaveAsVideo = inputData.getBoolean(SAVE_AS_VIDEO_ARG, false))
        showNotification(isCashing = false)
        Result.success()
    }

    private inline val notificationBuilder
        get() = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->
                Notification.Builder(applicationContext, VIDEO_CASH_CHANNEL_ID)
            else ->
                Notification.Builder(applicationContext)
        }
            .setSmallIcon(R.drawable.save_icon)
            .setAutoCancel(false)
            .setContentIntent(
                PendingIntent.getActivity(
                    applicationContext,
                    0,
                    Intent(applicationContext, MainActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )

    private inline val cashingNotification
        get() = notificationBuilder
            .setContentTitle(
                "${applicationContext.resources.getString(R.string.downloading)}: $videoTitle"
            )
            .setOngoing(true)
            .build()

    private inline val finishedNotification
        get() = notificationBuilder
            .setContentTitle(applicationContext.resources.getString(R.string.video_cashed))
            .setOngoing(false)
            .build()

    private fun buildNotification(isCashing: Boolean) = when {
        isCashing -> cashingNotification
        else -> finishedNotification
    }

    private suspend fun showNotification(isCashing: Boolean) =
        setForeground(ForegroundInfo(NOTIFICATION_ID, buildNotification(isCashing)))
}