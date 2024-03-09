package com.paranid5.crescendo.utils.extensions

import android.graphics.Bitmap
import android.support.v4.media.MediaMetadataCompat
import androidx.media3.common.MediaItem
import com.paranid5.crescendo.core.common.tracks.DefaultTrack
import com.paranid5.crescendo.core.common.tracks.Track

inline val com.paranid5.crescendo.core.common.tracks.Track.artistAlbum
    get() = "$artist / $album"

fun com.paranid5.crescendo.core.common.tracks.Track.toAndroidMetadata(cover: Bitmap? = null): MediaMetadataCompat =
    MediaMetadataCompat.Builder()
        .putText(MediaMetadataCompat.METADATA_KEY_TITLE, title)
        .putText(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
        .putText(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, durationMillis)
        .putBitmap(MediaMetadataCompat.METADATA_KEY_ART, cover)
        .build()

fun com.paranid5.crescendo.core.common.tracks.Track.toMediaItem() = MediaItem.fromUri(path)

fun Iterable<com.paranid5.crescendo.core.common.tracks.Track>.toDefaultTrackList() = map(::DefaultTrack)

fun Iterable<com.paranid5.crescendo.core.common.tracks.Track>.toMediaItemList() = map(com.paranid5.crescendo.core.common.tracks.Track::toMediaItem)

inline val Iterable<com.paranid5.crescendo.core.common.tracks.Track>.totalDurationMillis
    get() = fold(0L) { acc, track -> acc + track.durationMillis }