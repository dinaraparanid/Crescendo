package com.paranid5.crescendo.domain

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.paranid5.crescendo.data.VideoMetadata
import com.paranid5.crescendo.data.eq.EqualizerData
import com.paranid5.crescendo.data.eq.EqualizerParameters
import com.paranid5.crescendo.data.tracks.DefaultTrack
import com.paranid5.crescendo.data.tracks.TrackOrder
import com.paranid5.crescendo.presentation.ui.AudioStatus
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
        private val TAG = StorageHandler::class.simpleName!!

        private val CURRENT_URL = stringPreferencesKey("current_url")
        private val CURRENT_METADATA = stringPreferencesKey("current_metadata")

        private val CURRENT_TRACK_INDEX = intPreferencesKey("current_track_index")
        private val CURRENT_PLAYLIST = stringPreferencesKey("current_playlist")

        private val TRACK_ORDER = byteArrayPreferencesKey("track_order")

        private val PLAYBACK_POSITION = longPreferencesKey("playback_position")
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
    }

    private val Context.dataStore by preferencesDataStore("params")
    private val dataStore = context.dataStore

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentUrlFlow = dataStore.data
        .mapLatest { preferences -> preferences[CURRENT_URL] }
        .mapLatest { it ?: "" }

    val currentUrlState = currentUrlFlow
        .stateIn(this, SharingStarted.Eagerly, "")

    internal suspend inline fun storeCurrentUrl(url: String) {
        dataStore.edit { preferences -> preferences[CURRENT_URL] = url }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentMetadataFlow = dataStore.data
        .mapLatest { preferences -> preferences[CURRENT_METADATA] }
        .mapLatest { metaString -> metaString?.let { Json.decodeFromString<VideoMetadata>(it) } }

    val currentMetadataState = currentMetadataFlow
        .stateIn(this, SharingStarted.Eagerly, null)

    internal suspend inline fun storeCurrentMetadata(metadata: VideoMetadata?) {
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

    internal suspend inline fun storeCurrentTrackIndex(index: Int) {
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

    internal suspend inline fun storeCurrentPlaylist(playlist: List<DefaultTrack>) {
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

    internal suspend inline fun storeTrackOrder(trackOrder: TrackOrder) {
        dataStore.edit { preferences ->
            preferences[TRACK_ORDER] = byteArrayOf(
                trackOrder.contentOrder.ordinal.toByte(),
                trackOrder.orderType.ordinal.toByte()
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val playbackPositionFlow = dataStore.data
        .mapLatest { preferences -> preferences[PLAYBACK_POSITION] }
        .mapLatest { it ?: 0 }

    val playbackPositionState = playbackPositionFlow
        .stateIn(this, SharingStarted.Eagerly, 0)

    internal suspend inline fun storePlaybackPosition(position: Long) {
        dataStore.edit { preferences -> preferences[PLAYBACK_POSITION] = position }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val isRepeatingFlow = dataStore.data
        .mapLatest { preferences -> preferences[IS_REPEATING] }
        .mapLatest { it ?: false }

    val isRepeatingState = isRepeatingFlow
        .stateIn(this, SharingStarted.Eagerly, false)

    internal suspend inline fun storeIsRepeating(isRepeating: Boolean) {
        dataStore.edit { preferences -> preferences[IS_REPEATING] = isRepeating }
    }

    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalStdlibApi::class)
    val audioStatusFlow = dataStore.data
        .mapLatest { preferences -> preferences[AUDIO_STATUS] }
        .mapLatest { audioStatusInd -> audioStatusInd?.let { AudioStatus.entries[it] } }

    val audioStatusState = audioStatusFlow
        .stateIn(this, SharingStarted.Eagerly, null)

    internal suspend inline fun storeAudioStatus(audioStatus: AudioStatus) {
        dataStore.edit { preferences -> preferences[AUDIO_STATUS] = audioStatus.ordinal }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val areAudioEffectsEnabledFlow = dataStore.data
        .mapLatest { preferences -> preferences[AUDIO_EFFECTS_ENABLED] }
        .mapLatest { it ?: false }

    val areAudioEffectsEnabledState = areAudioEffectsEnabledFlow
        .stateIn(this, SharingStarted.Eagerly, false)

    internal suspend inline fun storeAudioEffectsEnabled(areAudioEffectsEnabled: Boolean) {
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

    internal suspend inline fun storePitch(pitch: Float) {
        dataStore.edit { preferences -> preferences[PITCH_VALUE] = pitch }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val speedFlow = dataStore.data
        .mapLatest { preferences -> preferences[SPEED_VALUE] }
        .mapLatest { it ?: 1.0F }

    val speedState = speedFlow
        .stateIn(this, SharingStarted.Eagerly, 1.0F)

    internal suspend inline fun storeSpeed(speed: Float) {
        dataStore.edit { preferences -> preferences[SPEED_VALUE] = speed }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val equalizerBandsFlow = dataStore.data
        .mapLatest { preferences -> preferences[EQ_BANDS] }
        .mapLatest { bandsStr -> bandsStr?.let { Json.decodeFromString<List<Short>?>(it) } }

    val equalizerBandsState = equalizerBandsFlow
        .stateIn(this, SharingStarted.Eagerly, null)

    internal suspend inline fun storeEqualizerBands(bands: List<Short>) {
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

    internal suspend inline fun storeEqualizerPreset(preset: Short) {
        dataStore.edit { preferences -> preferences[EQ_PRESET] = preset.toInt() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val equalizerParamFlow = dataStore.data
        .mapLatest { preferences -> preferences[EQ_PARAM] }
        .mapLatest { param -> EqualizerParameters.values()[param ?: 0] }

    val equalizerParamState = equalizerParamFlow
        .stateIn(this, SharingStarted.Eagerly, EqualizerParameters.NIL)

    internal suspend inline fun storeEqualizerParam(param: EqualizerParameters) {
        dataStore.edit { preferences -> preferences[EQ_PARAM] = param.ordinal }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val bassStrengthFlow = dataStore.data
        .mapLatest { preferences -> preferences[BASS_STRENGTH] }
        .mapLatest { strength -> strength?.toShort() ?: 0 }

    val bassStrengthState = bassStrengthFlow.stateIn(this, SharingStarted.Eagerly, 0)

    internal suspend inline fun storeBassStrength(bassStrength: Short) {
        dataStore.edit { preferences -> preferences[BASS_STRENGTH] = bassStrength.toInt() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val reverbPresetFlow = dataStore.data
        .mapLatest { preferences -> preferences[REVERB_PRESET] }
        .mapLatest { preset -> preset?.toShort() ?: 0 }

    val reverbPresetState = reverbPresetFlow.stateIn(this, SharingStarted.Eagerly, 0)

    internal suspend inline fun storeReverbPreset(reverbPreset: Short) {
        dataStore.edit { preferences -> preferences[REVERB_PRESET] = reverbPreset.toInt() }
    }
}