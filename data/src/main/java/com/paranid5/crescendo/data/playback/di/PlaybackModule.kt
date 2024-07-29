package com.paranid5.crescendo.data.playback.di

import com.paranid5.crescendo.data.playback.AudioSessionIdDataSourceImpl
import com.paranid5.crescendo.data.playback.AudioStatusPublisherImpl
import com.paranid5.crescendo.data.playback.AudioStatusSubscriberImpl
import com.paranid5.crescendo.data.playback.PlaybackRepositoryImpl
import com.paranid5.crescendo.data.playback.PlayingStateDataStoreImpl
import com.paranid5.crescendo.data.playback.RepeatingPublisherImpl
import com.paranid5.crescendo.data.playback.RepeatingSubscriberImpl
import com.paranid5.crescendo.data.playback.StreamPlaybackPositionPublisherImpl
import com.paranid5.crescendo.data.playback.StreamPlaybackPositionSubscriberImpl
import com.paranid5.crescendo.data.playback.TracksPlaybackPositionPublisherImpl
import com.paranid5.crescendo.data.playback.TracksPlaybackPositionSubscriberImpl
import com.paranid5.crescendo.domain.playback.AudioSessionIdPublisher
import com.paranid5.crescendo.domain.playback.AudioSessionIdSubscriber
import com.paranid5.crescendo.domain.playback.AudioStatusPublisher
import com.paranid5.crescendo.domain.playback.AudioStatusSubscriber
import com.paranid5.crescendo.domain.playback.PlaybackRepository
import com.paranid5.crescendo.domain.playback.PlayingStatePublisher
import com.paranid5.crescendo.domain.playback.PlayingStateSubscriber
import com.paranid5.crescendo.domain.playback.RepeatingPublisher
import com.paranid5.crescendo.domain.playback.RepeatingSubscriber
import com.paranid5.crescendo.domain.playback.StreamPlaybackPositionPublisher
import com.paranid5.crescendo.domain.playback.StreamPlaybackPositionSubscriber
import com.paranid5.crescendo.domain.playback.TracksPlaybackPositionPublisher
import com.paranid5.crescendo.domain.playback.TracksPlaybackPositionSubscriber
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

internal val playbackModule = module {
    singleOf(::AudioStatusSubscriberImpl) bind AudioStatusSubscriber::class
    singleOf(::AudioStatusPublisherImpl) bind AudioStatusPublisher::class
    singleOf(::RepeatingSubscriberImpl) bind RepeatingSubscriber::class
    singleOf(::RepeatingPublisherImpl) bind RepeatingPublisher::class
    singleOf(::StreamPlaybackPositionSubscriberImpl) bind StreamPlaybackPositionSubscriber::class
    singleOf(::StreamPlaybackPositionPublisherImpl) bind StreamPlaybackPositionPublisher::class
    singleOf(::TracksPlaybackPositionSubscriberImpl) bind TracksPlaybackPositionSubscriber::class
    singleOf(::TracksPlaybackPositionPublisherImpl) bind TracksPlaybackPositionPublisher::class

    singleOf(::PlayingStateDataStoreImpl) binds
            arrayOf(PlayingStateSubscriber::class, PlayingStatePublisher::class)

    singleOf(::AudioSessionIdDataSourceImpl) binds
            arrayOf(AudioSessionIdSubscriber::class, AudioSessionIdPublisher::class)

    singleOf(::PlaybackRepositoryImpl) bind PlaybackRepository::class
}
