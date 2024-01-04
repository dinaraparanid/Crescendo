package com.paranid5.crescendo.presentation.main.tracks

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.current_playlist.CurrentPlaylistRepository
import com.paranid5.crescendo.data.states.playback.AudioStatusStatePublisher
import com.paranid5.crescendo.data.states.playback.AudioStatusStatePublisherImpl
import com.paranid5.crescendo.data.states.tracks.CurrentTrackStateSubscriber
import com.paranid5.crescendo.data.states.tracks.CurrentTrackStateSubscriberImpl
import com.paranid5.crescendo.data.states.tracks.TrackOrderStatePublisher
import com.paranid5.crescendo.data.states.tracks.TrackOrderStatePublisherImpl
import com.paranid5.crescendo.data.states.tracks.TrackOrderStateSubscriber
import com.paranid5.crescendo.data.states.tracks.TrackOrderStateSubscriberImpl
import com.paranid5.crescendo.presentation.main.tracks.states.QueryStateHolder
import com.paranid5.crescendo.presentation.main.tracks.states.QueryStateHolderImpl
import com.paranid5.crescendo.presentation.main.tracks.states.SearchBarStateHolder
import com.paranid5.crescendo.presentation.main.tracks.states.SearchBarStateHolderImpl
import com.paranid5.crescendo.presentation.main.tracks.states.TracksStateHolder
import com.paranid5.crescendo.presentation.main.tracks.states.TracksStateHolderImpl

@Suppress("IncorrectFormatting")
class TracksViewModel(
    storageHandler: StorageHandler,
    currentPlaylistRepository: CurrentPlaylistRepository
) : ViewModel(),
    AudioStatusStatePublisher by AudioStatusStatePublisherImpl(storageHandler),
    TrackOrderStateSubscriber by TrackOrderStateSubscriberImpl(storageHandler),
    TrackOrderStatePublisher by TrackOrderStatePublisherImpl(storageHandler),
    CurrentTrackStateSubscriber by CurrentTrackStateSubscriberImpl(storageHandler, currentPlaylistRepository),
    QueryStateHolder by QueryStateHolderImpl(),
    SearchBarStateHolder by SearchBarStateHolderImpl(),
    TracksStateHolder by TracksStateHolderImpl(storageHandler)