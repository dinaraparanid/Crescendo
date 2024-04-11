package com.paranid5.crescendo.tracks.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal interface SearchBarActiveDataSource {
    val isSearchBarActiveState: StateFlow<Boolean>
    fun setSearchBarActive(isActive: Boolean)
}

internal class SearchBarActiveDataSourceImpl : SearchBarActiveDataSource {
    private val _isSearchBarActiveState by lazy {
        MutableStateFlow(false)
    }

    override val isSearchBarActiveState by lazy {
        _isSearchBarActiveState.asStateFlow()
    }

    override fun setSearchBarActive(isActive: Boolean) =
        _isSearchBarActiveState.update { isActive }
}