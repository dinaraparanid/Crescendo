package com.paranid5.crescendo.data.media_store.tracks

import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import com.paranid5.crescendo.core.common.tracks.DefaultTrack
import com.paranid5.crescendo.core.resources.R

internal fun getNextTrackOrNull(context: Context, cursor: Cursor) = when {
    cursor.moveToNext() -> DefaultTrack(
        androidId = cursor.getLong(0),
        title = cursor.getString(1).nullIfUnknown ?: context.unknownTrackStr,
        artist = cursor.getString(2).nullIfUnknown ?: context.unknownArtistStr,
        album = cursor.getString(3).nullIfUnknown ?: context.unknownAlbumStr,
        path = cursor.getString(4),
        durationMillis = cursor.getLong(5),
        displayName = cursor.getString(6),
        dateAdded = cursor.getLong(7),
        numberInAlbum = cursor.getInt(8)
    )

    else -> null
}

internal inline val commonSelection
    get() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
            "${MediaStore.Audio.Media.IS_MUSIC} != 0 OR ${MediaStore.Audio.Media.IS_AUDIOBOOK} != 0"

        else -> "${MediaStore.Audio.Media.IS_MUSIC} != 0"
    }

internal inline val commonProjection: Array<String>
    get() {
        val projection = mutableListOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.TRACK
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            projection.add(MediaStore.Audio.Media.RELATIVE_PATH)

        return projection.toTypedArray()
    }

internal inline val commonOrder
    get() = "${MediaStore.Audio.Media.DATE_ADDED} DESC"

internal inline val String.nullIfUnknown
    get() = takeIf { it != "<unknown>" }

internal inline val Context.unknownTrackStr
    get() = resources.getString(R.string.unknown_track)

internal inline val Context.unknownArtistStr
    get() = resources.getString(R.string.unknown_artist)

internal inline val Context.unknownAlbumStr
    get() = resources.getString(R.string.unknown_album)