package com.paranid5.crescendo.data.current_playlist

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.db.SqlDriver
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.data.CurrentPlaylist
import com.paranid5.crescendo.domain.current_playlist.CurrentPlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class CurrentPlaylistRepositoryImpl(driver: SqlDriver) : CurrentPlaylistRepository {
    private val database by lazy {
        CurrentPlaylist(driver)
    }

    private val queries by lazy {
        database.currentPlaylistTrackQueries
    }

    override val currentPlaylistFlow
        get() = queries
            .selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { it.toTracks() }

    private inline val currentPlaylist
        get() = queries.selectAll().executeAsList().toTracks()

    override suspend fun updateCurrentPlaylist(playlist: List<Track>) =
        withContext(Dispatchers.IO) {
            queries.transaction {
                resetPlaylistImpl(playlist)
            }
        }

    override suspend fun addTrackToPlaylist(track: Track) {
        withContext(Dispatchers.IO) {
            queries.transaction {
                resetPlaylistImpl(currentPlaylist + track)
            }
        }
    }

    private fun resetPlaylistImpl(playlist: List<Track>) {
        queries.clearPlaylist()

        playlist.forEach { track ->
            queries.insertTrack(
                id = null,
                androidId = track.androidId,
                title = track.title,
                artist = track.artist,
                album = track.album,
                path = track.path,
                durationMillis = track.durationMillis,
                displayName = track.displayName,
                dateAdded = track.dateAdded,
                numberInAlbum = track.numberInAlbum.toLong(),
            )
        }
    }
}
