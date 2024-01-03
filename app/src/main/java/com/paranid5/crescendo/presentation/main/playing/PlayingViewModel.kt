package com.paranid5.crescendo.presentation.main.playing

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.states.playback.AudioStatusStatePublisher
import com.paranid5.crescendo.data.states.playback.AudioStatusStatePublisherImpl
import com.paranid5.crescendo.data.states.playback.AudioStatusStateSubscriber
import com.paranid5.crescendo.data.states.playback.AudioStatusStateSubscriberImpl
import com.paranid5.crescendo.data.states.playback.RepeatingStateSubscriber
import com.paranid5.crescendo.data.states.playback.RepeatingStateSubscriberImpl
import com.paranid5.crescendo.data.states.playback.StreamPlaybackPositionStatePublisher
import com.paranid5.crescendo.data.states.playback.StreamPlaybackPositionStatePublisherImpl
import com.paranid5.crescendo.data.states.playback.StreamPlaybackPositionStateSubscriber
import com.paranid5.crescendo.data.states.playback.StreamPlaybackPositionStateSubscriberImpl
import com.paranid5.crescendo.data.states.playback.TracksPlaybackPositionStatePublisher
import com.paranid5.crescendo.data.states.playback.TracksPlaybackPositionStatePublisherImpl
import com.paranid5.crescendo.data.states.playback.TracksPlaybackPositionStateSubscriber
import com.paranid5.crescendo.data.states.playback.TracksPlaybackPositionStateSubscriberImpl
import com.paranid5.crescendo.data.states.stream.CurrentUrlStateSubscriber
import com.paranid5.crescendo.data.states.stream.CurrentUrlStateSubscriberImpl
import com.paranid5.crescendo.data.states.tracks.CurrentTrackStateSubscriber
import com.paranid5.crescendo.data.states.tracks.CurrentTrackStateSubscriberImpl
import com.paranid5.crescendo.presentation.main.playing.states.CacheDialogStateHolder
import com.paranid5.crescendo.presentation.main.playing.states.CacheDialogStateHolderImpl

@Suppress("IncorrectFormatting")
class PlayingViewModel(private val storageHandler: StorageHandler) : ViewModel(),
    AudioStatusStateSubscriber by AudioStatusStateSubscriberImpl(storageHandler),
    AudioStatusStatePublisher by AudioStatusStatePublisherImpl(storageHandler),
    StreamPlaybackPositionStateSubscriber by StreamPlaybackPositionStateSubscriberImpl(storageHandler),
    StreamPlaybackPositionStatePublisher by StreamPlaybackPositionStatePublisherImpl(storageHandler),
    TracksPlaybackPositionStateSubscriber by TracksPlaybackPositionStateSubscriberImpl(storageHandler),
    TracksPlaybackPositionStatePublisher by TracksPlaybackPositionStatePublisherImpl(storageHandler),
    RepeatingStateSubscriber by RepeatingStateSubscriberImpl(storageHandler),
    CurrentTrackStateSubscriber by CurrentTrackStateSubscriberImpl(storageHandler),
    CurrentUrlStateSubscriber by CurrentUrlStateSubscriberImpl(storageHandler),
    CacheDialogStateHolder by CacheDialogStateHolderImpl(storageHandler)
