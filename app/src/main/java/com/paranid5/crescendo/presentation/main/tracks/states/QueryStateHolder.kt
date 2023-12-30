package com.paranid5.crescendo.presentation.main.tracks.states

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface QueryStateHolder {
    val queryState: StateFlow<String?>
    fun setQuery(query: String?)
}

class QueryStateHolderImpl : QueryStateHolder {
    private val _queryState by lazy {
        MutableStateFlow<String?>(null)
    }

    override val queryState by lazy {
        _queryState.asStateFlow()
    }

    override fun setQuery(query: String?) =
        _queryState.update { query }
}