package com.paranid5.crescendo.presentation.main.tracks.states

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SearchBarStateHolder {
    private val _isSearchBarActiveState by lazy {
        MutableStateFlow(false)
    }

    val isSearchBarActiveState by lazy {
        _isSearchBarActiveState.asStateFlow()
    }

    fun setSearchBarActive(isActive: Boolean) =
        _isSearchBarActiveState.update { isActive }
}