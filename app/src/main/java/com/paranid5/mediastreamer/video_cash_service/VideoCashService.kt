package com.paranid5.mediastreamer.video_cash_service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import arrow.core.Either
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.YoutubeUrlExtractor
import com.paranid5.mediastreamer.data.VideoMetadata
import com.paranid5.mediastreamer.downloadFile
import com.paranid5.mediastreamer.presentation.MainActivity
import com.paranid5.mediastreamer.presentation.streaming.Broadcast_VIDEO_CASH_COMPLETED
import com.paranid5.mediastreamer.presentation.streaming.VIDEO_CASH_STATUS
import com.paranid5.mediastreamer.utils.AsyncCondVar
import com.paranid5.mediastreamer.utils.extensions.insertMediaFileToMediaStore
import com.paranid5.mediastreamer.utils.extensions.registerReceiverCompat
import com.paranid5.mediastreamer.utils.extensions.setAudioTagsToFile
import io.ktor.client.HttpClient
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.net.URI
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
        const val SAVE_AS_VIDEO_ARG = "save_as_video"

        private val TAG = VideoCashService::class.simpleName!!
        private const val NEXT_VIDEO_AWAIT_TIMEOUT_MS = 15000L

        internal inline val Intent.mUrlAndSaveAsVideoArgs
            get() = getStringExtra(URL_ARG)!! to getBooleanExtra(SAVE_AS_VIDEO_ARG, true)
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

    private val binder = object : Binder() {}
    private val ktorClient by inject<HttpClient>()

    private val videoCashQueue: Queue<Pair<String, Boolean>> = ConcurrentLinkedQueue()
    private val videoCashQueueLenState = MutableStateFlow(0)

    private val videoCashCompletionChannel = Channel<HttpStatusCode>()
    private val videoCashCondVar = AsyncCondVar()

    private val curVideoCashJobState = MutableStateFlow<Deferred<HttpStatusCode>?>(null)
    private val curVideoCashFileState = MutableStateFlow<File?>(null)
    private val curVideoMetadataState = MutableStateFlow<VideoMetadata?>(null)

    @Volatile
    private var isCashingNotificationShown = false

    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    private inline val resources
        get() = applicationContext.resources

    private val cashNextVideoReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            Log.d(TAG, "New video added to queue")
            mOfferVideoToQueue(titleToSaveAsVideo = intent.mUrlAndSaveAsVideoArgs)
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

    private fun registerReceivers() {
        registerReceiverCompat(cashNextVideoReceiver, Broadcast_CASH_NEXT_VIDEO)
        registerReceiverCompat(cancelCurVideoReceiver, Broadcast_CANCEL_CUR_VIDEO)
        registerReceiverCompat(cancelAllReceiver, Broadcast_CANCEL_ALL)
    }

    override fun onCreate() {
        super.onCreate()
        registerReceivers()
    }

    override fun onBind(intent: Intent?) = binder

    internal fun mOfferVideoToQueue(titleToSaveAsVideo: Pair<String, Boolean>) {
        videoCashQueue.offer(titleToSaveAsVideo)
        videoCashQueueLenState.update { videoCashQueue.size }
    }

    private fun pollVideoFromQueue(): Pair<String, Boolean>? {
        videoCashQueueLenState.update { videoCashQueue.size - 1 }
        return videoCashQueue.poll()
    }

    private suspend inline fun startNotificationObserving(): Unit = combine(
        curVideoMetadataState,
        videoCashQueueLenState
    ) { videoMetadata, videoCashQueueLen ->
        videoMetadata to videoCashQueueLen
    }.collectLatest { (videoMetadataOrNull, videoCashQueueLen) ->
        val videoMetadata = videoMetadataOrNull ?: VideoMetadata()

        when (videoCashQueueLen) {
            0 -> updateNotification(isCashing = false, videoMetadata, videoCashQueueLen)
            else -> updateNotification(isCashing = true, videoMetadata, videoCashQueueLen)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel()

        mOfferVideoToQueue(titleToSaveAsVideo = intent!!.mUrlAndSaveAsVideoArgs)

        launch { launchCashing() }
        launch { startNotificationObserving() }
        return START_REDELIVER_INTENT
    }

    // --------------------- File Cashing ---------------------

    private suspend inline fun createMediaFile(
        url: String,
        videoMetadata: VideoMetadata,
        mediaDirectory: String,
        externalContentUri: Uri,
    ) = coroutineScope {
        val relativePath = URI(url).path

        File("$mediaDirectory/$relativePath").also { file ->
            file.createNewFile() // TODO: Create file permission

            withContext(Dispatchers.IO) {
                setAudioTagsToFile(file, videoMetadata)

                insertMediaFileToMediaStore(
                    externalContentUri,
                    file.absolutePath,
                    relativePath,
                    videoMetadata
                )
            }
        }
    }

    private suspend inline fun createAudioFile(
        url: String,
        videoMetadata: VideoMetadata
    ) = createMediaFile(
        url = url,
        videoMetadata = videoMetadata,
        mediaDirectory = Environment.DIRECTORY_MUSIC,
        externalContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    )

    private suspend inline fun createVideoFile(
        url: String,
        videoMetadata: VideoMetadata
    ) = createMediaFile(
        url = url,
        videoMetadata = videoMetadata,
        mediaDirectory = Environment.DIRECTORY_MOVIES,
        externalContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    )

    private suspend inline fun cashAudioFile(audioUrl: String, videoMetadata: VideoMetadata) =
        ktorClient.downloadFile(
            fileUrl = audioUrl,
            storeFile = createAudioFile(
                url = audioUrl,
                videoMetadata = videoMetadata
            )
        )

    private suspend inline fun cashVideoFile(videoUrl: String, videoMetadata: VideoMetadata) =
        ktorClient.downloadFile(
            fileUrl = videoUrl,
            storeFile = createVideoFile(
                url = videoUrl,
                videoMetadata = videoMetadata
            )
        )

    private suspend inline fun cashFile(
        audioOrVideoUrl: Either<String, String>,
        videoMetadata: VideoMetadata
    ) = when (audioOrVideoUrl) {
        is Either.Left -> cashAudioFile(audioOrVideoUrl.value, videoMetadata)
        is Either.Right -> cashVideoFile(audioOrVideoUrl.value, videoMetadata)
    }

    private fun YoutubeUrlExtractor(isSaveAsVideo: Boolean) =
        YoutubeUrlExtractor(
            context = applicationContext,
            videoExtractionChannel = videoCashCompletionChannel
        ) { audioUrl, videoUrl, videoMeta ->
            val videoMetadata = videoMeta?.let(::VideoMetadata) ?: VideoMetadata()

            val audioOrVideoUrl = when {
                isSaveAsVideo -> Either.Left(audioUrl)
                else -> Either.Right(videoUrl)
            }

            curVideoMetadataState.update { videoMetadata }
            cashFile(audioOrVideoUrl, videoMetadata)
        }

    private suspend inline fun launchExtractionAndCashingFile(
        url: String,
        isSaveAsVideo: Boolean
    ): HttpStatusCode {
        YoutubeUrlExtractor(isSaveAsVideo).extract(url)
        return videoCashCompletionChannel.receive()
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

            pollVideoFromQueue()?.let { (url, isSaveAsVideo) ->
                onVideoCashStatusReceived(
                    statusCode = curVideoCashJobState.updateAndGet {
                        coroutineScope {
                            async {
                                launchExtractionAndCashingFile(url, isSaveAsVideo)
                            }
                        }
                    }!!.await()
                )
            }
        }

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
            enableLights(false)
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
            .setAutoCancel(false)
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
        videoCashQueueLen: Int
    ) = notificationBuilder
        .setContentTitle("${resources.getString(R.string.downloading)}: $videoMetadata")
        .setContentText("${resources.getString(R.string.tracks_in_queue)}: $videoCashQueueLen")
        .setOngoing(true)
        .addAction(cancelCurVideoAction)
        .addAction(cancelAllAction)

    private inline val cancelCurVideoAction
        get() = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> Notification.Action.Builder(
                null,
                resources.getString(R.string.cancel),
                Actions.CancelCurVideo.playbackIntent
            )

            else -> Notification.Action.Builder(
                0,
                resources.getString(R.string.cancel),
                Actions.CancelCurVideo.playbackIntent
            )
        }.build()

    private inline val cancelAllAction
        get() = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> Notification.Action.Builder(
                null,
                resources.getString(R.string.cancel_all),
                Actions.CancelAll.playbackIntent
            )

            else -> Notification.Action.Builder(
                0,
                resources.getString(R.string.cancel_all),
                Actions.CancelAll.playbackIntent
            )
        }.build()

    private inline val finishedNotificationBuilder
        get() = notificationBuilder
            .setContentTitle(resources.getString(R.string.video_cashed))
            .setOngoing(false)

    private fun buildNotification(
        isCashing: Boolean,
        videoMetadata: VideoMetadata,
        videoCashQueueLen: Int
    ) = when {
        isCashing -> getCashingNotificationBuilder(videoMetadata, videoCashQueueLen)
        else -> finishedNotificationBuilder
    }.build()

    private fun showCashingNotification(videoMetadata: VideoMetadata, videoCashQueueLen: Int) {
        isCashingNotificationShown = true
        startForeground(
            NOTIFICATION_ID,
            buildNotification(isCashing = true, videoMetadata, videoCashQueueLen)
        )
    }

    private fun updateNotification(
        isCashing: Boolean,
        videoMetadata: VideoMetadata,
        videoCashQueueLen: Int
    ) {
        isCashingNotificationShown = isCashing
        notificationManager.notify(
            NOTIFICATION_ID,
            buildNotification(isCashing, videoMetadata, videoCashQueueLen)
        )
    }
}