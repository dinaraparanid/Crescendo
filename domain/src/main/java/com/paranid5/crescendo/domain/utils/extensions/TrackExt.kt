package com.paranid5.crescendo.domain.utils.extensions

import android.graphics.Bitmap
import android.support.v4.media.MediaMetadataCompat
import com.paranid5.crescendo.domain.tracks.DefaultTrack
import com.paranid5.crescendo.domain.tracks.Track

inline val Track.artistAlbum
    get() = "$artist / $album"

fun Track.toAndroidMetadata(cover: Bitmap? = null): MediaMetadataCompat =
    MediaMetadataCompat.Builder()
        .putText(MediaMetadataCompat.METADATA_KEY_TITLE, title)
        .putText(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
        .putText(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
        .putBitmap(MediaMetadataCompat.METADATA_KEY_ART, cover)
        .build()

fun Iterable<Track>.toDefaultTrackList() = map(::DefaultTrack)

inline val Iterable<Track>.totalDuration
    get() = fold(0L) { acc, track -> acc + track.duration }