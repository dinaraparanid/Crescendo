package com.paranid5.crescendo.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.paranid5.crescendo.domain.VideoMetadata
import com.paranid5.crescendo.domain.eq.EqualizerData
import com.paranid5.crescendo.domain.eq.EqualizerBandsPreset
import com.paranid5.crescendo.domain.media.AudioStatus
import com.paranid5.crescendo.domain.tracks.DefaultTrack
import com.paranid5.crescendo.domain.tracks.TrackOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class StorageHandler(context: Context) :
    CoroutineScope by CoroutineScope(Dispatchers.IO) {
    companion object {
        private val CURRENT_URL = stringPreferencesKey("current_url")
        private val CURRENT_METADATA = stringPreferencesKey("current_metadata")

        private val CURRENT_TRACK_INDEX = intPreferencesKey("current_track_index")
        private val CURRENT_PLAYLIST = stringPreferencesKey("current_playlist")

        private val TRACK_ORDER = byteArrayPreferencesKey("track_order")

        private val TRACKS_PLAYBACK_POSITION = longPreferencesKey("tracks_playback_position")
        private val STREAM_PLAYBACK_POSITION = longPreferencesKey("stream_playback_position")

        private val IS_REPEATING = booleanPreferencesKey("is_repeating")
        private val AUDIO_STATUS = intPreferencesKey("audio_status")

        private val AUDIO_EFFECTS_ENABLED = booleanPreferencesKey("audio_effects_enabled")
        private val PITCH_VALUE = floatPreferencesKey("pitch_value")
        private val SPEED_VALUE = floatPreferencesKey("speed_value")

        private val EQ_PARAM = intPreferencesKey("eq_param")
        private val EQ_BANDS = stringPreferencesKey("eq_bands")
        private val EQ_PRESET = intPreferencesKey("eq_preset")

        private val BASS_STRENGTH = intPreferencesKey("bass_strength")
        private val REVERB_PRESET = intPreferencesKey("reverb_preset")

        private val AMPLITUDES = stringPreferencesKey("amplitudes")
    }

    private val Context.dataStore by preferencesDataStore("params")
    private val dataStore = context.dataStore

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentUrlFlow = dataStore.data
        .mapLatest { preferences -> preferences[CURRENT_URL] }
        .mapLatest { it ?: "" }

    val currentUrlState = currentUrlFlow
        .stateIn(this, SharingStarted.Eagerly, "")

    suspend fun storeCurrentUrl(url: String) {
        dataStore.edit { preferences -> preferences[CURRENT_URL] = url }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentMetadataFlow = dataStore.data
        .mapLatest { preferences -> preferences[CURRENT_METADATA] }
        .mapLatest { metaString -> metaString?.let { Json.decodeFromString<VideoMetadata>(it) } }

    val currentMetadataState = currentMetadataFlow
        .stateIn(this, SharingStarted.Eagerly, null)

    suspend fun storeCurrentMetadata(metadata: VideoMetadata?) {
        dataStore.edit { preferences ->
            preferences[CURRENT_METADATA] = Json.encodeToString(metadata)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentTrackIndexFlow = dataStore.data
        .mapLatest { preferences -> preferences[CURRENT_TRACK_INDEX] }
        .mapLatest { it ?: 0 }

    val currentTrackIndexState = currentTrackIndexFlow
        .stateIn(this, SharingStarted.Eagerly, 0)

    suspend fun storeCurrentTrackIndex(index: Int) {
        dataStore.edit { preferences ->
            preferences[CURRENT_TRACK_INDEX] = index
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentPlaylistFlow = dataStore.data
        .mapLatest { preferences -> preferences[CURRENT_PLAYLIST] }
        .mapLatest { trackStr -> trackStr?.let { Json.decodeFromString<List<DefaultTrack>>(it) } }

    val currentPlaylistState = currentPlaylistFlow
        .stateIn(this, SharingStarted.Eagerly, null)

    suspend fun storeCurrentPlaylist(playlist: List<DefaultTrack>) {
        dataStore.edit { preferences ->
            preferences[CURRENT_PLAYLIST] = Json.encodeToString(playlist)
        }
    }

    val currentTrackState =
        combine(currentTrackIndexFlow, currentPlaylistFlow) { trackInd, playlist ->
            playlist?.getOrNull(trackInd)
        }.stateIn(this, SharingStarted.Eagerly, null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val trackOrderFlow = dataStore.data
        .mapLatest { preferences -> preferences[TRACK_ORDER] }
        .mapLatest { trackOrder ->
            trackOrder?.let {
                val (contentOrder, orderType) = it

                TrackOrder(
                    contentOrder = TrackOrder.TrackContentOrder.entries[contentOrder.toInt()],
                    orderType = TrackOrder.TrackOrderType.entries[orderType.toInt()]
                )
            } ?: TrackOrder.default
        }

    val trackOrderState = trackOrderFlow
        .stateIn(this, SharingStarted.Eagerly, TrackOrder.default)

    suspend fun storeTrackOrder(trackOrder: TrackOrder) {
        dataStore.edit { preferences ->
            preferences[TRACK_ORDER] = byteArrayOf(
                trackOrder.contentOrder.ordinal.toByte(),
                trackOrder.orderType.ordinal.toByte()
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val tracksPlaybackPositionFlow = dataStore.data
        .mapLatest { preferences -> preferences[TRACKS_PLAYBACK_POSITION] }
        .mapLatest { it ?: 0 }

    val tracksPlaybackPositionState = tracksPlaybackPositionFlow
        .stateIn(this, SharingStarted.Eagerly, 0)

    suspend fun storeTracksPlaybackPosition(position: Long) {
        dataStore.edit { preferences -> preferences[TRACKS_PLAYBACK_POSITION] = position }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val streamPlaybackPositionFlow = dataStore.data
        .mapLatest { preferences -> preferences[STREAM_PLAYBACK_POSITION] }
        .mapLatest { it ?: 0 }

    val streamPlaybackPositionState = streamPlaybackPositionFlow
        .stateIn(this, SharingStarted.Eagerly, 0)

    suspend fun storeStreamPlaybackPosition(position: Long) {
        dataStore.edit { preferences -> preferences[STREAM_PLAYBACK_POSITION] = position }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val isRepeatingFlow = dataStore.data
        .mapLatest { preferences -> preferences[IS_REPEATING] }
        .mapLatest { it ?: false }

    val isRepeatingState = isRepeatingFlow
        .stateIn(this, SharingStarted.Eagerly, false)

    suspend fun storeIsRepeating(isRepeating: Boolean) {
        dataStore.edit { preferences -> preferences[IS_REPEATING] = isRepeating }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val audioStatusFlow = dataStore.data
        .mapLatest { preferences -> preferences[AUDIO_STATUS] }
        .mapLatest { audioStatusInd -> audioStatusInd?.let { AudioStatus.entries[it] } }

    val audioStatusState = audioStatusFlow
        .stateIn(this, SharingStarted.Eagerly, null)

    suspend fun storeAudioStatus(audioStatus: AudioStatus) {
        dataStore.edit { preferences -> preferences[AUDIO_STATUS] = audioStatus.ordinal }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val areAudioEffectsEnabledFlow = dataStore.data
        .mapLatest { preferences -> preferences[AUDIO_EFFECTS_ENABLED] }
        .mapLatest { it ?: false }

    val areAudioEffectsEnabledState = areAudioEffectsEnabledFlow
        .stateIn(this, SharingStarted.Eagerly, false)

    suspend fun storeAudioEffectsEnabled(areAudioEffectsEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[AUDIO_EFFECTS_ENABLED] = areAudioEffectsEnabled
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val pitchFlow = dataStore.data
        .mapLatest { preferences -> preferences[PITCH_VALUE] }
        .mapLatest { it ?: 1.0F }

    val pitchState = pitchFlow
        .stateIn(this, SharingStarted.Eagerly, 1.0F)

    suspend fun storePitch(pitch: Float) {
        dataStore.edit { preferences -> preferences[PITCH_VALUE] = pitch }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val speedFlow = dataStore.data
        .mapLatest { preferences -> preferences[SPEED_VALUE] }
        .mapLatest { it ?: 1.0F }

    val speedState = speedFlow
        .stateIn(this, SharingStarted.Eagerly, 1.0F)

    suspend fun storeSpeed(speed: Float) {
        dataStore.edit { preferences -> preferences[SPEED_VALUE] = speed }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val equalizerBandsFlow = dataStore.data
        .mapLatest { preferences -> preferences[EQ_BANDS] }
        .mapLatest { bandsStr -> bandsStr?.let { Json.decodeFromString<List<Short>?>(it) } }

    val equalizerBandsState = equalizerBandsFlow
        .stateIn(this, SharingStarted.Eagerly, null)

    suspend fun storeEqualizerBands(bands: List<Short>) {
        dataStore.edit { preferences ->
            preferences[EQ_BANDS] = Json.encodeToString(bands)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val equalizerPresetFlow = dataStore.data
        .mapLatest { preferences -> preferences[EQ_PRESET] }
        .mapLatest { preset -> preset?.toShort() ?: EqualizerData.NO_EQ_PRESET }

    val equalizerPresetState = equalizerPresetFlow
        .stateIn(this, SharingStarted.Eagerly, EqualizerData.NO_EQ_PRESET)

    suspend fun storeEqualizerPreset(preset: Short) {
        dataStore.edit { preferences -> preferences[EQ_PRESET] = preset.toInt() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val equalizerParamFlow = dataStore.data
        .mapLatest { preferences -> preferences[EQ_PARAM] }
        .mapLatest { param -> EqualizerBandsPreset.entries[param ?: 0] }

    val equalizerParamState = equalizerParamFlow
        .stateIn(this, SharingStarted.Eagerly, EqualizerBandsPreset.NIL)

    suspend fun storeEqualizerParam(param: EqualizerBandsPreset) {
        dataStore.edit { preferences -> preferences[EQ_PARAM] = param.ordinal }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val bassStrengthFlow = dataStore.data
        .mapLatest { preferences -> preferences[BASS_STRENGTH] }
        .mapLatest { strength -> strength?.toShort() ?: 0 }

    val bassStrengthState = bassStrengthFlow.stateIn(this, SharingStarted.Eagerly, 0)

    suspend fun storeBassStrength(bassStrength: Short) {
        dataStore.edit { preferences -> preferences[BASS_STRENGTH] = bassStrength.toInt() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val reverbPresetFlow = dataStore.data
        .mapLatest { preferences -> preferences[REVERB_PRESET] }
        .mapLatest { preset -> preset?.toShort() ?: 0 }

    val reverbPresetState = reverbPresetFlow.stateIn(this, SharingStarted.Eagerly, 0)

    suspend fun storeReverbPreset(reverbPreset: Short) {
        dataStore.edit { preferences -> preferences[REVERB_PRESET] = reverbPreset.toInt() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val amplitudesFlow = dataStore.data
        .mapLatest { preferences -> preferences[AMPLITUDES] }
        .mapLatest { bandsStr -> bandsStr?.let { Json.decodeFromString<List<Int>>(it) } }
        .mapLatest { it ?: emptyList() }

    val amplitudesState = amplitudesFlow
        .stateIn(this, SharingStarted.Eagerly, emptyList())

    suspend fun storeAmplitudes(amplitudes: List<Int>) {
        dataStore.edit { preferences ->
            preferences[AMPLITUDES] = Json.encodeToString(amplitudes)
        }
    }
}