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
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.arthenica.mobileffmpeg.FFmpeg
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
import com.paranid5.mediastreamer.domain.utils.extensions.setVideoTagsToFileCatching
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
        const val FORMAT_ARG = "format"

        private val TAG = VideoCashService::class.simpleName!!

        internal inline val Intent.mVideoCashDataArg
            get() = VideoCashData(
                url = getStringExtra(URL_ARG)!!,
                desiredFilename = getStringExtra(FILENAME_ARG)!!,
                format = getParcelableExtra(FORMAT_ARG, Formats::class.java)!!
            )
    }

    private sealed class Actions(val requestCode: Int, val playbackAction: String) {
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

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private var cashingLoopJob: Job? = null

    private val videoCashQueue: Queue<VideoCashData> = ConcurrentLinkedQueue()
    private val videoCashQueueLenState = MutableStateFlow(0)

    private val videoCashProgressState = MutableStateFlow(0L to 0L)
    private val videoCashErrorState = MutableStateFlow(0 to "")

    private val isVideoCashingCondVar = AsyncCondVar()
    private val isVideoCashQueueEmptyCondVar = AsyncCondVar()

    private var curVideoCashFile: File? = null
    private val curVideoMetadataState = MutableStateFlow<VideoMetadata?>(null)
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
                        cashingResult = mCashMediaFile(
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
        isVideoCashQueueEmptyCondVar.notify()
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

    /**
     * Runs loop that observers all notification related states
     * and updates notification when something has changed
     */

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

    // --------------------- File Creation ---------------------

    private fun getFullMediaDirectory(mediaDirectory: String) =
        Environment
            .getExternalStoragePublicDirectory(mediaDirectory)
            .absolutePath

    private fun getInitialMediaDirectory(isAudio: Boolean) = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> Environment.DIRECTORY_MOVIES
        isAudio -> Environment.DIRECTORY_MUSIC
        else -> Environment.DIRECTORY_MOVIES
    }

    /**
     * Creates new media file by given parameters.
     * If file with the same filename and extension already exists,
     * will try to create `[filename](try_number).[ext]` until such file not found
     *
     * @param mediaDirectory directory to put file
     * (either [Environment.DIRECTORY_MUSIC] for audio or [Environment.DIRECTORY_MOVIES] for video)
     * @param filename desired filename
     * @param ext file extension
     * @return created empty media file
     */

    private fun createMediaFile(mediaDirectory: String, filename: String, ext: String) =
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

    private fun createMediaFileCatching(mediaDirectory: String, filename: String, ext: String) =
        kotlin.runCatching { createMediaFile(mediaDirectory, filename, ext) }

    // --------------------- Audio File Conversion ---------------------

    /**
     * Converts video file to an audio file with ffmpeg
     * @param videoFile video file to convert
     * @param audioFormat audio file format
     * @param ffmpegCmd ffmpeg cmd command to execute
     * @return file if conversion was successful, otherwise null
     */

    private inline fun convertToAudioFile(
        videoFile: File,
        audioFormat: Formats,
        ffmpegCmd: (File) -> String
    ): File? {
        val ext = when (audioFormat) {
            Formats.MP3 -> "mp3"
            Formats.AAC -> "aac"
            Formats.WAV -> "wav"
            Formats.MP4 -> throw IllegalArgumentException("MP4 passed as an audio format")
        }

        val newFile = createMediaFileCatching(
            mediaDirectory = Environment.DIRECTORY_MUSIC,
            filename = videoFile.nameWithoutExtension,
            ext = ext
        ).getOrNull() ?: return null

        Log.d(TAG, "Converting to file: ${newFile.absolutePath}")

        val convertRes = FFmpeg.execute(ffmpegCmd(newFile))

        if (convertRes == 0) {
            videoFile.delete()
            return newFile
        }

        newFile.delete()
        return null
    }

    private fun convertToMP3(videoFile: File) = convertToAudioFile(
        videoFile = videoFile,
        audioFormat = Formats.MP3
    ) { newFile ->
        "-y -i ${videoFile.absolutePath} -vn -acodec libmp3lame -qscale:a 2 ${newFile.absolutePath}"
    }

    private fun convertToWAV(videoFile: File) =
        convertToAudioFile(
            videoFile = videoFile,
            audioFormat = Formats.WAV
        ) { newFile ->
            "-y -i ${videoFile.absolutePath} -vn -acodec pcm_s16le " +
                    "-ar 44100 ${newFile.absolutePath}"
        }

    private fun convertToAAC(videoFile: File) =
        convertToAudioFile(
            videoFile = videoFile,
            audioFormat = Formats.AAC
        ) { newFile ->
            "-y -i ${videoFile.absolutePath} -vn -c:a aac -b:a 256k ${newFile.absolutePath}"
        }

    /**
     * Converts video file to an audio file with ffmpeg according to the [audioFormat].
     * Adds new file to the [MediaStore] with provided [videoMetadata] tags.
     * For [Formats.MP3] sets tags (with cover) after conversion.
     * Finally, scans file with [android.media.MediaScannerConnection]
     *
     * @param videoFile video file to convert
     * @param videoMetadata metadata to set
     * @param audioFormat audio file format
     * @return file if conversion was successful, otherwise null
     */

    private suspend inline fun convertToAudioFileAndSetTags(
        videoFile: File,
        videoMetadata: VideoMetadata,
        audioFormat: Formats
    ): File? {
        val audioFile = withContext(Dispatchers.IO) {
            Log.d(TAG, "Audio conversion to $audioFormat")

            when (audioFormat) {
                Formats.MP3 -> convertToMP3(videoFile)
                Formats.WAV -> convertToWAV(videoFile)
                Formats.AAC -> convertToAAC(videoFile)
                Formats.MP4 -> throw IllegalArgumentException("MP4 passed as an audio format")
            }
        } ?: return null

        val externalContentUri = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
                MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

            else -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val mediaDirectory = Environment.DIRECTORY_MUSIC

        val mimeType = when (audioFormat) {
            Formats.MP3 -> "audio/mpeg"
            Formats.AAC -> "audio/aac"
            else -> "audio/x-wav"
        }

        val absoluteFilePath = audioFile.absolutePath

        withContext(Dispatchers.IO) {
            insertMediaFileToMediaStore(
                externalContentUri,
                absoluteFilePath,
                mediaDirectory,
                videoMetadata,
                mimeType
            )

            if (audioFormat == Formats.MP3)
                setAudioTagsToFileCatching(audioFile, videoMetadata)

            scanNextFile(absoluteFilePath)
        }

        return audioFile
    }

    private suspend inline fun setVideoTags(videoFile: File, videoMetadata: VideoMetadata) {
        val externalContentUri = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

            else -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        val mediaDirectory = Environment.DIRECTORY_MOVIES
        val mimeType = "video/${videoFile.extension}"
        val absoluteFilePath = videoFile.absolutePath

        withContext(Dispatchers.IO) {
            insertMediaFileToMediaStore(
                externalContentUri,
                absoluteFilePath,
                mediaDirectory,
                videoMetadata,
                mimeType
            )

            setVideoTagsToFileCatching(videoFile, videoMetadata)
            scanNextFile(absoluteFilePath)
        }
    }

    // --------------------- Media File Cashing ---------------------

    /**
     * Downloads initial mp4 file by the url according to the acquired itag
     * @param desiredFilename filename chosen by the user
     * @param mediaUrl url to the initial mp4 file
     * @param isAudio is final file required to be in audio format
     * @return [CashingResult.DownloadResult.Success] with file if conversion was successful,
     * [CashingResult.DownloadResult.Error] with [HttpStatusCode] if error has occurred,
     * [CashingResult.DownloadResult.Canceled] if cashing was canceled by the user
     */

    private suspend inline fun downloadMediaFile(
        desiredFilename: String,
        mediaUrl: String,
        isAudio: Boolean
    ): CashingResult.DownloadResult {
        val fileExt = ktorClient.getFileExt(mediaUrl)

        val storeFileRes = createMediaFileCatching(
            mediaDirectory = getInitialMediaDirectory(isAudio),
            filename = desiredFilename.replace(Regex("\\W+"), "_"),
            ext = fileExt
        )

        if (storeFileRes.isFailure) {
            storeFileRes.exceptionOrNull()?.printStackTrace()
            cashingStatusState.update { CashingStatus.ERR }
            isVideoCashingCondVar.notify()
            return CashingResult.DownloadResult.Error(HttpStatusCode.BadRequest)
        }

        curVideoCashFile = storeFileRes.getOrNull()!!
        Log.d(TAG, "Launching file download")

        val statusCode = ktorClient.downloadFile(
            fileUrl = mediaUrl,
            storeFile = curVideoCashFile!!,
            progressState = videoCashProgressState,
            cashingStatusState = cashingStatusState,
        )

        isVideoCashingCondVar.notify()
        Log.d(TAG, "Status code is received")

        return statusCode
            ?.takeIf { it.isSuccess() }
            ?.let { CashingResult.DownloadResult.Success(curVideoCashFile!!) }
            ?: statusCode?.let(CashingResult.DownloadResult::Error)
            ?: CashingResult.DownloadResult.Canceled
    }

    /**
     * Downloads both video and audio mp4 files and combines them into single mp4 file.
     * @param desiredFilename filename chosen by the user
     * @param audioUrl url to the audio mp4 file
     * @param videoUrl url to the video mp4 file
     * @param videoMetadata metadata to set as tags
     * @return [CashingResult.DownloadResult.Success] with file if conversion was successful,
     * [CashingResult.DownloadResult.Error] with [HttpStatusCode] if error has occurred,
     * [CashingResult.DownloadResult.Canceled] if cashing was canceled by the user,
     * [CashingResult.AudioConversionError] if conversion to .aac audio file has failed
     */

    private suspend inline fun cashVideoFile(
        desiredFilename: String,
        audioUrl: String,
        videoUrl: String,
        videoMetadata: VideoMetadata
    ): CashingResult {
        val videoResult = downloadMediaFile(desiredFilename, videoUrl, isAudio = false)

        if (videoResult !is CashingResult.DownloadResult.Success)
            return videoResult

        val audioResult = downloadMediaFile(desiredFilename, audioUrl, isAudio = true)

        if (audioResult !is CashingResult.DownloadResult.Success)
            return audioResult

        val audioConversionResult = convertToAAC(audioResult.file)
            ?: return CashingResult.AudioConversionError

        // TODO: combine with an audio file

        setVideoTags(
            videoFile = videoResult.file,
            videoMetadata = videoMetadata
        )

        return CashingResult.DownloadResult.Success(videoResult.file)
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
     * [CashingResult.AudioConversionError] if conversion to .aac audio file has failed
     */

    private suspend inline fun cashAudioFile(
        desiredFilename: String,
        audioUrl: String,
        videoMetadata: VideoMetadata,
        audioFormat: Formats
    ): CashingResult {
        val result = downloadMediaFile(desiredFilename, audioUrl, isAudio = true)

        if (result !is CashingResult.DownloadResult.Success)
            return result

        return when (val audioConversionResult = convertToAudioFileAndSetTags(
            videoFile = result.file,
            videoMetadata = videoMetadata,
            audioFormat = audioFormat
        )) {
            null -> CashingResult.DownloadResult.Canceled
            else -> CashingResult.DownloadResult.Success(audioConversionResult)
        }
    }

    /**
     * Cashes either with [cashAudioFile] if [videoUrl] is null,
     * or with [cashVideoFile] otherwise
     * @param desiredFilename filename chosen by the user
     * @param audioUrl url to the audio mp4 file
     * @param videoUrl url to the video mp4 file
     * @param videoMetadata metadata to set as tags
     * @param format required media file format
     * @return [CashingResult.DownloadResult.Success] with file if conversion was successful,
     * [CashingResult.DownloadResult.Error] with [HttpStatusCode] if error has occurred,
     * [CashingResult.DownloadResult.Canceled] if cashing was canceled by the user,
     * [CashingResult.AudioConversionError] if conversion to .aac audio file has failed
     */

    internal suspend inline fun mCashMediaFile(
        desiredFilename: String,
        audioUrl: String,
        videoUrl: String?,
        videoMetadata: VideoMetadata,
        format: Formats
    ): CashingResult {
        val isAudio = videoUrl == null

        return when {
            isAudio -> cashAudioFile(
                desiredFilename = desiredFilename,
                audioUrl = audioUrl,
                videoMetadata = videoMetadata,
                audioFormat = format
            )

            else -> cashVideoFile(
                desiredFilename = desiredFilename,
                audioUrl = audioUrl,
                videoUrl = videoUrl!!,
                videoMetadata = videoMetadata
            )
        }
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
            CashingResult.AudioConversionError -> onAudioConversionError()

            CashingResult.DownloadResult.Canceled -> onVideoCashCanceled()

            is CashingResult.DownloadResult.Error -> onDownloadError(
                code = cashingResult.statusCode.value,
                description = cashingResult.statusCode.description
            )

            is CashingResult.DownloadResult.Success -> onVideoCashSuccessful()
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

    /** Runs loop that cashes files from the [videoCashQueue] */

    private suspend inline fun launchCashing() {
        while (true) {
            Log.d(TAG, "QUEUE ${videoCashQueue.size}")

            while (videoCashQueue.isEmpty()) {
                isVideoCashQueueEmptyCondVar.wait()
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
            CashingStatus.CASHING -> updateCashingNotification(
                videoMetadata,
                videoCashQueueLen,
                downloadedBytes,
                totalBytes
            )

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