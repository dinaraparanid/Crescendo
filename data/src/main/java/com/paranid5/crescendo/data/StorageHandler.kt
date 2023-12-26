package com.paranid5.crescendo.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.paranid5.crescendo.data.properties.areAudioEffectsEnabledFlow
import com.paranid5.crescendo.data.properties.audioStatusFlow
import com.paranid5.crescendo.data.properties.bassStrengthFlow
import com.paranid5.crescendo.data.properties.currentMetadataFlow
import com.paranid5.crescendo.data.properties.currentPlaylistFlow
import com.paranid5.crescendo.data.properties.currentTrackFlow
import com.paranid5.crescendo.data.properties.currentTrackIndexFlow
import com.paranid5.crescendo.data.properties.currentUrlFlow
import com.paranid5.crescendo.data.properties.equalizerBandsFlow
import com.paranid5.crescendo.data.properties.equalizerParamFlow
import com.paranid5.crescendo.data.properties.equalizerPresetFlow
import com.paranid5.crescendo.data.properties.isRepeatingFlow
import com.paranid5.crescendo.data.properties.pitchFlow
import com.paranid5.crescendo.data.properties.reverbPresetFlow
import com.paranid5.crescendo.data.properties.speedFlow
import com.paranid5.crescendo.data.properties.streamPlaybackPositionFlow
import com.paranid5.crescendo.data.properties.trackOrderFlow
import com.paranid5.crescendo.data.properties.tracksPlaybackPositionFlow
import com.paranid5.crescendo.data.states.AudioEffectsStateProvider
import com.paranid5.crescendo.data.states.PlaybackStateProvider
import com.paranid5.crescendo.data.states.StreamStateProvider
import com.paranid5.crescendo.data.states.TracksStateProvider
import com.paranid5.crescendo.data.states.WaveformStateProvider
import com.paranid5.crescendo.domain.eq.EqualizerBandsPreset
import com.paranid5.crescendo.domain.tracks.TrackOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class StorageHandler(context: Context) : CoroutineScope by CoroutineScope(Dispatchers.IO) {
    private val Context.dataStore by preferencesDataStore("params")
    private val dataStore = context.dataStore

    val streamStateProvider by lazy { StreamStateProvider(dataStore) }
    val tracksStateProvider by lazy { TracksStateProvider(dataStore) }
    val playbackStateProvider by lazy { PlaybackStateProvider(dataStore) }
    val audioEffectsStateProvider by lazy { AudioEffectsStateProvider(dataStore) }
    val waveformStateProvider by lazy { WaveformStateProvider(dataStore) }

    // ------------------ TODO: Remove state flows ------------------

    val areAudioEffectsEnabledState = areAudioEffectsEnabledFlow
        .stateIn(this, SharingStarted.Eagerly, false)

    val pitchState = pitchFlow
        .stateIn(this, SharingStarted.Eagerly, 1F)

    val speedState = speedFlow
        .stateIn(this, SharingStarted.Eagerly, 1F)

    val equalizerBandsState = equalizerBandsFlow
        .stateIn(this, SharingStarted.Eagerly, emptyList())

    val equalizerPresetState = equalizerPresetFlow
        .stateIn(this, SharingStarted.Eagerly, 0)

    val equalizerParamState = equalizerParamFlow
        .stateIn(this, SharingStarted.Eagerly, EqualizerBandsPreset.NIL)

    val bassStrengthState = bassStrengthFlow
        .stateIn(this, SharingStarted.Eagerly, 0)

    val reverbPresetState = reverbPresetFlow
        .stateIn(this, SharingStarted.Eagerly, 0)

    val tracksPlaybackPositionState = tracksPlaybackPositionFlow
        .stateIn(this, SharingStarted.Eagerly, 0)

    val streamPlaybackPositionState = streamPlaybackPositionFlow
        .stateIn(this, SharingStarted.Eagerly, 0)

    val isRepeatingState = isRepeatingFlow
        .stateIn(this, SharingStarted.Eagerly, false)

    val audioStatusState = audioStatusFlow
        .stateIn(this, SharingStarted.Eagerly, null)

    val currentUrlState = currentUrlFlow
        .stateIn(this, SharingStarted.Eagerly, "")

    val currentMetadataState = currentMetadataFlow
        .stateIn(this, SharingStarted.Eagerly, null)

    val currentTrackIndexState = currentTrackIndexFlow
        .stateIn(this, SharingStarted.Eagerly, 0)

    val currentPlaylistState = currentPlaylistFlow
        .stateIn(this, SharingStarted.Eagerly, emptyList())

    val currentTrackState = currentTrackFlow
        .stateIn(this, SharingStarted.Eagerly, null)

    val trackOrderState = trackOrderFlow
        .stateIn(this, SharingStarted.Eagerly, TrackOrder.default)
}