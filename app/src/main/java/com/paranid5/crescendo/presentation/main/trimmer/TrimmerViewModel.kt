package com.paranid5.crescendo.presentation.main.trimmer

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.states.waveform.AmplitudesStatePublisher
import com.paranid5.crescendo.data.states.waveform.AmplitudesStatePublisherImpl
import com.paranid5.crescendo.data.states.waveform.AmplitudesStateSubscriber
import com.paranid5.crescendo.data.states.waveform.AmplitudesStateSubscriberImpl
import com.paranid5.crescendo.utils.AsyncCondVar
import com.paranid5.crescendo.presentation.main.trimmer.properties.setAmplitudesAsync
import com.paranid5.crescendo.presentation.main.trimmer.states.EffectsStateHolder
import com.paranid5.crescendo.presentation.main.trimmer.states.EffectsStateHolderImpl
import com.paranid5.crescendo.presentation.main.trimmer.states.PlaybackPositionsStateHolder
import com.paranid5.crescendo.presentation.main.trimmer.states.PlaybackPositionsStateHolderImpl
import com.paranid5.crescendo.presentation.main.trimmer.states.PlaybackStateHolder
import com.paranid5.crescendo.presentation.main.trimmer.states.PlaybackStateHolderImpl
import com.paranid5.crescendo.presentation.main.trimmer.states.TrackStateHolder
import com.paranid5.crescendo.presentation.main.trimmer.states.TrackStateHolderImpl
import com.paranid5.crescendo.presentation.main.trimmer.states.WaveformStateHolder
import com.paranid5.crescendo.presentation.main.trimmer.states.WaveformStateHolderImpl
import kotlinx.collections.immutable.persistentListOf

class TrimmerViewModel(storageRepository: StorageRepository) : ViewModel(),
    AmplitudesStateSubscriber by AmplitudesStateSubscriberImpl(storageRepository),
    AmplitudesStatePublisher by AmplitudesStatePublisherImpl(storageRepository),
    PlaybackPositionsStateHolder by PlaybackPositionsStateHolderImpl(),
    PlaybackStateHolder by PlaybackStateHolderImpl(),
    TrackStateHolder by TrackStateHolderImpl(),
    WaveformStateHolder by WaveformStateHolderImpl(),
    EffectsStateHolder by EffectsStateHolderImpl() {
    val resetPlaybackPosCondVar by lazy { AsyncCondVar() }

    override fun onCleared() {
        super.onCleared()
        setAmplitudesAsync(persistentListOf())
        releasePlaybackPosMonitorTask()
    }
}