package com.paranid5.crescendo.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.paranid5.crescendo.data.states.AudioEffectsStateProvider
import com.paranid5.crescendo.data.states.PlaybackStateProvider
import com.paranid5.crescendo.data.states.StreamStateProvider
import com.paranid5.crescendo.data.states.TracksStateProvider
import com.paranid5.crescendo.data.states.WaveformStateProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class StorageHandler(context: Context) : CoroutineScope by CoroutineScope(Dispatchers.IO) {
    private val Context.dataStore by preferencesDataStore("params")
    private val dataStore = context.dataStore

    val streamStateProvider by lazy { StreamStateProvider(dataStore) }
    val tracksStateProvider by lazy { TracksStateProvider(dataStore) }
    val playbackStateProvider by lazy { PlaybackStateProvider(dataStore) }
    val audioEffectsStateProvider by lazy { AudioEffectsStateProvider(dataStore) }
    val waveformStateProvider by lazy { WaveformStateProvider(dataStore) }
}