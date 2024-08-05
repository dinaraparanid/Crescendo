package com.paranid5.crescendo.data.tracks

import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import arrow.core.Either
import com.paranid5.crescendo.core.common.tracks.DefaultTrack
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.domain.tracks.TracksMediaStoreSubscriber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class TracksMediaStoreSubscriberImpl(
    private val context: Context,
) : TracksMediaStoreSubscriber {
    override suspend fun getAllTracksFromMediaStore() =
        withContext(Dispatchers.IO) {
            allTracksMediaStoreQuery()
                .getOrNull()
                ?.use(::trackList)
                .orEmpty()
        }

    override suspend fun getTrackFromMediaStore(trackPath: String) =
        withContext(Dispatchers.IO) {
            trackMediaStoreQuery(trackPath)
                .getOrNull()
                ?.use(::getNextTrackOrNull)
        }

    private fun trackMediaStoreQuery(trackPath: String) = Either.catch {
        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            commonProjection,
            trackSelection,
            arrayOf(trackPath),
            commonOrder
        )
    }

    private fun trackList(cursor: Cursor) =
        generateSequence { getNextTrackOrNull(cursor) }.toList()

    private fun allTracksMediaStoreQuery() = Either.catch {
        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            commonProjection,
            commonSelection,
            null,
            commonOrder
        )
    }

    private fun getNextTrackOrNull(cursor: Cursor) = when {
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
}

private inline val trackSelection
    get() = "${MediaStore.Audio.Media.DATA} = ? AND $commonSelection"

private inline val commonSelection
    get() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
            "${MediaStore.Audio.Media.IS_MUSIC} != 0 OR ${MediaStore.Audio.Media.IS_AUDIOBOOK} != 0"

        else -> "${MediaStore.Audio.Media.IS_MUSIC} != 0"
    }

private inline val commonProjection: Array<String>
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

private inline val commonOrder
    get() = "${MediaStore.Audio.Media.DATE_ADDED} DESC"

private inline val String.nullIfUnknown
    get() = takeIf { it != "<unknown>" }

private inline val Context.unknownTrackStr
    get() = resources.getString(R.string.unknown_track)

private inline val Context.unknownArtistStr
    get() = resources.getString(R.string.unknown_artist)

private inline val Context.unknownAlbumStr
    get() = resources.getString(R.string.unknown_album)
