package com.paranid5.crescendo.trimmer.presentation

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.sources.waveform.AmplitudesStatePublisher
import com.paranid5.crescendo.data.sources.waveform.AmplitudesStatePublisherImpl
import com.paranid5.crescendo.data.sources.waveform.AmplitudesStateSubscriber
import com.paranid5.crescendo.data.sources.waveform.AmplitudesStateSubscriberImpl
import com.paranid5.crescendo.trimmer.data.PlaybackDataSource
import com.paranid5.crescendo.trimmer.data.PlaybackDataSourceImpl
import com.paranid5.crescendo.trimmer.data.PlaybackPositionsDataSource
import com.paranid5.crescendo.trimmer.data.PlaybackPositionsDataSourceImpl
import com.paranid5.crescendo.trimmer.data.ShownEffectsDataSource
import com.paranid5.crescendo.trimmer.data.ShownEffectsDataSourceImpl
import com.paranid5.crescendo.trimmer.data.TrackDataSource
import com.paranid5.crescendo.trimmer.data.TrackDataSourceImpl
import com.paranid5.crescendo.trimmer.data.WaveformZoomDataSource
import com.paranid5.crescendo.trimmer.data.WaveformZoomDataSourceImpl
import com.paranid5.crescendo.trimmer.presentation.properties.setAmplitudesAsync
import com.paranid5.crescendo.utils.AsyncCondVar
import kotlinx.collections.immutable.persistentListOf

class TrimmerViewModel(storageRepository: StorageRepository) : ViewModel(),
    AmplitudesStateSubscriber by AmplitudesStateSubscriberImpl(storageRepository),
    AmplitudesStatePublisher by AmplitudesStatePublisherImpl(storageRepository),
    PlaybackPositionsDataSource by PlaybackPositionsDataSourceImpl(),
    PlaybackDataSource by PlaybackDataSourceImpl(),
    TrackDataSource by TrackDataSourceImpl(),
    WaveformZoomDataSource by WaveformZoomDataSourceImpl(),
    ShownEffectsDataSource by ShownEffectsDataSourceImpl() {
    val resetPlaybackPosCondVar by lazy { AsyncCondVar() }

    override fun onCleared() {
        super.onCleared()
        setAmplitudesAsync(persistentListOf())
        releasePlaybackPosMonitorTask()
    }
}