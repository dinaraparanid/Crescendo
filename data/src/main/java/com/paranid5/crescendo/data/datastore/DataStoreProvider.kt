package com.paranid5.crescendo.data.datastore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.paranid5.crescendo.data.datastore.sources.storage.AudioEffectsStateDataSource
import com.paranid5.crescendo.data.datastore.sources.storage.PlaybackStateDataSource
import com.paranid5.crescendo.data.datastore.sources.storage.StreamStateDataSource
import com.paranid5.crescendo.data.datastore.sources.storage.TracksStateDataSource
import com.paranid5.crescendo.data.datastore.sources.storage.WaveformStateDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class DataStoreProvider(context: Context) : CoroutineScope by CoroutineScope(Dispatchers.IO) {
    private val Context.dataStore by preferencesDataStore("params")
    private val dataStore = context.dataStore

    internal val streamStateDataSource by lazy {
        StreamStateDataSource(dataStore)
    }

    internal val tracksStateDataSource by lazy {
        TracksStateDataSource(dataStore)
    }

    internal val playbackStateDataSource by lazy {
        PlaybackStateDataSource(dataStore)
    }

    internal val audioEffectsStateDataSource: AudioEffectsStateDataSource by lazy {
        AudioEffectsStateDataSource(dataStore)
    }

    internal val waveformStateDataSource by lazy {
        WaveformStateDataSource(dataStore)
    }
}