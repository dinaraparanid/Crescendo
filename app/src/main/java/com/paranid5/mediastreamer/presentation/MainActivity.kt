package com.paranid5.mediastreamer.presentation

import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.CompositionLocalProvider
import androidx.media3.common.MimeTypes
import androidx.navigation.compose.rememberNavController
import com.paranid5.mediastreamer.data.VideoMetadata
import com.paranid5.mediastreamer.domain.media_scanner.scanNextFile
import com.paranid5.mediastreamer.presentation.composition_locals.LocalStreamState
import com.paranid5.mediastreamer.presentation.composition_locals.StreamState
import com.paranid5.mediastreamer.presentation.composition_locals.StreamStates
import com.paranid5.mediastreamer.presentation.ui.App
import com.paranid5.mediastreamer.presentation.ui.theme.MediaStreamerTheme
import com.paranid5.mediastreamer.utils.extensions.insertMediaFileToMediaStore
import com.paranid5.mediastreamer.utils.extensions.setAudioTagsToFile
import com.paranid5.mediastreamer.utils.extensions.setAudioTagsToFileCatching
import it.sauronsoftware.jave.AudioAttributes
import it.sauronsoftware.jave.Encoder
import it.sauronsoftware.jave.EncodingAttributes
import kotlinx.coroutines.*
import java.io.File

class MainActivity : ComponentActivity() {
    companion object {
        private const val BROADCASTS_LOCATION = "com.paranid5.mediastreamer.presentation"
        const val Broadcast_SET_TAGS = "$BROADCASTS_LOCATION.SET_TAGS"
        private const val Broadcast_RETRY_SET_TAGS = "$BROADCASTS_LOCATION.RETRY_SET_TAGS"

        const val FILE_PATH_ARG = "file_path"
        const val IS_VIDEO_ARG = "is_video"
        const val VIDEO_METADATA_ARG = "video_metadata"

        internal inline val Intent.mFilePathArg
            get() = getStringExtra(FILE_PATH_ARG)!!

        internal inline val Intent.mIsVideoArg
            get() = getBooleanExtra(IS_VIDEO_ARG, false)

        internal inline val Intent.mVideoMetadataArg
            get() = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                    getParcelableExtra(VIDEO_METADATA_ARG, VideoMetadata::class.java)
                else ->
                    getParcelableExtra(VIDEO_METADATA_ARG)
            }!!
    }

    private val setTagsIntentResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result != null && result.resultCode == Activity.RESULT_OK)
            sendBroadcast(Intent(Broadcast_RETRY_SET_TAGS))
    }

    private val tagsSetterReceiver = object :
        BroadcastReceiver(), CoroutineScope by CoroutineScope(Dispatchers.IO) {
        private val videoDataQueue = ArrayDeque<Pair<String, VideoMetadata>>()
        private val encoder by lazy(::Encoder)

        private fun isAllowedToModify(uri: Uri) =
            try {
                contentResolver.openFileDescriptor(uri, "w", null)?.close()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    applicationContext.contentResolver.update(
                        uri, ContentValues().apply { put(MediaStore.Audio.Media.IS_PENDING, 0) },
                        null, null
                    )
                true
            } catch (securityException: SecurityException) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val recoverableSecurityException =
                        securityException as? RecoverableSecurityException
                            ?: throw RuntimeException(securityException.message, securityException)

                    recoverableSecurityException
                        .userAction
                        .actionIntent
                        .intentSender
                        .let { intentSender ->
                            setTagsIntentResultLauncher.launch(
                                IntentSenderRequest.Builder(intentSender).build()
                            )
                        }
                }
                false
            }

        private suspend inline fun setTags(
            externalContentUri: Uri,
            file: File,
            mediaDirectory: String,
            videoMetadata: VideoMetadata,
            mimeType: String,
        ) = coroutineScope {
            val absoluteFilePath = file.absolutePath

            withContext(Dispatchers.IO) {
                val isAllowedToModify = insertMediaFileToMediaStore(
                    externalContentUri,
                    absoluteFilePath,
                    mediaDirectory,
                    videoMetadata,
                    mimeType
                )?.let { isAllowedToModify(uri = it) } ?: false

                when {
                    isAllowedToModify -> {
                        setAudioTagsToFileCatching(file, videoMetadata)
                        scanNextFile(absoluteFilePath)
                    }

                    else -> videoDataQueue.add(absoluteFilePath to videoMetadata)
                }
            }
        }

        /*private fun encodeToMp3(wavFile: File): File {
            val mp3File = File(
                wavFile.absolutePath.replace(
                    ".${Formats.WAV.extension}",
                    ".${Formats.MP3.extension}"
                )
            ).also(File::createNewFile)

            val attributes = EncodingAttributes().apply {
                setAudioAttributes(
                    AudioAttributes().apply {
                        setCodec(AudioAttributes.DIRECT_STREAM_COPY)
                        setChannels(2)
                    }
                )

                setFormat(Formats.MP3.extension)
            }

            encoder.encode(wavFile, mp3File, attributes)
            return mp3File
        }*/

        /*private fun encodeToMp3Catching(wavFile: File) = kotlin.runCatching {
            encodeToMp3(wavFile)
        }*/

        private suspend inline fun getDataAndSetTags(intent: Intent) = coroutineScope {
            val filePath = intent.mFilePathArg
            val isVideo = intent.mIsVideoArg
            val videoMetadata = intent.mVideoMetadataArg
            val file = File(filePath)

            val externalContentUri = when {
                isVideo -> when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
                        MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                    else ->
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                }

                else -> when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
                        MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                    else ->
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
            }

            val mediaDirectory = when {
                isVideo -> Environment.DIRECTORY_MOVIES
                else -> Environment.DIRECTORY_MUSIC
            }

            val mimeType = when {
                isVideo -> MimeTypes.VIDEO_MP4
                else -> MimeTypes.AUDIO_MPEG
            }

            launch {
                setTags(externalContentUri, file, mediaDirectory, videoMetadata, mimeType)
            }
        }

        private fun retrySetTags() {
            val (filePath, videoMetadata) = videoDataQueue.removeFirst()
            setAudioTagsToFile(File(filePath), videoMetadata)
            scanNextFile(filePath)
        }

        override fun onReceive(context: Context?, intent: Intent) {
            when (intent.action) {
                Broadcast_SET_TAGS -> launch { getDataAndSetTags(intent) }
                Broadcast_RETRY_SET_TAGS -> retrySetTags()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(tagsSetterReceiver, IntentFilter(Broadcast_SET_TAGS))

        setContent {
            MediaStreamerTheme {
                val mainNavController = NavHostController(
                    value = rememberNavController(),
                    initialRoute = Screens.StreamScreen.Searching.title
                )

                CompositionLocalProvider(
                    LocalNavController provides mainNavController,
                    LocalStreamState provides StreamState(StreamStates.SEARCHING),
                    LocalActivity provides this
                ) {
                    App()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(tagsSetterReceiver)
    }
}