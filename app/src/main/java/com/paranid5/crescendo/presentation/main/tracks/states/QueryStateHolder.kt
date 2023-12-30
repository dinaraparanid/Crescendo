package com.paranid5.crescendo.presentation.main.tracks.states

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class QueryStateHolder {
    private val _queryState by lazy {
        MutableStateFlow<String?>(null)
    }

    val queryState by lazy {
        _queryState.asStateFlow()
    }

    fun setQuery(query: String?) =
        _queryState.update { query }
}