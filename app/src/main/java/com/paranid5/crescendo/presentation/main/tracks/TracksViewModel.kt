package com.paranid5.crescendo.presentation.main.tracks

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.presentation.main.tracks.states.QueryStateHolder
import com.paranid5.crescendo.presentation.main.tracks.states.SearchBarStateHolder
import com.paranid5.crescendo.presentation.main.tracks.states.TracksStateHolder

class TracksViewModel(private val storageHandler: StorageHandler) : ViewModel() {
    val tracksStateHolder by lazy {
        TracksStateHolder(storageHandler)
    }

    val queryStateHolder by lazy {
        QueryStateHolder()
    }

    val searchBarStateHolder by lazy {
        SearchBarStateHolder()
    }
}