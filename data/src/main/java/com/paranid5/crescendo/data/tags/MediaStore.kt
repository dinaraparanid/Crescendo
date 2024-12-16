package com.paranid5.crescendo.data.tags

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.paranid5.crescendo.core.common.media.MimeType
import com.paranid5.crescendo.core.common.uri.Path
import com.paranid5.crescendo.domain.metadata.model.AudioMetadata
import com.paranid5.crescendo.domain.metadata.model.Metadata
import com.paranid5.crescendo.domain.metadata.model.VideoMetadata

internal object MediaStore {
    fun Context.insertMediaFileToMediaStore(
        externalContentUri: Uri,
        absoluteFilePath: Path,
        relativeFilePath: Path,
        metadata: Metadata,
        mimeType: MimeType,
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
        absoluteFilePath: Path,
        relativeFilePath: Path,
        metadata: Metadata,
        mimeType: MimeType,
    ) = when (metadata) {
        is AudioMetadata -> AudioTags.ContentValues(
            absoluteFilePath = absoluteFilePath,
            relativeFilePath = relativeFilePath,
            metadata = metadata,
            mimeType = mimeType,
        )

        is VideoMetadata -> VideoTags.ContentValues(
            absoluteFilePath = absoluteFilePath,
            relativeFilePath = relativeFilePath,
            metadata = metadata,
            mimeType = mimeType,
        )
    }
}
