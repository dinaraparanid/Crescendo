package com.paranid5.crescendo.media.tags

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.paranid5.crescendo.domain.metadata.AudioMetadata
import com.paranid5.crescendo.domain.metadata.Metadata
import com.paranid5.crescendo.domain.metadata.VideoMetadata

fun Context.insertMediaFileToMediaStore(
    externalContentUri: Uri,
    absoluteFilePath: String,
    relativeFilePath: String,
    metadata: Metadata,
    mimeType: String,
): Uri {
    val uri = applicationContext.contentResolver.insert(
        externalContentUri,
        ContentValues(absoluteFilePath, relativeFilePath, metadata, mimeType)
    )!!

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        applicationContext.contentResolver.update(
            uri, ContentValues().apply { put(MediaStore.Audio.Media.IS_PENDING, 0) },
            null, null
        )

    return uri
}

private fun ContentValues(
    absoluteFilePath: String,
    relativeFilePath: String,
    metadata: Metadata,
    mimeType: String,
) = when (metadata) {
    is AudioMetadata -> ContentValues(absoluteFilePath, relativeFilePath, metadata, mimeType)
    is VideoMetadata -> ContentValues(absoluteFilePath, relativeFilePath, metadata, mimeType)
}