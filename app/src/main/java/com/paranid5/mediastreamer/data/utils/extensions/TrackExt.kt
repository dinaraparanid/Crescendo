package com.paranid5.mediastreamer.data.utils.extensions

import android.graphics.Bitmap
import android.support.v4.media.MediaMetadataCompat
import com.paranid5.mediastreamer.data.tracks.Track

fun Track.toAndroidMetadata(cover: Bitmap? = null): MediaMetadataCompat =
    MediaMetadataCompat.Builder()
        .putText(MediaMetadataCompat.METADATA_KEY_TITLE, title)
        .putText(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
        .putText(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
        .putBitmap(MediaMetadataCompat.METADATA_KEY_ART, cover)
        .build()