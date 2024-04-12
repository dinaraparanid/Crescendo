package com.paranid5.crescendo.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.paranid5.crescendo.data.sources.AudioEffectsStateDataSource
import com.paranid5.crescendo.data.sources.PlaybackStateDataSource
import com.paranid5.crescendo.data.sources.StreamStateDataSource
import com.paranid5.crescendo.data.sources.TracksStateDataSource
import com.paranid5.crescendo.data.sources.WaveformStateDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class StorageRepository(context: Context) : CoroutineScope by CoroutineScope(Dispatchers.IO) {
    private val Context.dataStore by preferencesDataStore("params")
    private val dataStore = context.dataStore

    val streamStateDataSource by lazy { StreamStateDataSource(dataStore) }
    val tracksStateDataSource by lazy { TracksStateDataSource(dataStore) }
    val playbackStateDataSource by lazy { PlaybackStateDataSource(dataStore) }
    val audioEffectsStateDataSource by lazy { AudioEffectsStateDataSource(dataStore) }
    val waveformStateDataSource by lazy { WaveformStateDataSource(dataStore) }
}