package com.paranid5.crescendo.playing.view_model

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.domain.playback.AudioSessionIdSubscriber
import com.paranid5.crescendo.domain.playback.AudioStatusPublisher
import com.paranid5.crescendo.domain.playback.AudioStatusSubscriber
import com.paranid5.crescendo.domain.playback.PlaybackRepository
import com.paranid5.crescendo.domain.playback.PlayingStateSubscriber
import com.paranid5.crescendo.domain.playback.RepeatingSubscriber
import com.paranid5.crescendo.domain.playback.StreamPlaybackPositionPublisher
import com.paranid5.crescendo.domain.playback.StreamPlaybackPositionSubscriber
import com.paranid5.crescendo.domain.playback.TracksPlaybackPositionPublisher
import com.paranid5.crescendo.domain.playback.TracksPlaybackPositionSubscriber
import com.paranid5.crescendo.domain.stream.CurrentMetadataSubscriber
import com.paranid5.crescendo.domain.stream.PlayingUrlSubscriber
import com.paranid5.crescendo.domain.stream.StreamRepository
import com.paranid5.crescendo.domain.tracks.CurrentTrackSubscriber
import com.paranid5.crescendo.domain.tracks.TracksRepository
import kotlinx.coroutines.flow.MutableStateFlow

class PlayingViewModel(
    playbackRepository: PlaybackRepository,
    tracksRepository: TracksRepository,
    streamRepository: StreamRepository,
) : ViewModel(),
    AudioSessionIdSubscriber by playbackRepository,
    PlayingStateSubscriber by playbackRepository,
    AudioStatusSubscriber by playbackRepository,
    AudioStatusPublisher by playbackRepository,
    StreamPlaybackPositionSubscriber by playbackRepository,
    StreamPlaybackPositionPublisher by playbackRepository,
    TracksPlaybackPositionSubscriber by playbackRepository,
    TracksPlaybackPositionPublisher by playbackRepository,
    RepeatingSubscriber by playbackRepository,
    CurrentTrackSubscriber by tracksRepository,
    CurrentMetadataSubscriber by streamRepository,
    PlayingUrlSubscriber by streamRepository {
    val backResultState = MutableStateFlow<PlayingBackResult?>(null)
}
