package com.paranid5.crescendo.data.tracks.di

import com.paranid5.crescendo.data.tracks.CurrentTrackIndexPublisherImpl
import com.paranid5.crescendo.data.tracks.CurrentTrackIndexSubscriberImpl
import com.paranid5.crescendo.data.tracks.CurrentTrackSubscriberImpl
import com.paranid5.crescendo.data.tracks.TrackOrderPublisherImpl
import com.paranid5.crescendo.data.tracks.TrackOrderSubscriberImpl
import com.paranid5.crescendo.data.tracks.TracksMediaStoreSubscriberImpl
import com.paranid5.crescendo.data.tracks.TracksRepositoryImpl
import com.paranid5.crescendo.domain.tracks.CurrentTrackIndexPublisher
import com.paranid5.crescendo.domain.tracks.CurrentTrackIndexSubscriber
import com.paranid5.crescendo.domain.tracks.CurrentTrackSubscriber
import com.paranid5.crescendo.domain.tracks.TrackOrderPublisher
import com.paranid5.crescendo.domain.tracks.TrackOrderSubscriber
import com.paranid5.crescendo.domain.tracks.TracksMediaStoreSubscriber
import com.paranid5.crescendo.domain.tracks.TracksRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val tracksModule = module {
    singleOf(::CurrentTrackIndexSubscriberImpl) bind CurrentTrackIndexSubscriber::class
    singleOf(::CurrentTrackIndexPublisherImpl) bind CurrentTrackIndexPublisher::class
    singleOf(::CurrentTrackSubscriberImpl) bind CurrentTrackSubscriber::class
    singleOf(::TrackOrderSubscriberImpl) bind TrackOrderSubscriber::class
    singleOf(::TrackOrderPublisherImpl) bind TrackOrderPublisher::class
    singleOf(::TracksMediaStoreSubscriberImpl) bind TracksMediaStoreSubscriber::class
    singleOf(::TracksRepositoryImpl) bind TracksRepository::class
}
