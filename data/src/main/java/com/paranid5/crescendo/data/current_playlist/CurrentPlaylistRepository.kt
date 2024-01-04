package com.paranid5.crescendo.data.current_playlist

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.db.SqlDriver
import com.paranid5.crescendo.data.CurrentPlaylist
import com.paranid5.crescendo.data.CurrentPlaylistTrack
import com.paranid5.crescendo.domain.tracks.Track
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CurrentPlaylistRepository(driver: SqlDriver) :
    CoroutineScope by CoroutineScope(Dispatchers.IO) {
    private val database by lazy {
        CurrentPlaylist(driver)
    }

    private val queries by lazy {
        database.currentPlaylistTrackQueries
    }

    val tracksFlow
        get() = queries
            .selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { it.map(CurrentPlaylistTrack::toTrack).toImmutableList() }

    fun replacePlaylistAsync(playlist: List<Track>): Job {
        Exception("BEBRA").printStackTrace()
        return launch(Dispatchers.IO) { replacePlaylist(playlist) }
    }

    private fun replacePlaylist(playlist: List<Track>) =
        queries.transaction {
            println("TRANSACTION: ${playlist.size}")
            queries.clearPlaylist()

            playlist.forEach {
                queries.insertTrack(
                    id = null,
                    androidId = it.androidId,
                    title = it.title,
                    artist = it.artist,
                    album = it.album,
                    path = it.path,
                    durationMillis = it.durationMillis,
                    displayName = it.displayName,
                    dateAdded = it.dateAdded,
                    numberInAlbum = it.numberInAlbum.toLong()
                )
            }
        }
}