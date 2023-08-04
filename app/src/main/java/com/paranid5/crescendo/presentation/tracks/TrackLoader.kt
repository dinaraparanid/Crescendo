package com.paranid5.crescendo.presentation.tracks

import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import com.paranid5.crescendo.R
import com.paranid5.crescendo.data.tracks.DefaultTrack

private inline val selection
    get() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
            "${MediaStore.Audio.Media.IS_MUSIC} != 0 OR ${MediaStore.Audio.Media.IS_AUDIOBOOK} != 0"

        else -> "${MediaStore.Audio.Media.IS_MUSIC} != 0"
    }

private inline val projection: Array<String>
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

private inline val order
    get() = "${MediaStore.Audio.Media.DATE_ADDED} DESC"

private inline val String.nullIfUnknown
    get() = if (this == "<unknown>") null else this

private inline val Context.unknownTrack
    get() = resources.getString(R.string.unknown_track)

private inline val Context.unknownArtist
    get() = resources.getString(R.string.unknown_artist)

private inline val Context.unknownAlbum
    get() = resources.getString(R.string.unknown_album)

internal inline val Context.allTracksFromMediaStore
    get() = contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        null,
        order
    )?.use { cursor ->
        generateSequence { getNextTrackOrNull(cursor) }.toList()
    } ?: listOf()

private fun Context.getNextTrackOrNull(cursor: Cursor) = when {
    cursor.moveToNext() -> DefaultTrack(
        androidId = cursor.getLong(0),
        title = cursor.getString(1).nullIfUnknown ?: unknownTrack,
        artist = cursor.getString(2).nullIfUnknown ?: unknownArtist,
        album = cursor.getString(3).nullIfUnknown ?: unknownAlbum,
        path = cursor.getString(4),
        duration = cursor.getLong(5),
        displayName = cursor.getString(6),
        dateAdded = cursor.getLong(7),
        numberInAlbum = cursor.getInt(8)
    )

    else -> null
}

