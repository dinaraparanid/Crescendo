package com.paranid5.crescendo.data.tracks

import com.paranid5.crescendo.domain.tracks.CurrentTrackIndexPublisher
import com.paranid5.crescendo.domain.tracks.CurrentTrackIndexSubscriber
import com.paranid5.crescendo.domain.tracks.CurrentTrackSubscriber
import com.paranid5.crescendo.domain.tracks.TrackOrderPublisher
import com.paranid5.crescendo.domain.tracks.TrackOrderSubscriber
import com.paranid5.crescendo.domain.tracks.TracksMediaStoreSubscriber
import com.paranid5.crescendo.domain.tracks.TracksRepository

internal class TracksRepositoryImpl(
    currentTrackIndexSubscriber: CurrentTrackIndexSubscriber,
    currentTrackIndexPublisher: CurrentTrackIndexPublisher,
    currentTrackSubscriber: CurrentTrackSubscriber,
    trackOrderSubscriber: TrackOrderSubscriber,
    trackOrderPublisher: TrackOrderPublisher,
    tracksMediaStoreSubscriber: TracksMediaStoreSubscriber,
) : TracksRepository,
    CurrentTrackIndexSubscriber by currentTrackIndexSubscriber,
    CurrentTrackIndexPublisher by currentTrackIndexPublisher,
    CurrentTrackSubscriber by currentTrackSubscriber,
    TrackOrderSubscriber by trackOrderSubscriber,
    TrackOrderPublisher by trackOrderPublisher,
    TracksMediaStoreSubscriber by tracksMediaStoreSubscriber
