package com.paranid5.mediastreamer.domain.video_cash_service

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import android.util.SparseArray
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.repeatOnLifecycle
import arrow.core.Either
import arrow.core.merge
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.data.VideoMetadata
import com.paranid5.mediastreamer.domain.downloadFile
import com.paranid5.mediastreamer.domain.getFileExt
import com.paranid5.mediastreamer.domain.media_scanner.MediaScannerReceiver
import com.paranid5.mediastreamer.domain.media_scanner.scanNextFile
import com.paranid5.mediastreamer.domain.utils.AsyncCondVar
import com.paranid5.mediastreamer.domain.utils.extensions.insertMediaFileToMediaStore
import com.paranid5.mediastreamer.domain.utils.extensions.registerReceiverCompat
import com.paranid5.mediastreamer.domain.utils.extensions.setAudioTagsToFileCatching
import com.paranid5.mediastreamer.presentation.main_activity.MainActivity
import com.paranid5.mediastreamer.presentation.streaming.VIDEO_CASH_STATUS_ARG
import io.ktor.client.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.time.Duration.Companion.seconds

class VideoCashService : LifecycleService(), KoinComponent {
    companion object {
        private const val NOTIFICATION_ID = 102
        private const val VIDEO_CASH_CHANNEL_ID = "video_cash_channel"

        private const val SERVICE_LOCATION = "com.paranid5.mediastreamer.domain.video_cash_service"
        const val Broadcast_CASH_NEXT_VIDEO = "$SERVICE_LOCATION.CASH_NEXT_VIDEO"
        const val Broadcast_CANCEL_CUR_VIDEO = "$SERVICE_LOCATION.CANCEL_CUR_VIDEO"
        const val Broadcast_CANCEL_ALL = "$SERVICE_LOCATION.CANCEL_ALL"

        const val URL_ARG = "url"
        const val FILENAME_ARG = "filename"
        const val SAVE_AS_VIDEO_ARG = "save_as_video"

        private val TAG = VideoCashService::class.simpleName!!

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

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private val videoCashQueue: Queue<VideoCashData> = ConcurrentLinkedQueue()
    private val videoCashQueueLenState = MutableStateFlow(0)

    private val videoCashProgressState = MutableStateFlow(0L to 0L)
    private val videoCashErrorState = MutableStateFlow(0 to "")

    private val isVideoCashingCondVar = AsyncCondVar()
    private val videoCashQueueEmptyCondVar = AsyncCondVar()

    private var cashingLoopJob: Job? = null
    private var curVideoCashFile: File? = null
    private val curVideoMetadataState = MutableStateFlow<VideoMetadata?>(null)

    enum class CashingStatus { CASHING, CASHED, CANCELED, ERR, NONE }

    private val cashingStatusState = MutableStateFlow(CashingStatus.NONE)

    @Volatile
    private var wasStartForegroundUsed = false

    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    private val cashNextVideoReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            Log.d(TAG, "Cash next is received")
            scope.launch { mOfferVideoToQueue(videoCashData = intent.mVideoCashDataArg) }
        }
    }

    private val cancelCurVideoReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Canceling video cashing")
            scope.launch { mCancelCurVideoCashing() }
        }
    }

    private val cancelAllReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Canceling all videos cashing")
            scope.launch { mCancelAllVideosCashing() }
        }
    }

    private val mediaScannerReceiver = MediaScannerReceiver()

    private fun YoutubeUrlExtractor(desiredFilename: String, isSaveAsVideo: Boolean) =
        @SuppressLint("StaticFieldLeak")
        object : YouTubeExtractor(this) {
            override fun onExtractionComplete(
                ytFiles: SparseArray<YtFile>?,
                videoMeta: VideoMeta?
            ) {
                if (ytFiles == null)
                    return

                val audioTag = 140
                val audioUrl = ytFiles[audioTag].url!!

                val videoUrl = sequenceOf(22, 137, 18)
                    .map(ytFiles::get)
                    .filterNotNull()
                    .map(YtFile::getUrl)
                    .filterNotNull()
                    .filter(String::isNotEmpty)
                    .first()

                val videoMetadata = videoMeta?.let(::VideoMetadata) ?: VideoMetadata()

                val audioOrVideoUrl = when {
                    isSaveAsVideo -> Either.Right(videoUrl)
                    else -> Either.Left(audioUrl)
                }

                curVideoMetadataState.update { videoMetadata }

                scope.launch {
                    mOnVideoCashStatusReceived(
                        statusCode = mCashMediaFile(
                            desiredFilename,
                            audioOrVideoUrl,
                            videoMetadata
                        )
                    )
                }
            }
        }

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

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    internal suspend inline fun mOfferVideoToQueue(videoCashData: VideoCashData) {
        Log.d(TAG, "New video added to queue")
        videoCashQueue.offer(videoCashData)
        videoCashQueueLenState.update { videoCashQueue.size }
        videoCashQueueEmptyCondVar.notify()
        resetCashingJobIfCanceled()
    }

    private data class CashingNotificationData(
        val cashingState: CashingStatus,
        val metadata: VideoMetadata,
        val videoCashQueueLen: Int,
        val downloadedBytes: Long,
        val totalBytes: Long,
        val errorCode: Int,
        val errorDescription: String
    )

    private suspend inline fun startNotificationObserving(): Unit =
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            combine(
                cashingStatusState,
                curVideoMetadataState,
                videoCashQueueLenState,
                videoCashProgressState,
                videoCashErrorState
            ) { cashingState, videoMetadata, videoCashQueueLen,
                (downloadedBytes, totalBytes), (errorCode, errorDescription) ->
                CashingNotificationData(
                    cashingState,
                    videoMetadata ?: VideoMetadata(),
                    videoCashQueueLen,
                    downloadedBytes,
                    totalBytes,
                    errorCode,
                    errorDescription
                )
            }.collectLatest { (cashingState, videoMetadata, videoCashQueueLen,
                                  downloadedBytes, totalBytes, errorCode, errorDescription) ->
                Log.d(TAG, "Cashing state: $cashingState")

                updateNotification(
                    cashingState,
                    videoMetadata,
                    videoCashQueueLen,
                    downloadedBytes,
                    totalBytes,
                    errorCode,
                    errorDescription
                )
            }
        }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel()

        scope.launch { mOfferVideoToQueue(videoCashData = intent!!.mVideoCashDataArg) }
        resetCashingJobIfCanceled()
        scope.launch { startNotificationObserving() }
        return START_REDELIVER_INTENT
    }

    // --------------------- File Cashing ---------------------

    private fun getFullMediaDirectory(mediaDirectory: String) =
        Environment
            .getExternalStoragePublicDirectory(mediaDirectory)
            .absolutePath

    private inline val mediaDirectory
        get() = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> Environment.DIRECTORY_MOVIES
            else -> Environment.DIRECTORY_MUSIC
        }

    private fun createMediaFile(filename: String, ext: String) =
        "${getFullMediaDirectory(mediaDirectory)}/$filename.$ext"
            .takeIf { !File(it).exists() }
            ?.let(::File)
            ?.also { Log.d(TAG, "Creating file ${it.absolutePath}") }
            ?.also(File::createNewFile)
            ?: generateSequence(1) { it + 1 }
                .map { num -> "${getFullMediaDirectory(mediaDirectory)}/${filename}($num).$ext" }
                .map(::File)
                .first { !it.exists() }
                .also { Log.d(TAG, "Creating file ${it.absolutePath}") }
                .also(File::createNewFile)

    private fun createMediaFileCatching(filename: String, ext: String) =
        kotlin.runCatching { createMediaFile(filename, ext) }

    private suspend inline fun setTags(
        mediaFile: File,
        videoMetadata: VideoMetadata,
        isAudio: Boolean
    ) {
        val externalContentUri = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

            else -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val mediaDirectory = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> Environment.DIRECTORY_MOVIES
            else -> Environment.DIRECTORY_MUSIC
        }

        val mimeType = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> "video/${mediaFile.extension}"
            else -> if (isAudio) "audio/${mediaFile.extension}" else "video/${mediaFile.extension}"
        }

        val absoluteFilePath = mediaFile.absolutePath

        withContext(Dispatchers.IO) {
            insertMediaFileToMediaStore(
                externalContentUri,
                absoluteFilePath,
                mediaDirectory,
                videoMetadata,
                mimeType
            )

            setAudioTagsToFileCatching(mediaFile, videoMetadata, isAudio)
            scanNextFile(absoluteFilePath)
        }
    }

    internal suspend inline fun mCashMediaFile(
        desiredFilename: String,
        audioOrVideoUrl: Either<String, String>,
        videoMetadata: VideoMetadata
    ): HttpStatusCode? {
        val isAudio = audioOrVideoUrl.isLeft()
        val mediaUrl = audioOrVideoUrl.merge()

        val fileExt = ktorClient.getFileExt(mediaUrl)
        val storeFile = createMediaFileCatching(desiredFilename, fileExt)
            .apply { exceptionOrNull()?.printStackTrace() }
            .getOrNull()
            ?: return HttpStatusCode.BadRequest

        curVideoCashFile = storeFile
        Log.d(TAG, "Launching file download")

        val statusCode = ktorClient.downloadFile(
            fileUrl = mediaUrl,
            storeFile = storeFile,
            progressState = videoCashProgressState,
            cashingStatusState = cashingStatusState,
        )

        isVideoCashingCondVar.notify()
        Log.d(TAG, "Status code is received")

        statusCode?.takeIf { it.isSuccess() }?.let {
            setTags(
                mediaFile = storeFile,
                videoMetadata = videoMetadata,
                isAudio = isAudio
            )
        }

        return statusCode
    }

    private fun clearVideoCashStates() {
        curVideoCashFile = null
    }

    internal fun mOnVideoCashStatusReceived(statusCode: HttpStatusCode?) {
        Log.d(TAG, "Cash status handling")
        clearVideoCashStates()

        when {
            statusCode == null -> onVideoCashStatusCanceled()
            statusCode.isSuccess() -> onVideoCashStatusSuccessful()
            else -> onVideoCashStatusError(statusCode.value, statusCode.description)
        }

        Log.d(TAG, "Cash status $statusCode handled")
    }

    private fun onVideoCashStatusSuccessful() = sendBroadcast(
        Intent(MainActivity.Broadcast_VIDEO_CASH_COMPLETED)
            .putExtra(VIDEO_CASH_STATUS_ARG, VideoCashResponse.Success)
    )

    private fun onVideoCashStatusError(code: Int, description: String) {
        videoCashErrorState.update { code to description }

        sendBroadcast(
            Intent(MainActivity.Broadcast_VIDEO_CASH_COMPLETED)
                .putExtra(VIDEO_CASH_STATUS_ARG, VideoCashResponse.Error(code, description))
        )
    }

    private fun onVideoCashStatusCanceled() = sendBroadcast(
        Intent(MainActivity.Broadcast_VIDEO_CASH_COMPLETED)
            .putExtra(VIDEO_CASH_STATUS_ARG, VideoCashResponse.Canceled)
    )

    private suspend inline fun launchCashing() {
        while (true) {
            Log.d(TAG, "QUEUE ${videoCashQueue.size}")

            while (videoCashQueue.isEmpty()) {
                videoCashQueueEmptyCondVar.wait()
                Log.d(TAG, "Video Cash Queue Cond Var Awake")
            }

            while (cashingStatusState.value == CashingStatus.CASHING) {
                isVideoCashingCondVar.wait()
                Log.d(TAG, "Is Video Cashing Cond Var Awake")
            }

            videoCashQueue.poll()?.let { (url, desiredFilename, isSaveAsVideo) ->
                Log.d(TAG, "Prepare for cashing")
                videoCashProgressState.update { 0L to 0L }
                cashingStatusState.update { CashingStatus.CASHING }
                videoCashQueueLenState.update { videoCashQueue.size }
                YoutubeUrlExtractor(desiredFilename, isSaveAsVideo).extract(url)
            }
        }
    }

    private fun resetCashingJobIfCanceled() {
        Log.d(TAG, "Cashing loop status: ${cashingLoopJob?.isActive}")

        cashingStatusState.update {
            when (it) {
                CashingStatus.CANCELED -> CashingStatus.NONE
                else -> it
            }
        }

        if (cashingLoopJob?.isActive != true)
            cashingLoopJob = scope.launch(Dispatchers.IO) { launchCashing() }
    }

    internal suspend fun mCancelCurVideoCashing() {
        cashingStatusState.update { CashingStatus.CANCELED }
        isVideoCashingCondVar.notify()

        Log.d(TAG, "File is deleted ${curVideoCashFile?.delete()}")
        Log.d(TAG, "Cashing is canceled")
        clearVideoCashStates()
    }

    internal suspend fun mCancelAllVideosCashing() {
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
        job.cancel()
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

            else -> Notification.Builder(applicationContext)
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
        .setContentTitle("${resources.getString(R.string.downloading)}: ${videoMetadata.title}")
        .setContentText("${resources.getString(R.string.tracks_in_queue)}: $videoCashQueueLen")
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

    private fun finishedNotificationBuilder(message: String) = notificationBuilder
        .setContentTitle(message)
        .setAutoCancel(true)
        .setShowWhen(false)

    private fun finishedNotificationBuilder(@StringRes message: Int) =
        finishedNotificationBuilder(resources.getString(message))

    private inline val cashedNotificationBuilder
        get() = finishedNotificationBuilder(R.string.video_cashed)

    private inline val canceledNotificationBuilder
        get() = finishedNotificationBuilder(R.string.video_canceled)

    private fun getErrorNotificationBuilder(code: Int, description: String) =
        finishedNotificationBuilder("${resources.getString(R.string.error)} $code: $description")

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

    private fun showCashedNotification() = notificationManager.notify(
        NOTIFICATION_ID,
        cashedNotificationBuilder.build()
    )

    private fun showCanceledNotification() = notificationManager.notify(
        NOTIFICATION_ID,
        canceledNotificationBuilder.build()
    )

    private fun showErrorNotification(code: Int, description: String) = notificationManager.notify(
        NOTIFICATION_ID,
        getErrorNotificationBuilder(code, description).build()
    )

    private fun updateNotification(
        cashingState: CashingStatus,
        videoMetadata: VideoMetadata,
        videoCashQueueLen: Int,
        downloadedBytes: Long,
        totalBytes: Long,
        errorCode: Int,
        errorDescription: String
    ) = when {
        !wasStartForegroundUsed -> {
            Log.d(TAG, "Start Notification")

            startCashingNotification(
                videoMetadata,
                videoCashQueueLen,
                downloadedBytes,
                totalBytes
            )
        }

        else -> when (cashingState) {
            CashingStatus.CASHING -> {
                Log.d(TAG, "Cashing Notification")

                updateCashingNotification(
                    videoMetadata,
                    videoCashQueueLen,
                    downloadedBytes,
                    totalBytes
                )
            }

            else -> {
                when (cashingState) {
                    CashingStatus.CASHED -> {
                        scope.launch {
                            // Delay for NotificationManager in order
                            // to not skip this update
                            delay(1.seconds)

                            Log.d(TAG, "Cashed Notification")
                            showCashedNotification()
                        }
                    }

                    CashingStatus.CANCELED -> {
                        Log.d(TAG, "Canceled Notification")
                        showCanceledNotification()
                    }

                    CashingStatus.ERR -> {
                        Log.d(TAG, "Error Notification")
                        showErrorNotification(errorCode, errorDescription)
                    }

                    else -> Unit
                }

                detachNotification()
            }
        }
    }

    private fun detachNotification() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ->
            stopForeground(STOP_FOREGROUND_DETACH)

        else -> stopForeground(true)
    }
}