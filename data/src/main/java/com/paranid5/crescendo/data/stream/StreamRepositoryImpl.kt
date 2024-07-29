package com.paranid5.crescendo.data.stream

import com.paranid5.crescendo.domain.stream.CurrentMetadataPublisher
import com.paranid5.crescendo.domain.stream.CurrentMetadataSubscriber
import com.paranid5.crescendo.domain.stream.DownloadingUrlPublisher
import com.paranid5.crescendo.domain.stream.DownloadingUrlSubscriber
import com.paranid5.crescendo.domain.stream.PlayingUrlPublisher
import com.paranid5.crescendo.domain.stream.PlayingUrlSubscriber
import com.paranid5.crescendo.domain.stream.StreamRepository

internal class StreamRepositoryImpl(
    currentMetadataSubscriber: CurrentMetadataSubscriber,
    currentMetadataPublisher: CurrentMetadataPublisher,
    downloadingUrlSubscriber: DownloadingUrlSubscriber,
    downloadingUrlPublisher: DownloadingUrlPublisher,
    playingUrlSubscriber: PlayingUrlSubscriber,
    playingUrlPublisher: PlayingUrlPublisher,
) : StreamRepository,
    CurrentMetadataSubscriber by currentMetadataSubscriber,
    CurrentMetadataPublisher by currentMetadataPublisher,
    DownloadingUrlSubscriber by downloadingUrlSubscriber,
    DownloadingUrlPublisher by downloadingUrlPublisher,
    PlayingUrlSubscriber by playingUrlSubscriber,
    PlayingUrlPublisher by playingUrlPublisher
