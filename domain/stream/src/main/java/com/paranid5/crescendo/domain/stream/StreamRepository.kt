package com.paranid5.crescendo.domain.stream

interface StreamRepository :
    CurrentMetadataSubscriber,
    CurrentMetadataPublisher,
    DownloadingUrlSubscriber,
    DownloadingUrlPublisher,
    PlayingUrlSubscriber,
    PlayingUrlPublisher
