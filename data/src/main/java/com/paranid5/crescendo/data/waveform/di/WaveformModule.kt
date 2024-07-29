package com.paranid5.crescendo.data.waveform.di

import com.paranid5.crescendo.data.waveform.AmplitudesPublisherImpl
import com.paranid5.crescendo.data.waveform.AmplitudesSubscriberImpl
import com.paranid5.crescendo.data.waveform.WaveformRepositoryImpl
import com.paranid5.crescendo.domain.waveform.AmplitudesPublisher
import com.paranid5.crescendo.domain.waveform.AmplitudesSubscriber
import com.paranid5.crescendo.domain.waveform.WaveformRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val waveformModule = module {
    singleOf(::AmplitudesSubscriberImpl) bind AmplitudesSubscriber::class
    singleOf(::AmplitudesPublisherImpl) bind AmplitudesPublisher::class
    singleOf(::WaveformRepositoryImpl) bind WaveformRepository::class
}
