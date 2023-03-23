package com.paranid5.mediastreamer.data.utils.extensions

import android.graphics.Bitmap
import com.paranid5.mediastreamer.data.VideoMetadata
import android.media.MediaMetadata as OldMetadata

fun VideoMetadata.toOldMetadata(cover: Bitmap? = null) = OldMetadata.Builder()
    .putText(OldMetadata.METADATA_KEY_TITLE, title)
    .putText(OldMetadata.METADATA_KEY_ARTIST, author)
    .putText(OldMetadata.METADATA_KEY_ALBUM, title)
    .putLong(OldMetadata.METADATA_KEY_DURATION, lenInMillis)
    .putBitmap(OldMetadata.METADATA_KEY_ART, cover)
    .build()