package com.paranid5.crescendo.data.current_playlist

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.db.SqlDriver
import com.paranid5.crescendo.data.CurrentPlaylist
import com.paranid5.crescendo.data.CurrentPlaylistTrack
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.domain.repositories.CurrentPlaylistRepository
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CurrentPlaylistRepositoryImpl(driver: SqlDriver) :
    CurrentPlaylistRepository,
    CoroutineScope by CoroutineScope(Dispatchers.IO) {
    private val database by lazy {
        CurrentPlaylist(driver)
    }

    private val queries by lazy {
        database.currentPlaylistTrackQueries
    }

    override val tracksFlow
        get() = queries
            .selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { it.map(CurrentPlaylistTrack::toTrack).toImmutableList() }

    override fun replacePlaylistAsync(playlist: List<Track>) =
        launch(Dispatchers.IO) { replacePlaylist(playlist) }

    private fun replacePlaylist(playlist: List<Track>) =
        queries.transaction {
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