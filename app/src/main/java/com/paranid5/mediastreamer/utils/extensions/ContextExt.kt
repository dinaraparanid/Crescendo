package com.paranid5.mediastreamer.utils.extensions

import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.bumptech.glide.Glide
import com.paranid5.mediastreamer.data.VideoMetadata
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.images.ArtworkFactory
import java.io.File

fun Context.registerReceiverCompat(
    receiver: BroadcastReceiver,
    vararg actions: String,
) = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> registerReceiver(
        receiver,
        IntentFilter().also { actions.forEach(it::addAction) },
        Context.RECEIVER_NOT_EXPORTED
    )

    else -> registerReceiver(
        receiver,
        IntentFilter().also { actions.forEach(it::addAction) },
    )
}

fun Context.registerReceiverCompat(
    receiver: BroadcastReceiver,
    filter: IntentFilter,
) = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> registerReceiver(
        receiver,
        filter,
        Context.RECEIVER_NOT_EXPORTED
    )

    else -> registerReceiver(receiver, filter)
}

fun Context.getImageBinaryData(url: String) =
    Glide.with(applicationContext)
        .asBitmap()
        .load(url)
        .submit()
        .get()
        .byteData

fun Context.getImageBinaryDataCatching(url: String) =
    kotlin.runCatching { getImageBinaryData(url) }

fun Context.setAudioTagsToFile(file: File, videoMetadata: VideoMetadata) =
    AudioFileIO.read(file).apply {
        tagOrCreateAndSetDefault.apply {
            setField(FieldKey.TITLE, videoMetadata.title)
            setField(FieldKey.ARTIST, videoMetadata.author)

            videoMetadata
                .covers
                .asSequence()
                .map { getImageBinaryDataCatching(it) }
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

fun Context.insertMediaFileToMediaStore(
    externalContentUri: Uri,
    absoluteFilePath: String,
    relativeFilePath: String,
    videoMetadata: VideoMetadata
) {
    val uri = applicationContext.contentResolver.insert(
        externalContentUri,
        ContentValues().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                put(MediaStore.MediaColumns.IS_PENDING, 0)

            put(MediaStore.MediaColumns.TITLE, videoMetadata.title)
            put(MediaStore.MediaColumns.ARTIST, videoMetadata.author)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                put(MediaStore.MediaColumns.AUTHOR, videoMetadata.author)

            put(MediaStore.MediaColumns.DURATION, videoMetadata.lenInMillis)
            put(MediaStore.MediaColumns.DATA, absoluteFilePath)
            put(MediaStore.MediaColumns.DISPLAY_NAME, videoMetadata.title)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                put(MediaStore.MediaColumns.RELATIVE_PATH, relativeFilePath)
        }
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && uri != null)
        applicationContext.contentResolver.update(
            uri, ContentValues().apply { put(MediaStore.Audio.Media.IS_PENDING, 0) },
            null, null
        )
}