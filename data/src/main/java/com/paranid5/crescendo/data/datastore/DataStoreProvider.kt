package com.paranid5.crescendo.data.datastore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

internal class DataStoreProvider(context: Context) {
    private val Context.dataStore by preferencesDataStore("params")
    val dataStore = context.dataStore
}
