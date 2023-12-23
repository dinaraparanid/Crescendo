package com.paranid5.crescendo.services.video_cache_service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.paranid5.crescendo.R
import com.paranid5.crescendo.VIDEO_CASH_SERVICE_CONNECTION
import com.paranid5.crescendo.domain.VideoMetadata
import com.paranid5.crescendo.domain.trimming.TrimRange
import com.paranid5.crescendo.domain.caching.CachingResult
import com.paranid5.crescendo.domain.caching.DownloadingStatus
import com.paranid5.crescendo.domain.caching.Formats
import com.paranid5.crescendo.domain.caching.VideoCacheResponse
import com.paranid5.crescendo.domain.ktor_client.DownloadingProgress
import com.paranid5.crescendo.domain.ktor_client.UrlWithFile
import com.paranid5.crescendo.domain.ktor_client.downloadFile
import com.paranid5.crescendo.domain.ktor_client.downloadFiles
import com.paranid5.crescendo.domain.media.files.MediaFile
import com.paranid5.crescendo.domain.media.files.createVideoFileCatching
import com.paranid5.crescendo.domain.media.files.getInitialVideoDirectory
import com.paranid5.crescendo.domain.media_scanner.MediaScannerReceiver
import com.paranid5.crescendo.domain.utils.AsyncCondVar
import com.paranid5.crescendo.domain.utils.extensions.registerReceiverCompat
import com.paranid5.crescendo.media.convertToAudioFileAndSetTagsAsync
import com.paranid5.crescendo.media.mergeToMP4AndSetTagsAsync
import com.paranid5.crescendo.presentation.main.MainActivity
import com.paranid5.crescendo.presentation.main.MainActivity.Companion.VIDEO_CASH_STATUS_ARG
import com.paranid5.crescendo.services.ServiceAction
import com.paranid5.crescendo.services.SuspendService
import com.paranid5.yt_url_extractor_kt.YtFile
import com.paranid5.yt_url_extractor_kt.extractYtFilesWithMeta
import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.time.Duration.Companion.seconds

class VideoCacheService : SuspendService(), KoinComponent {
    companion object {
        private const val NOTIFICATION_ID = 102
        private const val VIDEO_CASH_CHANNEL_ID = "video_cache_channel"

        private const val SERVICE_LOCATION = "com.paranid5.crescendo.services.video_cache_service"
        const val Broadcast_CASH_NEXT_VIDEO = "$SERVICE_LOCATION.CASH_NEXT_VIDEO"
        const val Broadcast_CANCEL_CUR_VIDEO = "$SERVICE_LOCATION.CANCEL_CUR_VIDEO"
        const val Broadcast_CANCEL_ALL = "$SERVICE_LOCATION.CANCEL_ALL"

        const val URL_ARG = "url"
        const val FILENAME_ARG = "filename"
        const val FORMAT_ARG = "format"
        const val TRIM_RANGE_ARG = "trim_range"

        private val TAG = VideoCacheService::class.simpleName!!

        @Suppress("DEPRECATION")
        internal inline val Intent.mVideoCacheDataArg
            get() = VideoCacheData(
                url = getStringExtra(URL_ARG)!!,
                desiredFilename = getStringExtra(FILENAME_ARG)!!,
                format = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                        getParcelableExtra(FORMAT_ARG, Formats::class.java)!!

                    else -> getParcelableExtra(FORMAT_ARG)!!
                },
                trimRange = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                        getParcelableExtra(TRIM_RANGE_ARG, TrimRange::class.java)!!

                    else -> getParcelableExtra(TRIM_RANGE_ARG)!!
                }
            )
    }

    private sealed class Actions(
        override val requestCode: Int,
        override val playbackAction: String
    ) : ServiceAction {
        data object CancelCurVideo : Actions(
            requestCode = NOTIFICATION_ID + 1,
            playbackAction = Broadcast_CANCEL_CUR_VIDEO
        )

        data object CancelAll : Actions(
            requestCode = NOTIFICATION_ID + 2,
            playbackAction = Broadcast_CANCEL_ALL
        )
    }

    private inline val Actions.playbackIntent: PendingIntent
        get() = PendingIntent.getBroadcast(
            this@VideoCacheService,
            requestCode,
            Intent(playbackAction),
            PendingIntent.FLAG_IMMUTABLE
        )

    internal data class VideoCacheData(
        val url: String,
        val desiredFilename: String,
        val format: Formats,
        val trimRange: TrimRange
    )

    private val ktorClient by inject<HttpClient>()
    private var cachingLoopJob: Job? = null

    private val videoCashQueue: Queue<VideoCacheData> = ConcurrentLinkedQueue()
    private val videoCashQueueLenState = MutableStateFlow(0)

    private val videoCashProgressState = MutableStateFlow(DownloadingProgress(0L, 0L))
    private val videoCashErrorState = MutableStateFlow(0 to "")

    private val isConnectedState by inject<MutableStateFlow<Boolean>>(
        named(VIDEO_CASH_SERVICE_CONNECTION)
    )

    private val isVideoCashingCondVar = AsyncCondVar()
    private val isVideoCashQueueEmptyCondVar = AsyncCondVar()

    private var curVideoCashFile: MediaFile? = null
    private val curVideoMetadataState = MutableStateFlow<VideoMetadata?>(null)
    private val cachingStatusState = MutableStateFlow(DownloadingStatus.NONE)

    @Volatile
    private var wasStartForegroundUsed = false

    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    // --------------------------- Action Receivers ---------------------------

    private val cacheNextVideoReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            Log.d(TAG, "Cash next is received")
            scope.launch { mOfferVideoToQueue(videoCacheData = intent.mVideoCacheDataArg) }
        }
    }

    private val cancelCurVideoReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Canceling video caching")
            scope.launch { mCancelCurVideoCashing() }
        }
    }

    private val cancelAllReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Canceling all videos caching")
            scope.launch { mCancelAllVideosCashing() }
        }
    }

    private val mediaScannerReceiver = MediaScannerReceiver()

    private fun registerReceivers() {
        registerReceiverCompat(cacheNextVideoReceiver, Broadcast_CASH_NEXT_VIDEO)
        registerReceiverCompat(cancelCurVideoReceiver, Broadcast_CANCEL_CUR_VIDEO)
        registerReceiverCompat(cancelAllReceiver, Broadcast_CANCEL_ALL)
        registerReceiverCompat(mediaScannerReceiver, MediaScannerReceiver.Broadcast_SCAN_FILE)
    }

    private fun unregisterReceivers() {
        unregisterReceiver(cacheNextVideoReceiver)
        unregisterReceiver(cancelCurVideoReceiver)
        unregisterReceiver(cancelAllReceiver)
        unregisterReceiver(mediaScannerReceiver)
    }

    // --------------------------- Service Impl ---------------------------

    override fun onCreate() {
        super.onCreate()
        registerReceivers()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        isConnectedState.update { true }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel()

        scope.launch { mOfferVideoToQueue(videoCacheData = intent!!.mVideoCacheDataArg) }
        resetCashingJobIfCanceled()
        scope.launch { startNotificationObserving() }
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        isConnectedState.update { false }
        unregisterReceivers()
    }

    // --------------------------- Cashing Management ---------------------------

    private suspend inline fun extractMediaFilesAndStartCaching(
        ytUrl: String,
        desiredFilename: String,
        format: Formats,
        trimRange: TrimRange
    ) {
        val extractRes = ktorClient.extractYtFilesWithMeta(
            context = this,
            ytUrl = ytUrl
        )

        val (ytFiles, _, videoMetaRes) =
            when (val res = extractRes.getOrNull()) {
                null -> {
                    extractRes.exceptionOrNull()!!.printStackTrace()
                    return
                }

                else -> res
            }

        val videoMeta = videoMetaRes.getOrNull()

        if (videoMeta?.isLiveStream == true) {
            onLiveStreamCaching()
            return
        }

        val audioTag = 140
        val audioUrl = ytFiles[audioTag]!!.url!!

        val videoUrl = sequenceOf(137, 22, 18)
            .map(ytFiles::get)
            .filterNotNull()
            .map(YtFile::url)
            .filterNotNull()
            .filter(String::isNotEmpty)
            .first()

        val videoMetadata = videoMeta?.let(::VideoMetadata) ?: VideoMetadata()
        curVideoMetadataState.update { videoMetadata }

        scope.launch {
            onVideoCacheStatusReceived(
                cachingResult = cacheMediaFileOrNotifyError(
                    desiredFilename = desiredFilename,
                    audioUrl = audioUrl,
                    videoUrl = if (format == Formats.MP4) videoUrl else null,
                    videoMetadata = videoMetadata,
                    format = format,
                    trimRange = trimRange
                )
            )
        }
    }

    internal suspend inline fun mOfferVideoToQueue(videoCacheData: VideoCacheData) {
        Log.d(TAG, "New video added to queue")
        videoCashQueue.offer(videoCacheData)
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
     * [CachingResult.DownloadResult.Error] with [HttpStatusCode] if error has occurred
     */

    private suspend inline fun initMediaFile(
        desiredFilename: String,
        isAudio: Boolean
    ) = either {
        val storeFileRes = createVideoFileCatching(
            mediaDirectory = getInitialVideoDirectory(isAudio),
            filename = desiredFilename.replace(Regex("\\W+"), "_"),
            ext = "mp4"
        )

        ensure(storeFileRes.isSuccess) {
            storeFileRes.exceptionOrNull()!!.printStackTrace()
            CachingResult.DownloadResult.FileCreationError
        }

        storeFileRes.getOrNull()!!
    }

    /**
     * Prepares store file for the download request or cancels caching and notifies about error.
     * @param desiredFilename filename chosen by the user
     * @param isAudio is final file required to be in audio format
     * @return [MediaFile.VideoFile] with file or
     * [CachingResult.DownloadResult.Error] with [HttpStatusCode] if error has occurred
     */

    private suspend inline fun initMediaFileOrNotifyError(
        desiredFilename: String,
        isAudio: Boolean
    ) = initMediaFile(desiredFilename, isAudio).onLeft {
        cachingStatusState.update { DownloadingStatus.ERR }
        isVideoCashingCondVar.notify()
    }

    /**
     * Downloads initial mp4 file by the url according to the acquired itag.
     * Allows the task to be canceled by user.
     * Provides error message handling, no need to do it further
     * @param desiredFilename filename chosen by the user
     * @param mediaUrl url to the initial mp4 file
     * @param isAudio is final file required to be in audio format
     * @return [CachingResult.DownloadResult.Success] with file if conversion was successful,
     * [CachingResult.DownloadResult.Error] with [HttpStatusCode] if error has occurred,
     * [CachingResult.DownloadResult.Canceled] if caching was canceled by the user
     */

    private suspend inline fun downloadMediaFileOrNotifyError(
        desiredFilename: String,
        mediaUrl: String,
        isAudio: Boolean
    ): CachingResult.DownloadResult {
        val storeFileRes = initMediaFileOrNotifyError(desiredFilename, isAudio)

        curVideoCashFile = when (storeFileRes) {
            is Either.Left -> return storeFileRes.value
            is Either.Right -> storeFileRes.value
        }

        val statusCode = ktorClient.downloadFile(
            fileUrl = mediaUrl,
            storeFile = curVideoCashFile!!,
            progressState = videoCashProgressState,
            downloadingState = cachingStatusState,
        )

        if (statusCode?.isSuccess() != true) {
            onCashingError(errorStatus = DownloadingStatus.ERR)
            return statusCode?.let(CachingResult.DownloadResult::Error)
                ?: CachingResult.DownloadResult.Canceled
        }

        isVideoCashingCondVar.notify()
        Log.d(TAG, "Status code is received")
        return CachingResult.DownloadResult.Success(curVideoCashFile!!)
    }

    /**
     * Downloads both audio and video mp4 files by the urls according to the acquired itags.
     * Allows the task to be canceled by user.
     * Provides error message handling, no need to do it further
     * @param audioUrl url to the initial audio mp4 file
     * @param videoUrl url to the initial video mp4 file
     * @param audioFileStore file to store downloaded audio mp4 file
     * @param videoFileStore file to store downloaded video mp4 file
     * @return [CachingResult.DownloadResult.Success] with file if conversion was successful,
     * [CachingResult.DownloadResult.Error] with [HttpStatusCode] if error has occurred,
     * [CachingResult.DownloadResult.Canceled] if caching was canceled by the user
     */

    private suspend inline fun downloadAudioAndVideoFilesOrNotifyError(
        audioUrl: String,
        videoUrl: String,
        audioFileStore: MediaFile,
        videoFileStore: MediaFile.VideoFile
    ): CachingResult.DownloadResult {
        val statusCode = ktorClient.downloadFiles(
            cachingStatusState,
            videoCashProgressState,
            UrlWithFile(audioUrl, audioFileStore), UrlWithFile(videoUrl, videoFileStore)
        )

        if (statusCode?.isSuccess() != true) {
            onCashingError(errorStatus = DownloadingStatus.ERR)
            return statusCode?.let(CachingResult.DownloadResult::Error)
                ?: CachingResult.DownloadResult.Canceled
        }

        isVideoCashingCondVar.notify()
        Log.d(TAG, "Status code is received")
        return CachingResult.DownloadResult.Success(audioFileStore)
    }

    /**
     * Initialises both mp4 files (audio and video) to download data and then merge them together.
     * Provides error message handling, no need to do it further
     * @param desiredFilename filename chosen by the user
     * @return audio [MediaFile] and [MediaFile.VideoFile] or
     * [CachingResult.DownloadResult.Error] with [HttpStatusCode] if error has occurred
     */

    private suspend inline fun prepareMediaFilesForMP4MergingOrNotifyErrors(
        desiredFilename: String,
    ): Either<CachingResult.DownloadResult.FileCreationError, Pair<MediaFile, MediaFile.VideoFile>> {
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
     * @return [CachingResult.DownloadResult.Success] with file if conversion was successful,
     * [CachingResult.DownloadResult.Error] with [HttpStatusCode] if error has occurred,
     * [CachingResult.DownloadResult.Canceled] if caching was canceled by the user,
     * [CachingResult.ConversionError] if conversion to .aac audio file has failed
     */

    private suspend inline fun cacheVideoFileOrNotifyError(
        desiredFilename: String,
        audioUrl: String,
        videoUrl: String,
        videoMetadata: VideoMetadata
    ): CachingResult {
        val mediaFilesInitResult = prepareMediaFilesForMP4MergingOrNotifyErrors(desiredFilename)

        val (audioFileStore, videoFileStore) = when (mediaFilesInitResult) {
            is Either.Left -> return mediaFilesInitResult.value
            is Either.Right -> mediaFilesInitResult.value
        }

        val deleteStoreFilesAndHandleError = suspend {
            audioFileStore.delete()
            videoFileStore.delete()
            onCashingError(errorStatus = DownloadingStatus.ERR)
        }

        downloadAudioAndVideoFilesOrNotifyError(
            audioUrl,
            videoUrl,
            audioFileStore,
            videoFileStore
        ).let { result ->
            if (result !is CachingResult.DownloadResult.Success)
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
     * @return [CachingResult.DownloadResult.Success] with file if conversion was successful,
     * [CachingResult.DownloadResult.Error] with [HttpStatusCode] if error has occurred,
     * [CachingResult.DownloadResult.Canceled] if caching was canceled by the user,
     * [CachingResult.ConversionError] if conversion to .aac audio file has failed
     */

    private suspend inline fun cacheAudioFileOrNotifyError(
        desiredFilename: String,
        audioUrl: String,
        videoMetadata: VideoMetadata,
        audioFormat: Formats,
        trimRange: TrimRange
    ): CachingResult {
        val result = downloadMediaFileOrNotifyError(desiredFilename, audioUrl, isAudio = true)

        if (result !is CachingResult.DownloadResult.Success)
            return result

        return when (val audioConversionResult =
            (result.file as MediaFile.VideoFile).convertToAudioFileAndSetTagsAsync(
                context = this,
                videoMetadata = videoMetadata,
                audioFormat = audioFormat,
                trimRange = trimRange
            ).await()
        ) {
            null -> {
                onCashingError(errorStatus = DownloadingStatus.ERR)
                CachingResult.ConversionError
            }

            else -> CachingResult.DownloadResult.Success(audioConversionResult)
        }
    }

    /**
     * Cashes either with [cacheAudioFileOrNotifyError] if [videoUrl] is null,
     * or with [cacheVideoFileOrNotifyError] otherwise
     * @param desiredFilename filename chosen by the user
     * @param audioUrl url to the audio mp4 file
     * @param videoUrl url to the video mp4 file
     * @param videoMetadata metadata to set as tags
     * @param format required media file format
     * @return [CachingResult.DownloadResult.Success] with file if conversion was successful,
     * [CachingResult.DownloadResult.Error] with [HttpStatusCode] if error has occurred,
     * [CachingResult.DownloadResult.Canceled] if caching was canceled by the user,
     * [CachingResult.ConversionError] if conversion to .aac audio file has failed
     */

    private suspend inline fun cacheMediaFileOrNotifyError(
        desiredFilename: String,
        audioUrl: String,
        videoUrl: String?,
        videoMetadata: VideoMetadata,
        format: Formats,
        trimRange: TrimRange
    ) = when (videoUrl) {
        null -> cacheAudioFileOrNotifyError(
            desiredFilename,
            audioUrl,
            videoMetadata,
            format,
            trimRange
        )

        else -> cacheVideoFileOrNotifyError(
            desiredFilename,
            audioUrl,
            videoUrl,
            videoMetadata,
        )
    }

    /** Runs loop that caches files from the [videoCashQueue] */

    private suspend inline fun launchCashing() {
        while (true) {
            Log.d(TAG, "QUEUE ${videoCashQueue.size}")

            while (videoCashQueue.isEmpty()) {
                isVideoCashQueueEmptyCondVar.wait()
                Log.d(TAG, "Video Cash Queue Cond Var Awake")
            }

            while (cachingStatusState.value == DownloadingStatus.DOWNLOADING) {
                isVideoCashingCondVar.wait()
                Log.d(TAG, "Is Video Cashing Cond Var Awake")
            }

            videoCashQueue.poll()?.let { (url, desiredFilename, format, trimRange) ->
                Log.d(TAG, "Prepare for caching")
                videoCashProgressState.update { DownloadingProgress(0L, 0L) }
                cachingStatusState.update { DownloadingStatus.DOWNLOADING }
                videoCashQueueLenState.update { videoCashQueue.size }

                extractMediaFilesAndStartCaching(
                    ytUrl = url,
                    desiredFilename = desiredFilename,
                    format = format,
                    trimRange = trimRange
                )
            }
        }
    }

    private fun resetCashingJobIfCanceled() {
        Log.d(TAG, "Cashing loop status: ${cachingLoopJob?.isActive}")

        cachingStatusState.update {
            when (it) {
                DownloadingStatus.CANCELED -> DownloadingStatus.NONE
                else -> it
            }
        }

        if (cachingLoopJob?.isActive != true)
            cachingLoopJob = scope.launch(Dispatchers.IO) { launchCashing() }
    }

    internal suspend inline fun mCancelCurVideoCashing() {
        cachingStatusState.update { DownloadingStatus.CANCELED }
        isVideoCashingCondVar.notify()

        Log.d(TAG, "File is deleted ${curVideoCashFile?.delete()}")
        Log.d(TAG, "Cashing is canceled")
        clearVideoCacheStates()
    }

    internal suspend inline fun mCancelAllVideosCashing() {
        videoCashQueue.clear()
        videoCashQueueLenState.update { 0 }
        mCancelCurVideoCashing()
    }

    private fun clearVideoCacheStates() {
        curVideoCashFile = null
    }

    // --------------------- Cashing Response Handling ---------------------

    /** Sends broadcasts according to the [cachingResult] */

    private fun onVideoCacheStatusReceived(cachingResult: CachingResult) {
        Log.d(TAG, "Caching result handling")
        clearVideoCacheStates()

        when (cachingResult) {
            CachingResult.ConversionError -> onAudioConversionError()

            CachingResult.DownloadResult.Canceled -> onVideoCashCanceled()

            is CachingResult.DownloadResult.Error -> onDownloadError(
                code = cachingResult.statusCode.value,
                description = cachingResult.statusCode.description
            )

            is CachingResult.DownloadResult.Success -> onVideoCashSuccessful()

            CachingResult.DownloadResult.FileCreationError -> onFileCreationError()

            CachingResult.DownloadResult.ConnectionLostError -> onConnectionLostError()
        }

        Log.d(TAG, "Cashing result $cachingResult handled")
    }

    private fun onVideoCashSuccessful() = sendBroadcast(
        Intent(MainActivity.Broadcast_VIDEO_CASH_COMPLETED)
            .putExtra(VIDEO_CASH_STATUS_ARG, VideoCacheResponse.Success)
    )

    private fun onDownloadError(code: Int, description: String) {
        videoCashErrorState.update { code to description }

        sendBroadcast(
            Intent(MainActivity.Broadcast_VIDEO_CASH_COMPLETED)
                .putExtra(VIDEO_CASH_STATUS_ARG, VideoCacheResponse.Error(code, description))
        )
    }

    private fun onVideoCashCanceled() = sendBroadcast(
        Intent(MainActivity.Broadcast_VIDEO_CASH_COMPLETED)
            .putExtra(VIDEO_CASH_STATUS_ARG, VideoCacheResponse.Canceled)
    )

    private fun onAudioConversionError() = sendBroadcast(
        Intent(MainActivity.Broadcast_VIDEO_CASH_COMPLETED)
            .putExtra(VIDEO_CASH_STATUS_ARG, VideoCacheResponse.AudioConversionError)
    )

    private fun onFileCreationError() = sendBroadcast(
        Intent(MainActivity.Broadcast_VIDEO_CASH_COMPLETED)
            .putExtra(VIDEO_CASH_STATUS_ARG, VideoCacheResponse.FileCreationError)
    )

    private fun onConnectionLostError() = sendBroadcast(
        Intent(MainActivity.Broadcast_VIDEO_CASH_COMPLETED)
            .putExtra(VIDEO_CASH_STATUS_ARG, VideoCacheResponse.ConnectionLostError)
    )

    private fun onLiveStreamCaching() = sendBroadcast(
        Intent(MainActivity.Broadcast_VIDEO_CASH_COMPLETED)
            .putExtra(VIDEO_CASH_STATUS_ARG, VideoCacheResponse.LiveStreamNotAllowed)
    )

    private suspend inline fun onCashingError(errorStatus: DownloadingStatus) {
        cachingStatusState.update { errorStatus }
        isVideoCashingCondVar.notify()

        Log.d(TAG, "File is deleted ${curVideoCashFile?.delete()}")
        Log.d(TAG, "Cashing was interrupted by an error")
        clearVideoCacheStates()
    }

    // --------------------------- Notification Actions ---------------------------

    @Suppress("DEPRECATION")
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

    @Suppress("DEPRECATION")
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

    @Suppress("DEPRECATION")
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

    private inline val cachedNotificationBuilder
        get() = finishedNotificationBuilder(R.string.video_cached)

    private inline val canceledNotificationBuilder
        get() = finishedNotificationBuilder(R.string.video_canceled)

    private fun getErrorNotificationBuilder(code: Int, description: String) =
        finishedNotificationBuilder("${resources.getString(R.string.error)} $code: $description")

    private inline val connectionLostNotificationBuilder
        get() = finishedNotificationBuilder(R.string.connection_lost)

    private data class CashingNotificationData(
        val cachingState: DownloadingStatus,
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

    private suspend fun startNotificationObserving() =
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            combine(
                cachingStatusState,
                curVideoMetadataState,
                videoCashQueueLenState,
                videoCashProgressState,
                videoCashErrorState
            ) { cachingState, videoMetadata, videoCashQueueLen,
                (downloadedBytes, totalBytes), (errorCode, errorDescription) ->
                CashingNotificationData(
                    cachingState,
                    videoMetadata ?: VideoMetadata(),
                    videoCashQueueLen,
                    downloadedBytes,
                    totalBytes,
                    errorCode,
                    errorDescription
                )
            }.collectLatest { (cachingState, videoMetadata, videoCashQueueLen,
                                  downloadedBytes, totalBytes, errorCode, errorDescription) ->
                updateNotification(
                    cachingState,
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
        cachedNotificationBuilder.build()
    )

    private fun showCanceledNotification() = notificationManager.notify(
        NOTIFICATION_ID,
        canceledNotificationBuilder.build()
    )

    private fun showErrorNotification(code: Int, description: String) = notificationManager.notify(
        NOTIFICATION_ID,
        getErrorNotificationBuilder(code, description).build()
    )

    private fun showConnectionLostNotification() = notificationManager.notify(
        NOTIFICATION_ID,
        connectionLostNotificationBuilder.build()
    )

    private fun updateNotification(
        cachingState: DownloadingStatus,
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

        else -> when (cachingState) {
            DownloadingStatus.DOWNLOADING -> updateCashingNotification(
                videoMetadata,
                videoCashQueueLen,
                downloadedBytes,
                totalBytes
            )

            else -> {
                when (cachingState) {
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

                    DownloadingStatus.CONNECT_LOST -> {
                        Log.d(TAG, "Connection Lost Notification")
                        showConnectionLostNotification()
                    }

                    else -> Unit
                }

                detachNotification()
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun detachNotification() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ->
            stopForeground(STOP_FOREGROUND_DETACH)

        else -> stopForeground(true)
    }
}