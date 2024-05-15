package com.paranid5.crescendo.trimmer.presentation

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.sources.waveform.AmplitudesPublisherImpl
import com.paranid5.crescendo.data.sources.waveform.AmplitudesSubscriberImpl
import com.paranid5.crescendo.domain.sources.waveform.AmplitudesPublisher
import com.paranid5.crescendo.domain.sources.waveform.AmplitudesSubscriber
import com.paranid5.crescendo.trimmer.data.FocusEventDataSource
import com.paranid5.crescendo.trimmer.data.FocusEventDataSourceImpl
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
    AmplitudesSubscriber by AmplitudesSubscriberImpl(storageRepository),
    AmplitudesPublisher by AmplitudesPublisherImpl(storageRepository),
    PlaybackPositionsDataSource by PlaybackPositionsDataSourceImpl(),
    PlaybackDataSource by PlaybackDataSourceImpl(),
    TrackDataSource by TrackDataSourceImpl(),
    WaveformZoomDataSource by WaveformZoomDataSourceImpl(),
    ShownEffectsDataSource by ShownEffectsDataSourceImpl(),
    FocusEventDataSource by FocusEventDataSourceImpl() {
    val resetPlaybackPosCondVar by lazy { AsyncCondVar() }

    override fun onCleared() {
        super.onCleared()
        setAmplitudesAsync(persistentListOf())
        releasePlaybackPosMonitorTask()
    }
}