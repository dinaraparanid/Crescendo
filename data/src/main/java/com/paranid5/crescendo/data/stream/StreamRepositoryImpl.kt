package com.paranid5.crescendo.data.stream

import com.paranid5.crescendo.domain.stream.CurrentMetadataPublisher
import com.paranid5.crescendo.domain.stream.CurrentMetadataSubscriber
import com.paranid5.crescendo.domain.stream.DownloadingUrlPublisher
import com.paranid5.crescendo.domain.stream.DownloadingUrlSubscriber
import com.paranid5.crescendo.domain.stream.PlayingStreamUrlPublisher
import com.paranid5.crescendo.domain.stream.PlayingStreamUrlSubscriber
import com.paranid5.crescendo.domain.stream.StreamRepository
import com.paranid5.crescendo.domain.stream.VideoMetadataApi

internal class StreamRepositoryImpl(
    currentMetadataSubscriber: CurrentMetadataSubscriber,
    currentMetadataPublisher: CurrentMetadataPublisher,
    downloadingUrlSubscriber: DownloadingUrlSubscriber,
    downloadingUrlPublisher: DownloadingUrlPublisher,
    playingStreamUrlSubscriber: PlayingStreamUrlSubscriber,
    playingStreamUrlPublisher: PlayingStreamUrlPublisher,
    videoMetadataApi: VideoMetadataApi,
) : StreamRepository,
    CurrentMetadataSubscriber by currentMetadataSubscriber,
    CurrentMetadataPublisher by currentMetadataPublisher,
    DownloadingUrlSubscriber by downloadingUrlSubscriber,
    DownloadingUrlPublisher by downloadingUrlPublisher,
    PlayingStreamUrlSubscriber by playingStreamUrlSubscriber,
    PlayingStreamUrlPublisher by playingStreamUrlPublisher,
    VideoMetadataApi by videoMetadataApi
