package com.paranid5.crescendo.data.stream.di

import com.paranid5.crescendo.data.stream.CurrentMetadataPublisherImpl
import com.paranid5.crescendo.data.stream.CurrentMetadataSubscriberImpl
import com.paranid5.crescendo.data.stream.DownloadingUrlPublisherImpl
import com.paranid5.crescendo.data.stream.DownloadingUrlSubscriberImpl
import com.paranid5.crescendo.data.stream.PlayingStreamUrlPublisherImpl
import com.paranid5.crescendo.data.stream.PlayingStreamUrlSubscriberImpl
import com.paranid5.crescendo.data.stream.StreamRepositoryImpl
import com.paranid5.crescendo.data.stream.VideoMetadataApiImpl
import com.paranid5.crescendo.domain.stream.CurrentMetadataPublisher
import com.paranid5.crescendo.domain.stream.CurrentMetadataSubscriber
import com.paranid5.crescendo.domain.stream.DownloadingUrlPublisher
import com.paranid5.crescendo.domain.stream.DownloadingUrlSubscriber
import com.paranid5.crescendo.domain.stream.PlayingStreamUrlPublisher
import com.paranid5.crescendo.domain.stream.PlayingStreamUrlSubscriber
import com.paranid5.crescendo.domain.stream.StreamRepository
import com.paranid5.crescendo.domain.stream.VideoMetadataApi
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val streamModule = module {
    singleOf(::CurrentMetadataSubscriberImpl) bind CurrentMetadataSubscriber::class
    singleOf(::CurrentMetadataPublisherImpl) bind CurrentMetadataPublisher::class
    singleOf(::DownloadingUrlSubscriberImpl) bind DownloadingUrlSubscriber::class
    singleOf(::DownloadingUrlPublisherImpl) bind DownloadingUrlPublisher::class
    singleOf(::PlayingStreamUrlSubscriberImpl) bind PlayingStreamUrlSubscriber::class
    singleOf(::PlayingStreamUrlPublisherImpl) bind PlayingStreamUrlPublisher::class
    singleOf(::VideoMetadataApiImpl) bind VideoMetadataApi::class
    singleOf(::StreamRepositoryImpl) bind StreamRepository::class
}
