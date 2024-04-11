package com.paranid5.crescendo.trimmer.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.core.common.caching.Formats
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.core.common.trimming.FadeDurations
import com.paranid5.crescendo.core.common.trimming.PitchAndSpeed
import com.paranid5.crescendo.core.common.trimming.TrimRange
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.states.waveform.AmplitudesStatePublisher
import com.paranid5.crescendo.data.states.waveform.AmplitudesStatePublisherImpl
import com.paranid5.crescendo.data.states.waveform.AmplitudesStateSubscriber
import com.paranid5.crescendo.data.states.waveform.AmplitudesStateSubscriberImpl
import com.paranid5.crescendo.trimmer.domain.TrimmerInteractor
import com.paranid5.crescendo.trimmer.presentation.properties.setAmplitudesAsync
import com.paranid5.crescendo.trimmer.data.PlaybackPositionsDataSource
import com.paranid5.crescendo.trimmer.data.PlaybackPositionsDataSourceImpl
import com.paranid5.crescendo.trimmer.data.PlaybackDataSource
import com.paranid5.crescendo.trimmer.data.PlaybackDataSourceImpl
import com.paranid5.crescendo.trimmer.data.ShownEffectsDataSource
import com.paranid5.crescendo.trimmer.data.ShownEffectsDataSourceImpl
import com.paranid5.crescendo.trimmer.data.TrackDataSource
import com.paranid5.crescendo.trimmer.data.TrackDataSourceImpl
import com.paranid5.crescendo.trimmer.data.WaveformZoomDataSource
import com.paranid5.crescendo.trimmer.data.WaveformZoomDataSourceImpl
import com.paranid5.crescendo.utils.AsyncCondVar
import kotlinx.collections.immutable.persistentListOf

class TrimmerViewModel(
    storageRepository: StorageRepository,
    private val trimmerInteractor: TrimmerInteractor
) : ViewModel(),
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

    internal suspend fun trimTrackAndSendBroadcast(
        context: Context,
        track: Track,
        outputFilename: String,
        audioFormat: Formats,
        trimRange: TrimRange,
        pitchAndSpeed: PitchAndSpeed,
        fadeDurations: FadeDurations
    ) = trimmerInteractor.trimTrackAndSendBroadcast(
        context = context,
        track = track,
        outputFilename = outputFilename,
        audioFormat = audioFormat,
        trimRange = trimRange,
        pitchAndSpeed = pitchAndSpeed,
        fadeDurations = fadeDurations
    )
}