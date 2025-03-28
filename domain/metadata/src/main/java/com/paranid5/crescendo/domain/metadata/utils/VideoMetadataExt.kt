package com.paranid5.crescendo.domain.metadata.utils

import android.graphics.Bitmap
import android.support.v4.media.MediaMetadataCompat
import com.paranid5.crescendo.domain.metadata.model.VideoMetadata

fun VideoMetadata.toAndroidMetadata(cover: Bitmap? = null): MediaMetadataCompat =
    MediaMetadataCompat.Builder()
        .putText(MediaMetadataCompat.METADATA_KEY_TITLE, title)
        .putText(MediaMetadataCompat.METADATA_KEY_ARTIST, author)
        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, durationMillis)
        .putBitmap(MediaMetadataCompat.METADATA_KEY_ART, cover)
        .build()