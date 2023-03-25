package com.paranid5.mediastreamer.data.utils.extensions

import android.graphics.Bitmap
import com.paranid5.mediastreamer.data.VideoMetadata
import android.media.MediaMetadata

fun VideoMetadata.toAndroidMetadata(cover: Bitmap? = null): MediaMetadata =
    MediaMetadata.Builder()
        .putText(MediaMetadata.METADATA_KEY_TITLE, title)
        .putText(MediaMetadata.METADATA_KEY_ARTIST, author)
        .putLong(MediaMetadata.METADATA_KEY_DURATION, lenInMillis)
        .putBitmap(MediaMetadata.METADATA_KEY_ART, cover)
        .build()