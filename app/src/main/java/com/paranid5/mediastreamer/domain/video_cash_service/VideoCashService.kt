package com.paranid5.mediastreamer.domain.video_cash_service

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.util.SparseArray
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.raise.ensure
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.data.VideoMetadata
import com.paranid5.mediastreamer.domain.LifecycleNotificationManager
import com.paranid5.mediastreamer.domain.Receiver
import com.paranid5.mediastreamer.domain.ServiceAction
import com.paranid5.mediastreamer.domain.SuspendService
import com.paranid5.mediastreamer.domain.ktor_client.downloadFile
import com.paranid5.mediastreamer.domain.ktor_client.downloadFiles
import com.paranid5.mediastreamer.domain.media_scanner.MediaScannerReceiver
import com.paranid5.mediastreamer.domain.utils.AsyncCondVar
import com.paranid5.mediastreamer.domain.utils.extensions.registerReceiverCompat
import com.paranid5.mediastreamer.domain.utils.media.MediaFile
import com.paranid5.mediastreamer.domain.utils.media.convertToAudioFileAndSetTagsAsync
import com.paranid5.mediastreamer.domain.utils.media.createMediaFileCatching
import com.paranid5.mediastreamer.domain.utils.media.getInitialMediaDirectory
import com.paranid5.mediastreamer.domain.utils.media.mergeToMP4AndSetTagsAsync
import com.paranid5.mediastreamer.presentation.main_activity.MainActivity
import com.paranid5.mediastreamer.presentation.streaming.VIDEO_CASH_STATUS_ARG
import io.ktor.client.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.time.Duration.Companion.seconds

class VideoCashService : SuspendService(), Receiver, LifecycleNotificationManager, KoinComponent {
    companion object {
        private const val NOTIFICATION_ID = 102
        private const val VIDEO_CASH_CHANNEL_ID = "video_cash_channel"

        private const val SERVICE_LOCATION = "com.paranid5.mediastreamer.domain.video_cash_service"
        const val Broadcast_CASH_NEXT_VIDEO = "$SERVICE_LOCATION.CASH_NEXT_VIDEO"
        const val Broadcast_CANCEL_CUR_VIDEO = "$SERVICE_LOCATION.CANCEL_CUR_VIDEO"
        const val Broadcast_CANCEL_ALL = "$SERVICE_LOCATION.CANCEL_ALL"

        const val URL_ARG = "url"
        const val FILENAME_ARG = "filename"
        const val FORMAT_ARG = "format"

        private val TAG = VideoCashService::class.simpleName!!

        internal inline val Intent.mVideoCashDataArg
            get() = VideoCashData(
                url = getStringExtra(URL_ARG)!!,
                desiredFilename = getStringExtra(FILENAME_ARG)!!,
                format = getParcelableExtra(FORMAT_ARG, Formats::class.java)!!
            )
    }

    private sealed class Actions(
        override val requestCode: Int,
        override val playbackAction: String
    ) : ServiceAction {
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
        val format: Formats
    )

    private val binder = object : Binder() {}
    private val ktorClient by inject<HttpClient>()
    private var cashingLoopJob: Job? = null

    private val videoCashQueue: Queue<VideoCashData> = ConcurrentLinkedQueue()
    private val videoCashQueueLenState = MutableStateFlow(0)

    private val videoCashProgressState = MutableStateFlow(0L to 0L)
    private val videoCashErrorState = MutableStateFlow(0 to "")

    private val isVideoCashingCondVar = AsyncCondVar()
    private val isVideoCashQueueEmptyCondVar = AsyncCondVar()

    private var curVideoCashFile: MediaFile? = null
    private val curVideoMetadataState = MutableStateFlow<VideoMetadata?>(null)
    private val cashingStatusState = MutableStateFlow(DownloadingStatus.NONE)

    @Volatile
    private var wasStartForegroundUsed = false

    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    // --------------------------- Action Receivers ---------------------------

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

    override fun registerReceivers() {
        registerReceiverCompat(cashNextVideoReceiver, Broadcast_CASH_NEXT_VIDEO)
        registerReceiverCompat(cancelCurVideoReceiver, Broadcast_CANCEL_CUR_VIDEO)
        registerReceiverCompat(cancelAllReceiver, Broadcast_CANCEL_ALL)
        registerReceiverCompat(mediaScannerReceiver, MediaScannerReceiver.Broadcast_SCAN_NEXT_FILE)
    }

    override fun unregisterReceivers() {
        unregisterReceiver(cashNextVideoReceiver)
        unregisterReceiver(cancelCurVideoReceiver)
        unregisterReceiver(cancelAllReceiver)
        unregisterReceiver(mediaScannerReceiver)
    }

    // --------------------------- Service Impl ---------------------------

    override fun onCreate() {
        super.onCreate()
        registerReceivers()
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel()

        scope.launch { mOfferVideoToQueue(videoCashData = intent!!.mVideoCashDataArg) }
        resetCashingJobIfCanceled()
        scope.launch { startNotificationObserving() }
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceivers()
    }

    // --------------------------- Cashing Management ---------------------------

    private fun YoutubeUrlExtractor(desiredFilename: String, format: Formats) =
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

                val videoUrl = sequenceOf(137, 22, 18)
                    .map(ytFiles::get)
                    .filterNotNull()
                    .map(YtFile::getUrl)
                    .filterNotNull()
                    .filter(String::isNotEmpty)
                    .first()

                val videoMetadata = videoMeta?.let(::VideoMetadata) ?: VideoMetadata()
                curVideoMetadataState.update { videoMetadata }

                scope.launch {
                    mOnVideoCashStatusReceived(
                        cashingResult = mCashMediaFileOrNotifyError(
                            desiredFilename = desiredFilename,
                            audioUrl = audioUrl,
                            videoUrl = if (format == Formats.MP4) videoUrl else null,
                            videoMetadata = videoMetadata,
                            format = format
                        )
                    )
                }
            }
        }

    internal suspend inline fun mOfferVideoToQueue(videoCashData: VideoCashData) {
        Log.d(TAG, "New video added to queue")
        videoCashQueue.offer(videoCashData)
        videoCashQueueLenState.update { videoCashQueue.size }
        isVideoCashQueueEmptyCondVar.notify()
        resetCashingJobIfCanceled()
    }

    // --------------------- Media File Management ---------------------

    /**
     * Prepares store file for the download request or notifies about error.
     * @param desiredFilename filename chosen by the user
     * @param isAudio is final file required to be in audio format
     * @return [MediaFile.VideoFile] with file or
     * [CashingResult.DownloadResult.Error] with [HttpStatusCode] if error has occurred
     */

    private suspend inline fun initMediaFile(
        desiredFilename: String,
        isAudio: Boolean
    ) = either {
        val storeFileRes = createMediaFileCatching(
            mediaDirectory = getInitialMediaDirectory(isAudio),
            filename = desiredFilename.replace(Regex("\\W+"), "_"),
            ext = "mp4"
        )

        ensure(storeFileRes.isSuccess) {
            storeFileRes.exceptionOrNull()!!.printStackTrace()
            CashingResult.DownloadResult.FileCreationError
        }

        storeFileRes.getOrNull()!!
    }

    /**
     * Prepares store file for the download request or cancels cashing and notifies about error.
     * @param desiredFilename filename chosen by the user
     * @param isAudio is final file required to be in audio format
     * @return [MediaFile.VideoFile] with file or
     * [CashingResult.DownloadResult.Error] with [HttpStatusCode] if error has occurred
     */

    private suspend inline fun initMediaFileOrNotifyError(
        desiredFilename: String,
        isAudio: Boolean
    ) = initMediaFile(desiredFilename, isAudio).onLeft {
        cashingStatusState.update { DownloadingStatus.ERR }
        isVideoCashingCondVar.notify()
    }

    /**
     * Downloads initial mp4 file by the url according to the acquired itag.
     * Allows the task to be canceled by user.
     * Provides error message handling, no need to do it further
     * @param desiredFilename filename chosen by the user
     * @param mediaUrl url to the initial mp4 file
     * @param isAudio is final file required to be in audio format
     * @return [CashingResult.DownloadResult.Success] with file if conversion was successful,
     * [CashingResult.DownloadResult.Error] with [HttpStatusCode] if error has occurred,
     * [CashingResult.DownloadResult.Canceled] if cashing was canceled by the user
     */

    private suspend inline fun downloadMediaFileOrNotifyError(
        desiredFilename: String,
        mediaUrl: String,
        isAudio: Boolean
    ): CashingResult.DownloadResult {
        val storeFileRes = initMediaFileOrNotifyError(desiredFilename, isAudio)

        curVideoCashFile = when (storeFileRes) {
            is Either.Left -> return storeFileRes.value
            is Either.Right -> storeFileRes.value
        }

        val statusCode = ktorClient.downloadFile(
            fileUrl = mediaUrl,
            storeFile = curVideoCashFile!!,
            progressState = videoCashProgressState,
            downloadingState = cashingStatusState,
        )

        if (statusCode?.isSuccess() != true) {
            onCashingError()
            return statusCode?.let(CashingResult.DownloadResult::Error)
                ?: CashingResult.DownloadResult.Canceled
        }

        isVideoCashingCondVar.notify()
        Log.d(TAG, "Status code is received")
        return CashingResult.DownloadResult.Success(curVideoCashFile!!)
    }

    /**
     * Downloads both audio and video mp4 files by the urls according to the acquired itags.
     * Allows the task to be canceled by user.
     * Provides error message handling, no need to do it further
     * @param audioUrl url to the initial audio mp4 file
     * @param videoUrl url to the initial video mp4 file
     * @param audioFileStore file to store downloaded audio mp4 file
     * @param videoFileStore file to store downloaded video mp4 file
     * @return [CashingResult.DownloadResult.Success] with file if conversion was successful,
     * [CashingResult.DownloadResult.Error] with [HttpStatusCode] if error has occurred,
     * [CashingResult.DownloadResult.Canceled] if cashing was canceled by the user
     */

    private suspend inline fun downloadAudioAndVideoFilesOrNotifyError(
        audioUrl: String,
        videoUrl: String,
        audioFileStore: MediaFile,
        videoFileStore: MediaFile.VideoFile
    ): CashingResult.DownloadResult {
        val statusCode = ktorClient.downloadFiles(
            files = arrayOf(audioUrl to audioFileStore, videoUrl to videoFileStore),
            progressState = videoCashProgressState,
            downloadingState = cashingStatusState,
        )

        if (statusCode?.isSuccess() != true) {
            onCashingError()
            return statusCode?.let(CashingResult.DownloadResult::Error)
                ?: CashingResult.DownloadResult.Canceled
        }

        isVideoCashingCondVar.notify()
        Log.d(TAG, "Status code is received")
        return CashingResult.DownloadResult.Success(audioFileStore)
    }

    /**
     * Initialises both mp4 files (audio and video) to download data and then merge them together.
     * Provides error message handling, no need to do it further
     * @param desiredFilename filename chosen by the user
     * @return audio [MediaFile] and [MediaFile.VideoFile] or
     * [CashingResult.DownloadResult.Error] with [HttpStatusCode] if error has occurred
     */

    private suspend inline fun prepareMediaFilesForMP4MergingOrNotifyErrors(
        desiredFilename: String,
    ): Either<CashingResult.DownloadResult.FileCreationError, Pair<MediaFile, MediaFile.VideoFile>> {
        val audioFileStoreRes = initMediaFileOrNotifyError(
            desiredFilename,
            isAudio = true
        )

        val audioFileStore = when (audioFileStoreRes) {
            is Either.Left -> return audioFileStoreRes.value.left()
            is Either.Right -> audioFileStoreRes.value
        }

        val videoFileStoreRes = initMediaFileOrNotifyError(
            desiredFilename,
            isAudio = false
        )

        val videoFileStore = when (videoFileStoreRes) {
            is Either.Left -> {
                audioFileStore.delete()
                return videoFileStoreRes.value.left()
            }

            is Either.Right -> videoFileStoreRes.value
        }

        return Either.Right(audioFileStore to videoFileStore)
    }

    // --------------------- Media Cashing ---------------------

    /**
     * Downloads both video and audio mp4 files,
     * convert audio to .aac file and
     * combines them into single mp4 file
     * @param desiredFilename filename chosen by the user
     * @param audioUrl url to the audio mp4 file
     * @param videoUrl url to the video mp4 file
     * @param videoMetadata metadata to set as tags
     * @return [CashingResult.DownloadResult.Success] with file if conversion was successful,
     * [CashingResult.DownloadResult.Error] with [HttpStatusCode] if error has occurred,
     * [CashingResult.DownloadResult.Canceled] if cashing was canceled by the user,
     * [CashingResult.ConversionError] if conversion to .aac audio file has failed
     */

    private suspend inline fun cashVideoFileOrNotifyError(
        desiredFilename: String,
        audioUrl: String,
        videoUrl: String,
        videoMetadata: VideoMetadata
    ): CashingResult {
        val mediaFilesInitResult = prepareMediaFilesForMP4MergingOrNotifyErrors(desiredFilename)

        val (audioFileStore, videoFileStore) = when (mediaFilesInitResult) {
            is Either.Left -> return mediaFilesInitResult.value
            is Either.Right -> mediaFilesInitResult.value
        }

        val deleteStoreFilesAndHandleError = suspend {
            audioFileStore.delete()
            videoFileStore.delete()
            onCashingError()
        }

        downloadAudioAndVideoFilesOrNotifyError(
            audioUrl,
            videoUrl,
            audioFileStore,
            videoFileStore
        ).let { result ->
            if (result !is CashingResult.DownloadResult.Success)
                return result
        }

        return when (val storeFileRes =
            initMediaFileOrNotifyError(desiredFilename, isAudio = false)
        ) {
            is Either.Left -> {
                deleteStoreFilesAndHandleError()
                storeFileRes.value
            }

            is Either.Right -> mergeToMP4AndSetTagsAsync(
                context = this,
                audioTrack = audioFileStore,
                videoTrack = videoFileStore,
                mp4StoreFile = storeFileRes.value,
                videoMetadata = videoMetadata
            ).await()
        }
    }

    /**
     * Downloads audio-only mp4 file and converts it to the required [audioFormat]
     * @param desiredFilename filename chosen by the user
     * @param audioUrl url to the audio mp4 file
     * @param videoMetadata metadata to set as tags
     * @param audioFormat audio file format
     * @return [CashingResult.DownloadResult.Success] with file if conversion was successful,
     * [CashingResult.DownloadResult.Error] with [HttpStatusCode] if error has occurred,
     * [CashingResult.DownloadResult.Canceled] if cashing was canceled by the user,
     * [CashingResult.ConversionError] if conversion to .aac audio file has failed
     */

    private suspend inline fun cashAudioFileOrNotifyError(
        desiredFilename: String,
        audioUrl: String,
        videoMetadata: VideoMetadata,
        audioFormat: Formats
    ): CashingResult {
        val result = downloadMediaFileOrNotifyError(desiredFilename, audioUrl, isAudio = true)

        if (result !is CashingResult.DownloadResult.Success)
            return result

        return when (val audioConversionResult =
            (result.file as MediaFile.VideoFile).convertToAudioFileAndSetTagsAsync(
                context = this,
                videoMetadata = videoMetadata,
                audioFormat = audioFormat
            ).await()
        ) {
            null -> {
                onCashingError()
                CashingResult.ConversionError
            }

            else -> CashingResult.DownloadResult.Success(audioConversionResult)
        }
    }

    /**
     * Cashes either with [cashAudioFileOrNotifyError] if [videoUrl] is null,
     * or with [cashVideoFileOrNotifyError] otherwise
     * @param desiredFilename filename chosen by the user
     * @param audioUrl url to the audio mp4 file
     * @param videoUrl url to the video mp4 file
     * @param videoMetadata metadata to set as tags
     * @param format required media file format
     * @return [CashingResult.DownloadResult.Success] with file if conversion was successful,
     * [CashingResult.DownloadResult.Error] with [HttpStatusCode] if error has occurred,
     * [CashingResult.DownloadResult.Canceled] if cashing was canceled by the user,
     * [CashingResult.ConversionError] if conversion to .aac audio file has failed
     */

    internal suspend inline fun mCashMediaFileOrNotifyError(
        desiredFilename: String,
        audioUrl: String,
        videoUrl: String?,
        videoMetadata: VideoMetadata,
        format: Formats
    ) = when (videoUrl) {
        null -> cashAudioFileOrNotifyError(
            desiredFilename,
            audioUrl,
            videoMetadata,
            format
        )

        else -> cashVideoFileOrNotifyError(
            desiredFilename,
            audioUrl,
            videoUrl,
            videoMetadata
        )
    }

    /** Runs loop that cashes files from the [videoCashQueue] */

    private suspend inline fun launchCashing() {
        while (true) {
            Log.d(TAG, "QUEUE ${videoCashQueue.size}")

            while (videoCashQueue.isEmpty()) {
                isVideoCashQueueEmptyCondVar.wait()
                Log.d(TAG, "Video Cash Queue Cond Var Awake")
            }

            while (cashingStatusState.value == DownloadingStatus.DOWNLOADING) {
                isVideoCashingCondVar.wait()
                Log.d(TAG, "Is Video Cashing Cond Var Awake")
            }

            videoCashQueue.poll()?.let { (url, desiredFilename, isSaveAsVideo) ->
                Log.d(TAG, "Prepare for cashing")
                videoCashProgressState.update { 0L to 0L }
                cashingStatusState.update { DownloadingStatus.DOWNLOADING }
                videoCashQueueLenState.update { videoCashQueue.size }
                YoutubeUrlExtractor(desiredFilename, isSaveAsVideo).extract(url)
            }
        }
    }

    private fun resetCashingJobIfCanceled() {
        Log.d(TAG, "Cashing loop status: ${cashingLoopJob?.isActive}")

        cashingStatusState.update {
            when (it) {
                DownloadingStatus.CANCELED -> DownloadingStatus.NONE
                else -> it
            }
        }

        if (cashingLoopJob?.isActive != true)
            cashingLoopJob = scope.launch(Dispatchers.IO) { launchCashing() }
    }

    internal suspend inline fun mCancelCurVideoCashing() {
        cashingStatusState.update { DownloadingStatus.CANCELED }
        isVideoCashingCondVar.notify()

        Log.d(TAG, "File is deleted ${curVideoCashFile?.delete()}")
        Log.d(TAG, "Cashing is canceled")
        clearVideoCashStates()
    }

    internal suspend inline fun mCancelAllVideosCashing() {
        videoCashQueue.clear()
        videoCashQueueLenState.update { 0 }
        mCancelCurVideoCashing()
    }

    private fun clearVideoCashStates() {
        curVideoCashFile = null
    }

    // --------------------- Cashing Response Handling ---------------------

    /** Sends broadcasts according to the [cashingResult] */

    internal fun mOnVideoCashStatusReceived(cashingResult: CashingResult) {
        Log.d(TAG, "Cashing result handling")
        clearVideoCashStates()

        when (cashingResult) {
            CashingResult.ConversionError -> onAudioConversionError()

            CashingResult.DownloadResult.Canceled -> onVideoCashCanceled()

            is CashingResult.DownloadResult.Error -> onDownloadError(
                code = cashingResult.statusCode.value,
                description = cashingResult.statusCode.description
            )

            is CashingResult.DownloadResult.Success -> onVideoCashSuccessful()

            CashingResult.DownloadResult.FileCreationError -> onFileCreationError()
        }

        Log.d(TAG, "Cashing result $cashingResult handled")
    }

    private fun onVideoCashSuccessful() = sendBroadcast(
        Intent(MainActivity.Broadcast_VIDEO_CASH_COMPLETED)
            .putExtra(VIDEO_CASH_STATUS_ARG, VideoCashResponse.Success)
    )

    private fun onDownloadError(code: Int, description: String) {
        videoCashErrorState.update { code to description }

        sendBroadcast(
            Intent(MainActivity.Broadcast_VIDEO_CASH_COMPLETED)
                .putExtra(VIDEO_CASH_STATUS_ARG, VideoCashResponse.Error(code, description))
        )
    }

    private fun onVideoCashCanceled() = sendBroadcast(
        Intent(MainActivity.Broadcast_VIDEO_CASH_COMPLETED)
            .putExtra(VIDEO_CASH_STATUS_ARG, VideoCashResponse.Canceled)
    )

    private fun onAudioConversionError() = sendBroadcast(
        Intent(MainActivity.Broadcast_VIDEO_CASH_COMPLETED)
            .putExtra(VIDEO_CASH_STATUS_ARG, VideoCashResponse.AudioConversionError)
    )

    private fun onFileCreationError() = sendBroadcast(
        Intent(MainActivity.Broadcast_VIDEO_CASH_COMPLETED)
            .putExtra(VIDEO_CASH_STATUS_ARG, VideoCashResponse.FileCreationError)
    )

    private suspend inline fun onCashingError() {
        cashingStatusState.update { DownloadingStatus.ERR }
        isVideoCashingCondVar.notify()

        Log.d(TAG, "File is deleted ${curVideoCashFile?.delete()}")
        Log.d(TAG, "Cashing was interrupted by an error")
        clearVideoCashStates()
    }

    // --------------------------- Notification Actions ---------------------------

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

    // --------------------------- Notification Handle ---------------------------

    @RequiresApi(Build.VERSION_CODES.O)
    override fun createChannel() = notificationManager.createNotificationChannel(
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

    private data class CashingNotificationData(
        val cashingState: DownloadingStatus,
        val metadata: VideoMetadata,
        val videoCashQueueLen: Int,
        val downloadedBytes: Long,
        val totalBytes: Long,
        val errorCode: Int,
        val errorDescription: String
    )

    /**
     * Runs loop that observers all notification related states
     * and updates notification when something has changed
     */

    override suspend fun startNotificationObserving(): Unit =
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
        cashingState: DownloadingStatus,
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
            DownloadingStatus.DOWNLOADING -> updateCashingNotification(
                videoMetadata,
                videoCashQueueLen,
                downloadedBytes,
                totalBytes
            )

            else -> {
                when (cashingState) {
                    DownloadingStatus.DOWNLOADED -> {
                        scope.launch {
                            // Delay for NotificationManager in order
                            // to not skip this update
                            delay(1.seconds)

                            Log.d(TAG, "Cashed Notification")
                            showCashedNotification()
                        }
                    }

                    DownloadingStatus.CANCELED -> {
                        Log.d(TAG, "Canceled Notification")
                        showCanceledNotification()
                    }

                    DownloadingStatus.ERR -> {
                        Log.d(TAG, "Error Notification")
                        showErrorNotification(errorCode, errorDescription)
                    }

                    else -> Unit
                }

                detachNotification()
            }
        }
    }

    override fun detachNotification() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ->
            stopForeground(STOP_FOREGROUND_DETACH)

        else -> stopForeground(true)
    }
}