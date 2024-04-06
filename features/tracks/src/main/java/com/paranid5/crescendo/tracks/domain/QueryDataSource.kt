package com.paranid5.crescendo.tracks.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal interface QueryDataSource {
    val queryState: StateFlow<String?>
    fun setQuery(query: String?)
}

internal class QueryDataSourceImpl : QueryDataSource {
    private val _queryState by lazy {
        MutableStateFlow<String?>(null)
    }

    override val queryState by lazy {
        _queryState.asStateFlow()
    }

    override fun setQuery(query: String?) =
        _queryState.update { query }
}