package com.paranid5.crescendo.presentation.main.current_playlist

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.states.playback.AudioStatusStatePublisher
import com.paranid5.crescendo.data.states.playback.AudioStatusStatePublisherImpl
import com.paranid5.crescendo.data.states.tracks.CurrentPlaylistStatePublisher
import com.paranid5.crescendo.data.states.tracks.CurrentPlaylistStatePublisherImpl
import com.paranid5.crescendo.data.states.tracks.CurrentPlaylistStateSubscriber
import com.paranid5.crescendo.data.states.tracks.CurrentPlaylistStateSubscriberImpl
import com.paranid5.crescendo.data.states.tracks.CurrentTrackIndexStatePublisher
import com.paranid5.crescendo.data.states.tracks.CurrentTrackIndexStatePublisherImpl
import com.paranid5.crescendo.data.states.tracks.CurrentTrackIndexStateSubscriber
import com.paranid5.crescendo.data.states.tracks.CurrentTrackIndexStateSubscriberImpl
import com.paranid5.crescendo.presentation.main.current_playlist.states.TrackDismissStateHolder
import com.paranid5.crescendo.presentation.main.current_playlist.states.TrackDismissStateHolderImpl

class CurrentPlaylistViewModel(private val storageHandler: StorageHandler) :
    ViewModel(),
    AudioStatusStatePublisher by AudioStatusStatePublisherImpl(storageHandler),
    CurrentPlaylistStateSubscriber by CurrentPlaylistStateSubscriberImpl(storageHandler),
    CurrentPlaylistStatePublisher by CurrentPlaylistStatePublisherImpl(storageHandler),
    CurrentTrackIndexStateSubscriber by CurrentTrackIndexStateSubscriberImpl(storageHandler),
    CurrentTrackIndexStatePublisher by CurrentTrackIndexStatePublisherImpl(storageHandler),
    TrackDismissStateHolder by TrackDismissStateHolderImpl()