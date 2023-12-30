package com.paranid5.crescendo.presentation.main.current_playlist.states

import com.paranid5.crescendo.domain.tracks.DefaultTrack
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface TrackDismissStateHolder {
    val playlistDismissMediatorState: StateFlow<ImmutableList<DefaultTrack>>
    val trackIndexDismissMediatorState: StateFlow<Int>
    val trackPathDismissKeyState: StateFlow<String>

    fun setPlaylistDismissMediator(playlistDismissMediator: ImmutableList<DefaultTrack>)
    fun setTrackIndexDismissMediator(trackIndexDismissMediator: Int)
    fun setTrackPathDismissKey(trackPathDismissKey: String)
}

class TrackDismissStateHolderImpl : TrackDismissStateHolder {
    private val _playlistDismissMediatorState by lazy {
        MutableStateFlow<ImmutableList<DefaultTrack>>(persistentListOf())
    }

    override val playlistDismissMediatorState by lazy {
        _playlistDismissMediatorState.asStateFlow()
    }

    override fun setPlaylistDismissMediator(playlistDismissMediator: ImmutableList<DefaultTrack>) =
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