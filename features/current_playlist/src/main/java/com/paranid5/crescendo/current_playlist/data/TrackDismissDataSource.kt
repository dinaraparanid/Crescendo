package com.paranid5.crescendo.current_playlist.data

import com.paranid5.crescendo.core.common.tracks.Track
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal interface TrackDismissDataSource {
    val playlistDismissMediatorState: StateFlow<ImmutableList<Track>>
    val trackIndexDismissMediatorState: StateFlow<Int>
    val trackPathDismissKeyState: StateFlow<String>

    fun setPlaylistDismissMediator(playlistDismissMediator: ImmutableList<Track>)
    fun setTrackIndexDismissMediator(trackIndexDismissMediator: Int)
    fun setTrackPathDismissKey(trackPathDismissKey: String)
}

class TrackDismissDataSourceImpl : TrackDismissDataSource {
    private val _playlistDismissMediatorState by lazy {
        MutableStateFlow<ImmutableList<Track>>(persistentListOf())
    }

    override val playlistDismissMediatorState by lazy {
        _playlistDismissMediatorState.asStateFlow()
    }

    override fun setPlaylistDismissMediator(playlistDismissMediator: ImmutableList<Track>) =
        _playlistDismissMediatorState.update { playlistDismissMediator }

    private val _trackIndexDismissMediatorState by lazy {
        MutableStateFlow(0)
    }

    override val trackIndexDismissMediatorState by lazy {
        _trackIndexDismissMediatorState.asStateFlow()
    }

    override fun setTrackIndexDismissMediator(trackIndexDismissMediator: Int) =
        _trackIndexDismissMediatorState.update { trackIndexDismissMediator }

    private val _trackPathDismissKeyState by lazy {
        MutableStateFlow("")
    }

    override val trackPathDismissKeyState by lazy {
        _trackPathDismissKeyState.asStateFlow()
    }

    override fun setTrackPathDismissKey(trackPathDismissKey: String) =
        _trackPathDismissKeyState.update { trackPathDismissKey }
}