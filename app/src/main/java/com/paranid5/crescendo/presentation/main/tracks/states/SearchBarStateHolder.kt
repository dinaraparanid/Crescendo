package com.paranid5.crescendo.presentation.main.tracks.states

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface SearchBarStateHolder {
    val isSearchBarActiveState: StateFlow<Boolean>
    fun setSearchBarActive(isActive: Boolean)
}

class SearchBarStateHolderImpl : SearchBarStateHolder {
    private val _isSearchBarActiveState by lazy {
        MutableStateFlow(false)
    }

    override val isSearchBarActiveState by lazy {
        _isSearchBarActiveState.asStateFlow()
    }

    override fun setSearchBarActive(isActive: Boolean) =
        _isSearchBarActiveState.update { isActive }
}