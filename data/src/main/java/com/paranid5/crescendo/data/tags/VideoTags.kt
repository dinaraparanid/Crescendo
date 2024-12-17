package com.paranid5.crescendo.data.tags

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import arrow.core.Either
import com.paranid5.crescendo.core.common.caching.Formats
import com.paranid5.crescendo.core.common.caching.mimeType
import com.paranid5.crescendo.core.common.media.MimeType
import com.paranid5.crescendo.core.common.uri.Path
import com.paranid5.crescendo.core.media.files.MediaFile
import com.paranid5.crescendo.core.media.media_scanner.sendScanFile
import com.paranid5.crescendo.data.tags.MediaStore.insertMediaFileToMediaStore
import com.paranid5.crescendo.domain.metadata.model.VideoMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.mp4.Mp4FieldKey
import org.jaudiotagger.tag.mp4.Mp4Tag

internal object VideoTags {
    suspend fun setVideoTags(
        context: Context,
        videoFile: MediaFile.VideoFile,
        metadata: VideoMetadata,
    ) = coroutineScope {
        val externalContentUri = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

            else -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        val mimeType = Formats.MP4.mimeType
        val absoluteFilePath = videoFile.path
        val mediaDirectory = Path(Environment.DIRECTORY_MOVIES)

        withContext(Dispatchers.IO) {
            context.insertMediaFileToMediaStore(
                externalContentUri = externalContentUri,
                absoluteFilePath = absoluteFilePath,
                relativeFilePath = mediaDirectory,
                metadata = metadata,
                mimeType = mimeType,
            )

            setVideoTagsToFileCatching(videoFile, metadata)
            context.sendScanFile(absoluteFilePath)
        }
    }

    private fun setVideoTagsToFile(file: MediaFile.VideoFile, metadata: VideoMetadata) =
        AudioFileIO.read(file).run {
            tagOrCreateAndSetDefault.let { it as Mp4Tag }.run {
                setField(Mp4FieldKey.TITLE, metadata.title)
                setField(Mp4FieldKey.ARTIST, metadata.author)
                commit()
            }
        }

    private fun setVideoTagsToFileCatching(file: MediaFile.VideoFile, metadata: VideoMetadata) =
        Either.catch { setVideoTagsToFile(file, metadata) }

    fun ContentValues(
        absoluteFilePath: Path,
        relativeFilePath: Path,
        metadata: VideoMetadata,
        mimeType: MimeType,
    ) = ContentValues().apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            put(MediaStore.MediaColumns.IS_PENDING, 1)

        put(MediaStore.MediaColumns.TITLE, metadata.title)
        put(MediaStore.MediaColumns.ARTIST, metadata.author)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            put(MediaStore.MediaColumns.AUTHOR, metadata.author)

        put(MediaStore.MediaColumns.DURATION, metadata.durationMillis)
        put(MediaStore.MediaColumns.DISPLAY_NAME, metadata.title)
        put(MediaStore.MediaColumns.MIME_TYPE, mimeType.value)

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
                put(MediaStore.MediaColumns.RELATIVE_PATH, relativeFilePath.toString())

            else -> put(MediaStore.MediaColumns.DATA, absoluteFilePath.toString())
        }
    }
}
