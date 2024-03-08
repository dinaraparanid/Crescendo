package com.paranid5.crescendo.media.tags

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import arrow.core.Either
import com.paranid5.crescendo.core.media.files.MediaFile
import com.paranid5.crescendo.core.media.media_scanner.sendScanFile
import com.paranid5.crescendo.core.common.metadata.VideoMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.mp4.Mp4FieldKey
import org.jaudiotagger.tag.mp4.Mp4Tag

private fun setVideoTagsToFile(file: com.paranid5.crescendo.core.media.files.MediaFile.VideoFile, metadata: com.paranid5.crescendo.core.common.metadata.VideoMetadata) =
    AudioFileIO.read(file).run {
        tagOrCreateAndSetDefault.let { it as Mp4Tag }.run {
            setField(Mp4FieldKey.TITLE, metadata.title)
            setField(Mp4FieldKey.ARTIST, metadata.author)
            commit()
        }
    }

private fun setVideoTagsToFileCatching(file: com.paranid5.crescendo.core.media.files.MediaFile.VideoFile, metadata: com.paranid5.crescendo.core.common.metadata.VideoMetadata) =
    Either.catch { setVideoTagsToFile(file, metadata) }

suspend fun setVideoTagsAsync(
    context: Context,
    videoFile: com.paranid5.crescendo.core.media.files.MediaFile.VideoFile,
    metadata: com.paranid5.crescendo.core.common.metadata.VideoMetadata
) = coroutineScope {
    val externalContentUri = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

        else -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    }

    val mediaDirectory = Environment.DIRECTORY_MOVIES
    val mimeType = "video/${videoFile.extension}"
    val absoluteFilePath = videoFile.absolutePath

    launch(Dispatchers.IO) {
        context.insertMediaFileToMediaStore(
            externalContentUri,
            absoluteFilePath,
            mediaDirectory,
            metadata,
            mimeType
        )

        setVideoTagsToFileCatching(videoFile, metadata)
        context.sendScanFile(absoluteFilePath)
    }
}

internal fun ContentValues(
    absoluteFilePath: String,
    relativeFilePath: String,
    metadata: com.paranid5.crescendo.core.common.metadata.VideoMetadata,
    mimeType: String,
) = ContentValues().apply {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        put(MediaStore.MediaColumns.IS_PENDING, 1)

    put(MediaStore.MediaColumns.TITLE, metadata.title)
    put(MediaStore.MediaColumns.ARTIST, metadata.author)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        put(MediaStore.MediaColumns.AUTHOR, metadata.author)

    put(MediaStore.MediaColumns.DURATION, metadata.durationMillis)
    put(MediaStore.MediaColumns.DISPLAY_NAME, metadata.title)
    put(MediaStore.MediaColumns.MIME_TYPE, mimeType)

    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
            put(MediaStore.MediaColumns.RELATIVE_PATH, relativeFilePath)

        else -> put(MediaStore.MediaColumns.DATA, absoluteFilePath)
    }
}