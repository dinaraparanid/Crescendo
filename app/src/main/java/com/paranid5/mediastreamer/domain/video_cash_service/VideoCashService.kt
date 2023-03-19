package com.paranid5.mediastreamer.domain.video_cash_service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import arrow.core.Either
import arrow.core.merge
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.data.VideoMetadata
import com.paranid5.mediastreamer.domain.YoutubeUrlExtractor
import com.paranid5.mediastreamer.domain.downloadFile
import com.paranid5.mediastreamer.domain.getFileExt
import com.paranid5.mediastreamer.domain.media_scanner.MediaScannerReceiver
import com.paranid5.mediastreamer.domain.media_scanner.scanNextFile
import com.paranid5.mediastreamer.domain.utils.AsyncCondVar
import com.paranid5.mediastreamer.presentation.MainActivity
import com.paranid5.mediastreamer.presentation.streaming.Broadcast_VIDEO_CASH_COMPLETED
import com.paranid5.mediastreamer.presentation.streaming.VIDEO_CASH_STATUS
import com.paranid5.mediastreamer.utils.extensions.insertMediaFileToMediaStore
import com.paranid5.mediastreamer.utils.extensions.registerReceiverCompat
import com.paranid5.mediastreamer.utils.extensions.setAudioTagsToFileCatching
import io.ktor.client.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue

class VideoCashService : Service(), CoroutineScope by MainScope(), KoinComponent {
    companion object {
        private const val NOTIFICATION_ID = 102
        private const val VIDEO_CASH_CHANNEL_ID = "video_cash_channel"

        private const val SERVICE_LOCATION = "com.paranid5.mediastreamer.video_cash_service"
        const val Broadcast_CASH_NEXT_VIDEO = "$SERVICE_LOCATION.CASH_NEXT_VIDEO"
        const val Broadcast_CANCEL_CUR_VIDEO = "$SERVICE_LOCATION.CANCEL_CUR_VIDEO"
        const val Broadcast_CANCEL_ALL = "$SERVICE_LOCATION.CANCEL_ALL"

        const val URL_ARG = "url"
        const val FILENAME_ARG = "filename"
        const val SAVE_AS_VIDEO_ARG = "save_as_video"

        private val TAG = VideoCashService::class.simpleName!!
        private const val NEXT_VIDEO_AWAIT_TIMEOUT_MS = 3000L

        internal inline val Intent.mVideoCashDataArg
            get() = VideoCashData(
                url = getStringExtra(URL_ARG)!!,
                desiredFilename = getStringExtra(FILENAME_ARG)!!,
                isSaveAsVideo = getBooleanExtra(SAVE_AS_VIDEO_ARG, true)
            )
    }

    sealed class Actions(val requestCode: Int, val playbackAction: String) {
        object CancelCurVideo : Actions(
            requestCode = NOTIFICATION_ID + 1,
            playbackAction = Broadcast_CANCEL_CUR_VIDEO
        )

        object CancelAll : Actions(
            requestCode = NOTIFICATION_ID + 2,
            playbackAction = Broadcast_CANCEL_ALL
        )
    }

    private inline val Actions.playbackIntent: PendingIntent
        get() = PendingIntent.getBroadcast(
            this@VideoCashService,
            requestCode,
            Intent(playbackAction),
            PendingIntent.FLAG_MUTABLE
        )

    internal data class VideoCashData(
        val url: String,
        val desiredFilename: String,
        val isSaveAsVideo: Boolean
    )

    private val binder = object : Binder() {}
    private val ktorClient by inject<HttpClient>()

    private val videoCashQueue: Queue<VideoCashData> = ConcurrentLinkedQueue()
    private val videoCashQueueLenState = MutableStateFlow(0)

    private val videoCashCompletionChannel = Channel<HttpStatusCode>()
    private val videoCashProgressState = MutableStateFlow(0L to 0L)
    private val videoCashCondVar = AsyncCondVar()

    private val curVideoCashJobState = MutableStateFlow<Deferred<HttpStatusCode>?>(null)
    private val curVideoCashFileState = MutableStateFlow<File?>(null)
    private val curVideoMetadataState = MutableStateFlow<VideoMetadata?>(null)

    @Volatile
    private var wasStartForegroundUsed = false

    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    private inline val res
        get() = applicationContext.resources

    private val cashNextVideoReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            Log.d(TAG, "New video added to queue")
            mOfferVideoToQueue(videoCashData = intent.mVideoCashDataArg)
        }
    }

    private val cancelCurVideoReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Canceling video cashing")
            mCancelCurVideoCashing()
        }
    }

    private val cancelAllReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Canceling all videos cashing")
            mCancelAllVideosCashing()
        }
    }

    private val mediaScannerReceiver = MediaScannerReceiver()

    private fun registerReceivers() {
        registerReceiverCompat(cashNextVideoReceiver, Broadcast_CASH_NEXT_VIDEO)
        registerReceiverCompat(cancelCurVideoReceiver, Broadcast_CANCEL_CUR_VIDEO)
        registerReceiverCompat(cancelAllReceiver, Broadcast_CANCEL_ALL)
        registerReceiverCompat(mediaScannerReceiver, MediaScannerReceiver.Broadcast_SCAN_NEXT_FILE)
    }

    override fun onCreate() {
        super.onCreate()
        registerReceivers()
    }

    override fun onBind(intent: Intent?) = binder

    internal fun mOfferVideoToQueue(videoCashData: VideoCashData) {
        videoCashQueue.offer(videoCashData)
        videoCashQueueLenState.update { videoCashQueue.size }
    }

    private data class CashingNotificationData(
        val metadata: VideoMetadata,
        val videoCashQueueLen: Int,
        val downloadedBytes: Long,
        val totalBytes: Long,
    )

    private suspend inline fun startNotificationObserving(): Unit = combine(
        curVideoMetadataState,
        videoCashQueueLenState,
        videoCashProgressState
    ) { videoMetadata, videoCashQueueLen, (downloadedBytes, totalBytes) ->
        CashingNotificationData(
            videoMetadata ?: VideoMetadata(),
            videoCashQueueLen,
            downloadedBytes,
            totalBytes
        )
    }.collectLatest { (videoMetadata, videoCashQueueLen, downloadedBytes, totalBytes) ->
        updateNotification(
            isCashing = when (videoCashQueueLen) {
                0 -> false
                else -> true
            },
            videoMetadata,
            videoCashQueueLen,
            downloadedBytes,
            totalBytes
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel()

        mOfferVideoToQueue(videoCashData = intent!!.mVideoCashDataArg)

        launch(Dispatchers.IO) { launchCashing() }
        launch { startNotificationObserving() }
        return START_REDELIVER_INTENT
    }

    // --------------------- File Cashing ---------------------

    private fun getFullMediaDirectory(mediaDirectory: String) =
        Environment
            .getExternalStoragePublicDirectory(mediaDirectory)
            .absolutePath

    private fun createMediaFile(filename: String, ext: String) =
        "${getFullMediaDirectory(Environment.DIRECTORY_MOVIES)}/$filename.$ext"
            .takeIf { !File(it).exists() }
            ?.let(::File)
            ?.also { Log.d(TAG, "Creating file ${it.absolutePath}") }
            ?.also(File::createNewFile)
            ?: generateSequence(1) { it + 1 }
                .map { num -> "${getFullMediaDirectory(Environment.DIRECTORY_MOVIES)}/${filename}($num).$ext" }
                .map(::File)
                .first { !it.exists() }
                .also(File::createNewFile)

    private suspend inline fun setTags(mediaFile: File, videoMetadata: VideoMetadata) {
        val externalContentUri = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            else ->
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        val mediaDirectory = Environment.DIRECTORY_MOVIES
        val mimeType = "video/${mediaFile.extension}"
        val absoluteFilePath = mediaFile.absolutePath

        withContext(Dispatchers.IO) {
            insertMediaFileToMediaStore(
                externalContentUri,
                absoluteFilePath,
                mediaDirectory,
                videoMetadata,
                mimeType
            )

            setAudioTagsToFileCatching(mediaFile, videoMetadata)
            scanNextFile(absoluteFilePath)
        }
    }

    private suspend inline fun cashMediaFile(
        desiredFilename: String,
        audioOrVideoUrl: Either<String, String>,
        videoMetadata: VideoMetadata
    ): HttpStatusCode {
        val mediaUrl = audioOrVideoUrl.merge()
        val fileExt = ktorClient.getFileExt(mediaUrl)
        val storeFile = createMediaFile(desiredFilename, fileExt)
        curVideoCashFileState.update { storeFile }

        val statusCode = ktorClient.downloadFile(
            fileUrl = mediaUrl,
            storeFile = storeFile,
            progressState = videoCashProgressState
        )

        setTags(
            mediaFile = storeFile,
            videoMetadata = videoMetadata
        )

        return statusCode
    }

    private fun YoutubeUrlExtractor(desiredFilename: String, isSaveAsVideo: Boolean) =
        YoutubeUrlExtractor(
            context = applicationContext,
            videoExtractionChannel = videoCashCompletionChannel
        ) { audioUrl, videoUrl, videoMeta ->
            val videoMetadata = videoMeta?.let(::VideoMetadata) ?: VideoMetadata()

            val audioOrVideoUrl = when {
                isSaveAsVideo -> Either.Right(videoUrl)
                else -> Either.Left(audioUrl)
            }

            curVideoMetadataState.update { videoMetadata }
            cashMediaFile(desiredFilename, audioOrVideoUrl, videoMetadata)
        }

    private suspend inline fun launchExtractionAndCashingFile(
        url: String,
        desiredFilename: String,
        isSaveAsVideo: Boolean
    ): HttpStatusCode = coroutineScope {
        launch(Dispatchers.IO) { YoutubeUrlExtractor(desiredFilename, isSaveAsVideo).extract(url) }
        videoCashCompletionChannel.receive()
    }

    private fun clearVideoCashStates() {
        curVideoCashJobState.update { null }
        curVideoCashFileState.update { null }
    }

    private fun onVideoCashStatusReceived(statusCode: HttpStatusCode) {
        clearVideoCashStates()

        when {
            statusCode.isSuccess() -> onVideoCashStatusSuccessful()
            else -> onVideoCashStatusError(statusCode.value, statusCode.description)
        }
    }

    private fun onVideoCashStatusSuccessful() = sendBroadcast(
        Intent(Broadcast_VIDEO_CASH_COMPLETED)
            .putExtra(VIDEO_CASH_STATUS, VideoCashResponse.Success)
    )

    private fun onVideoCashStatusError(code: Int, description: String) = sendBroadcast(
        Intent(Broadcast_VIDEO_CASH_COMPLETED)
            .putExtra(VIDEO_CASH_STATUS, VideoCashResponse.Error(code, description))
    )

    private suspend inline fun launchCashing() {
        while (true) {
            if (videoCashQueue.isEmpty())
                if (videoCashCondVar.wait(NEXT_VIDEO_AWAIT_TIMEOUT_MS).isFailure)
                    break

            videoCashQueue.poll()?.let { (url, desiredFilename, isSaveAsVideo) ->
                onVideoCashStatusReceived(
                    statusCode = curVideoCashJobState.updateAndGet {
                        coroutineScope {
                            async {
                                launchExtractionAndCashingFile(url, desiredFilename, isSaveAsVideo)
                            }
                        }
                    }!!.await()
                )

                videoCashQueueLenState.update { videoCashQueue.size }
            }
        }

        removeNotification()
        stopSelf()
    }

    internal fun mCancelCurVideoCashing() {
        curVideoCashJobState.value?.cancel()
        curVideoCashFileState.value?.delete() // TODO: Delete Permission
        clearVideoCashStates()
    }

    internal fun mCancelAllVideosCashing() {
        videoCashQueue.clear()
        videoCashQueueLenState.update { 0 }
        mCancelCurVideoCashing()
    }

    private fun unregisterReceivers() {
        unregisterReceiver(cashNextVideoReceiver)
        unregisterReceiver(cancelCurVideoReceiver)
        unregisterReceiver(cancelAllReceiver)
        unregisterReceiver(mediaScannerReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceivers()
    }

    // --------------------- Notifications ---------------------

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() = notificationManager.createNotificationChannel(
        NotificationChannel(
            VIDEO_CASH_CHANNEL_ID,
            "Video Cash",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setSound(null, null)
            enableVibration(false)
            enableLights(true)
        }
    )

    private inline val notificationBuilder
        get() = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->
                Notification.Builder(applicationContext, VIDEO_CASH_CHANNEL_ID)
            else ->
                Notification.Builder(applicationContext)
        }
            .setSmallIcon(R.drawable.save_icon)
            .setContentIntent(
                PendingIntent.getActivity(
                    applicationContext,
                    0,
                    Intent(applicationContext, MainActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )

    private fun getCashingNotificationBuilder(
        videoMetadata: VideoMetadata,
        videoCashQueueLen: Int,
        downloadedBytes: Long,
        totalBytes: Long
    ) = notificationBuilder
        .setContentTitle("${res.getString(R.string.downloading)}: ${videoMetadata.title}")
        .extend { notificationBuilder ->
            notificationBuilder.setContentText(
                "${res.getString(R.string.tracks_in_queue)}: $videoCashQueueLen"
            )
        }
        .setProgress(totalBytes.toInt(), downloadedBytes.toInt(), false)
        .setOngoing(true)
        .setShowWhen(false)
        .setOnlyAlertOnce(true)
        .addAction(cancelCurVideoAction)
        .addAction(cancelAllAction)

    private inline val cancelCurVideoAction
        get() = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> Notification.Action.Builder(
                null,
                res.getString(R.string.cancel),
                Actions.CancelCurVideo.playbackIntent
            )

            else -> Notification.Action.Builder(
                0,
                res.getString(R.string.cancel),
                Actions.CancelCurVideo.playbackIntent
            )
        }.build()

    private inline val cancelAllAction
        get() = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> Notification.Action.Builder(
                null,
                res.getString(R.string.cancel_all),
                Actions.CancelAll.playbackIntent
            )

            else -> Notification.Action.Builder(
                0,
                res.getString(R.string.cancel_all),
                Actions.CancelAll.playbackIntent
            )
        }.build()

    private inline val finishedNotificationBuilder
        get() = notificationBuilder
            .setContentTitle(res.getString(R.string.video_cashed))
            .setOngoing(false)
            .setShowWhen(false)

    private fun startCashingNotification(
        videoMetadata: VideoMetadata,
        videoCashQueueLen: Int,
        downloadedBytes: Long,
        totalBytes: Long
    ) {
        wasStartForegroundUsed = true

        startForeground(
            NOTIFICATION_ID,
            getCashingNotificationBuilder(
                videoMetadata,
                videoCashQueueLen,
                downloadedBytes,
                totalBytes
            ).build()
        )
    }

    private fun updateCashingNotification(
        videoMetadata: VideoMetadata,
        videoCashQueueLen: Int,
        downloadedBytes: Long,
        totalBytes: Long
    ) = notificationManager.notify(
        NOTIFICATION_ID,
        getCashingNotificationBuilder(
            videoMetadata,
            videoCashQueueLen,
            downloadedBytes,
            totalBytes
        ).build()
    )

    private fun showFinishedNotification() = notificationManager.notify(
        NOTIFICATION_ID,
        finishedNotificationBuilder.build()
    )

    private fun updateNotification(
        isCashing: Boolean,
        videoMetadata: VideoMetadata,
        videoCashQueueLen: Int,
        downloadedBytes: Long,
        totalBytes: Long
    ) = when {
        !wasStartForegroundUsed -> startCashingNotification(
            videoMetadata,
            videoCashQueueLen,
            downloadedBytes,
            totalBytes
        )

        isCashing -> updateCashingNotification(
            videoMetadata,
            videoCashQueueLen,
            downloadedBytes,
            totalBytes
        )

        else -> showFinishedNotification()
    }

    private fun removeNotification() {
        notificationManager.cancel(NOTIFICATION_ID)

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ->
                stopForeground(STOP_FOREGROUND_DETACH)
            else ->
                stopForeground(true)
        }
    }
}