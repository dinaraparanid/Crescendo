package com.paranid5.crescendo.media.tags

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.paranid5.crescendo.domain.caching.Formats
import com.paranid5.crescendo.domain.media.files.MediaFile
import com.paranid5.crescendo.domain.media_scanner.sendScanFile
import com.paranid5.crescendo.domain.metadata.AudioMetadata
import com.paranid5.crescendo.domain.metadata.Metadata
import com.paranid5.crescendo.domain.metadata.VideoMetadata
import com.paranid5.crescendo.media.images.getImageBinaryDataFromPathCatching
import com.paranid5.crescendo.media.images.getImageBinaryDataFromUrlCatching
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import org.jaudiotagger.tag.images.ArtworkFactory

suspend fun setAudioTags(
    context: Context,
    audioFile: MediaFile.AudioFile,
    metadata: Metadata,
    audioFormat: Formats
) {
    val externalContentUri = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

        else -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    }

    val mediaDirectory = Environment.DIRECTORY_MUSIC

    val mimeType = when (audioFormat) {
        Formats.MP3 -> "audio/mpeg"
        Formats.AAC -> "audio/aac"
        Formats.WAV -> "audio/x-wav"
        else -> throw IllegalArgumentException("Video format for audio file")
    }

    val absoluteFilePath = audioFile.absolutePath

    withContext(Dispatchers.IO) {
        context.insertMediaFileToMediaStore(
            externalContentUri,
            absoluteFilePath,
            mediaDirectory,
            metadata,
            mimeType
        )

        if (audioFormat == Formats.MP3)
            setAudioTagsToFileCatching(context, audioFile, metadata)

        context.sendScanFile(absoluteFilePath)
    }
}

private inline fun <M : Metadata> setBaseAudioTagsToFile(
    file: MediaFile.AudioFile,
    metadata: M,
    setSpecificTags: (tag: Tag) -> Unit
) = AudioFileIO.read(file).run {
    tagOrCreateAndSetDefault.run {
        setField(FieldKey.TITLE, metadata.title)
        setField(FieldKey.ARTIST, metadata.author)
        setSpecificTags(this)
        commit()
    }
}

private fun setAudioTagsToFile(
    context: Context,
    file: MediaFile.AudioFile,
    metadata: VideoMetadata
) = setBaseAudioTagsToFile(file, metadata) { tag ->
    metadata
        .covers
        .asSequence()
        .map { getImageBinaryDataFromUrlCatching(context, it) }
        .firstOrNull { it.isSuccess }
        ?.getOrNull()
        ?.let { ArtworkFactory.getNew().apply { binaryData = it } }
        ?.let(tag::setField)
}

private fun setAudioTagsToFile(
    context: Context,
    file: MediaFile.AudioFile,
    metadata: AudioMetadata
) = setBaseAudioTagsToFile(file, metadata) { tag ->
    tag.setField(FieldKey.ALBUM, metadata.album)

    metadata
        .covers
        .asSequence()
        .map { getImageBinaryDataFromPathCatching(context, it) }
        .firstOrNull { it.isSuccess }
        ?.getOrNull()
        ?.let { ArtworkFactory.getNew().apply { binaryData = it } }
        ?.let(tag::setField)
}

private fun setAudioTagsToFileCatching(
    context: Context,
    file: MediaFile.AudioFile,
    metadata: Metadata
) = runCatching {
    when (metadata) {
        is AudioMetadata -> setAudioTagsToFile(context, file, metadata)
        is VideoMetadata -> setAudioTagsToFile(context, file, metadata)
    }
}

internal fun ContentValues(
    absoluteFilePath: String,
    relativeFilePath: String,
    metadata: AudioMetadata,
    mimeType: String,
) = ContentValues().apply {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        put(MediaStore.MediaColumns.IS_PENDING, 1)

    put(MediaStore.MediaColumns.TITLE, metadata.title)
    put(MediaStore.MediaColumns.ARTIST, metadata.author)
    put(MediaStore.MediaColumns.ALBUM, metadata.album)

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