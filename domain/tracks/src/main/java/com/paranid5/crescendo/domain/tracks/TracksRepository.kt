package com.paranid5.crescendo.domain.tracks

interface TracksRepository :
    CurrentTrackIndexSubscriber,
    CurrentTrackIndexPublisher,
    CurrentTrackSubscriber,
    TrackOrderSubscriber,
    TrackOrderPublisher,
    TracksMediaStoreSubscriber
