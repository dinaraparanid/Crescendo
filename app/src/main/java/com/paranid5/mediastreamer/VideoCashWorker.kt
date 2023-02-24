package com.paranid5.mediastreamer

import android.app.*
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.work.*
import com.bumptech.glide.Glide
import com.paranid5.mediastreamer.data.VideoMetadata
import com.paranid5.mediastreamer.presentation.MainActivity
import com.paranid5.mediastreamer.utils.extensions.byteData
import io.ktor.client.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.images.ArtworkFactory
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import java.io.File
import java.net.URI

@Deprecated("Use VideoCashService instead")
class VideoCashWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams), KoinComponent {
    companion object : KoinComponent {
        private const val URL_ARG = "url"
        private const val SAVE_AS_VIDEO_ARG = "save_as_video"

        private const val NOTIFICATION_ID = 102
        private const val VIDEO_CASH_CHANNEL_ID = "video_cash_channel"

        private val TAG = VideoCashWorker::class.simpleName!!

        fun launch(url: String, saveAsVideo: Boolean) =
            WorkManager.getInstance(get<MainApplication>())
                .enqueue(
                    OneTimeWorkRequestBuilder<VideoCashWorker>()
                        .setInputData(
                            workDataOf(
                                URL_ARG to url,
                                SAVE_AS_VIDEO_ARG to saveAsVideo
                            )
                        )
                        .setConstraints(
                            Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .setRequiresStorageNotLow(true)
                                .build()
                        )
                        .build()
                )
    }

    private lateinit var url: String
    private lateinit var videoTitle: String

    private val ktorClient by inject<HttpClient>()

    private inline val String.imageBinaryData: kotlin.Result<ByteArray>
        get() = kotlin.runCatching {
            Glide.with(applicationContext)
                .asBitmap()
                .load(this)
                .submit()
                .get()
                .byteData
        }

    // ---------------------- Audio File ----------------------

    private fun File.setAudioTags(videoMetadata: VideoMetadata) =
        AudioFileIO.read(this).apply {
            tagOrCreateAndSetDefault.apply {
                setField(FieldKey.TITLE, videoMetadata.title)
                setField(FieldKey.ARTIST, videoMetadata.author)

                videoMetadata
                    .covers
                    .asSequence()
                    .map { it.imageBinaryData }
                    .firstOrNull { it.isSuccess }
                    ?.getOrNull()
                    ?.let { byteData ->
                        addField(
                            ArtworkFactory
                                .createArtworkFromFile(file)
                                .apply { binaryData = byteData }
                        )
                    }

                commit()
            }
        }

    private fun File.insertToMediaStoreAsAudio(videoMetadata: VideoMetadata, relativePath: String) {
        val uri = applicationContext.contentResolver.insert(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            ContentValues().apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    put(MediaStore.Audio.Media.IS_PENDING, 0)

                put(MediaStore.Audio.Media.TITLE, videoMetadata.title)
                put(MediaStore.Audio.Media.ARTIST, videoMetadata.author)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                    put(MediaStore.Audio.Media.AUTHOR, videoMetadata.author)

                put(MediaStore.Audio.Media.DURATION, videoMetadata.lenInMillis)
                put(MediaStore.Audio.Media.DATA, absolutePath)
                put(MediaStore.Audio.Media.DISPLAY_NAME, videoMetadata.title)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    put(MediaStore.Audio.Media.RELATIVE_PATH, relativePath)
            }
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && uri != null)
            applicationContext.contentResolver.update(
                uri, ContentValues().apply { put(MediaStore.Audio.Media.IS_PENDING, 0) },
                null, null
            )
    }

    private suspend fun createAudioFile(
        url: String,
        videoMetadata: VideoMetadata
    ) = coroutineScope {
        val relativePath = URI(url).path

        File("${Environment.DIRECTORY_MUSIC}/$relativePath").apply {
            createNewFile()
            launch(Dispatchers.IO) {
                setAudioTags(videoMetadata)
                insertToMediaStoreAsAudio(videoMetadata, relativePath)
            }
        }
    }

    // ---------------------- Video File ----------------------

    private fun File.insertToMediaStoreAsVideo(videoMetadata: VideoMetadata, relativePath: String) {
        val uri = applicationContext.contentResolver.insert(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            ContentValues().apply {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q)
                    put(MediaStore.Video.Media.IS_PENDING, 0)

                put(MediaStore.Video.Media.TITLE, videoMetadata.title)
                put(MediaStore.Video.Media.ARTIST, videoMetadata.author)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                    put(MediaStore.Audio.Media.AUTHOR, videoMetadata.author)

                put(MediaStore.Audio.Media.DURATION, videoMetadata.lenInMillis)
                put(MediaStore.Audio.Media.DATA, absolutePath)
                put(MediaStore.Audio.Media.DISPLAY_NAME, videoMetadata.title)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    put(MediaStore.Video.Media.RELATIVE_PATH, relativePath)
            }
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && uri != null)
            applicationContext.contentResolver.update(
                uri, ContentValues().apply { put(MediaStore.Video.Media.IS_PENDING, 0) },
                null, null
            )
    }

    private suspend fun createVideoFile(
        url: String,
        videoMetadata: VideoMetadata
    ) = coroutineScope {
        val relativePath = URI(url).path

        File("${Environment.DIRECTORY_MOVIES}/$relativePath").apply {
            createNewFile()
            launch(Dispatchers.IO) {
                setAudioTags(videoMetadata)
                insertToMediaStoreAsVideo(videoMetadata, relativePath)
            }
        }
    }

    private fun cashFile(isSaveAsVideo: Boolean) =
        YoutubeUrlExtractor(context = applicationContext) { audioUrl, videoUrl, videoMeta ->
            val videoMetadata = videoMeta?.let(::VideoMetadata) ?: VideoMetadata()

            when {
                isSaveAsVideo -> ktorClient.downloadFile(
                    fileUrl = videoUrl,
                    storeFile = createVideoFile(
                        url = audioUrl,
                        videoMetadata = videoMetadata
                    )
                )

                else -> ktorClient.downloadFile(
                    fileUrl = audioUrl,
                    storeFile = createAudioFile(
                        url = audioUrl,
                        videoMetadata = videoMetadata
                    )
                )
            }
        }

    override suspend fun doWork() = withContext(Dispatchers.IO) {
        url = inputData.getString(URL_ARG)!!

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel()

        showNotification(isCashing = true)
        cashFile(isSaveAsVideo = inputData.getBoolean(SAVE_AS_VIDEO_ARG, false))
        showNotification(isCashing = false)
        Result.success()
    }

    // ---------------------- Notifications ----------------------

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() =
        (applicationContext.getSystemService(Service.NOTIFICATION_SERVICE)!! as NotificationManager)
            .createNotificationChannel(
                NotificationChannel(
                    VIDEO_CASH_CHANNEL_ID,
                    "VideoCash",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                    setSound(null, null)
                    enableVibration(true)
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