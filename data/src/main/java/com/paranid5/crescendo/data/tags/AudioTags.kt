package com.paranid5.crescendo.data.tags

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import arrow.core.Either
import com.paranid5.crescendo.core.common.uri.Path
import com.paranid5.crescendo.core.media.images.getImageBinaryDataFromPathCatching
import com.paranid5.crescendo.core.media.images.getImageBinaryDataFromUrlCatching
import com.paranid5.crescendo.core.media.media_scanner.sendScanFile
import com.paranid5.crescendo.data.tags.MediaStore.insertMediaFileToMediaStore
import com.paranid5.crescendo.domain.files.entity.Formats
import com.paranid5.crescendo.domain.files.entity.MediaFile
import com.paranid5.crescendo.domain.files.entity.MimeType
import com.paranid5.crescendo.domain.files.entity.mimeType
import com.paranid5.crescendo.domain.image.model.Image
import com.paranid5.crescendo.domain.metadata.model.AudioMetadata
import com.paranid5.crescendo.domain.metadata.model.Metadata
import com.paranid5.crescendo.domain.metadata.model.VideoMetadata
import com.paranid5.crescendo.utils.extensions.catchNonCancellation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import org.jaudiotagger.tag.images.ArtworkFactory

internal object AudioTags {
    suspend fun setAudioTags(
        context: Context,
        audioFile: MediaFile.AudioFile,
        metadata: Metadata,
        audioFormat: Formats,
    ) {
        val externalContentUri = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
                MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

            else -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val mimeType = audioFormat.mimeType
        val absoluteFilePath = audioFile.path
        val mediaDirectory = Path(Environment.DIRECTORY_MUSIC)

        withContext(Dispatchers.IO) {
            context.insertMediaFileToMediaStore(
                externalContentUri = externalContentUri,
                absoluteFilePath = absoluteFilePath,
                relativeFilePath = mediaDirectory,
                metadata = metadata,
                mimeType = mimeType,
            )

            if (audioFormat == Formats.MP3)
                setAudioTagsToFileCatching(context, audioFile, metadata)

            context.sendScanFile(absoluteFilePath)
        }
    }

    private inline fun <M : Metadata> setBaseAudioTagsToFile(
        file: MediaFile.AudioFile,
        metadata: M,
        setSpecificTags: (tag: Tag) -> Unit,
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
        metadata: VideoMetadata,
    ) = setBaseAudioTagsToFile(file, metadata) { tag ->
        metadata
            .covers
            .asSequence()
            .map { getImageBinaryDataCatching(context = context, image = it) }
            .firstOrNull { it.isRight() }
            ?.getOrNull()
            ?.let { ArtworkFactory.getNew().apply { binaryData = it } }
            ?.let(tag::setField)
    }

    private fun setAudioTagsToFile(
        context: Context,
        file: MediaFile.AudioFile,
        metadata: AudioMetadata,
    ) = setBaseAudioTagsToFile(file, metadata) { tag ->
        tag.setField(FieldKey.ALBUM, metadata.album.orEmpty())

        metadata
            .covers
            .asSequence()
            .map { getImageBinaryDataCatching(context = context, image = it) }
            .firstOrNull { it.isRight() }
            ?.getOrNull()
            ?.let { ArtworkFactory.getNew().apply { binaryData = it } }
            ?.let(tag::setField)
    }

    private fun getImageBinaryDataCatching(context: Context, image: Image) =
        when (image) {
            is Image.Path -> getImageBinaryDataFromPathCatching(context, image.value.value)
            is Image.Url -> getImageBinaryDataFromUrlCatching(context, image.value.value)
            is Image.Resource -> Either.Left(Exception("Resource files are not supported"))
        }

    private fun setAudioTagsToFileCatching(
        context: Context,
        file: MediaFile.AudioFile,
        metadata: Metadata,
    ) = Either.catchNonCancellation {
        when (metadata) {
            is AudioMetadata -> setAudioTagsToFile(context, file, metadata)
            is VideoMetadata -> setAudioTagsToFile(context, file, metadata)
        }
    }

    fun ContentValues(
        absoluteFilePath: Path,
        relativeFilePath: Path,
        metadata: AudioMetadata,
        mimeType: MimeType,
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
        put(MediaStore.MediaColumns.MIME_TYPE, mimeType.value)

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
                put(MediaStore.MediaColumns.RELATIVE_PATH, relativeFilePath.toString())

            else -> put(MediaStore.MediaColumns.DATA, absoluteFilePath.toString())
        }
    }
}
