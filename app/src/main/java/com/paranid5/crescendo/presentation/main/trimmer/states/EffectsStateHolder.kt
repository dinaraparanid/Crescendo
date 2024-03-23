package com.paranid5.crescendo.presentation.main.trimmer.states

import com.paranid5.crescendo.presentation.main.trimmer.entities.ShownEffects
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

interface EffectsStateHolder {
    val shownEffectsOrdState: StateFlow<Int>
    fun setShownEffectsOrd(shownEffectsOrd: Int)
}

class EffectsStateHolderImpl : EffectsStateHolder {
    private val _shownEffectsOrdState by lazy {
        MutableStateFlow(0)
    }

    override val shownEffectsOrdState by lazy {
        _shownEffectsOrdState.asStateFlow()
    }

    override fun setShownEffectsOrd(shownEffectsOrd: Int) =
        _shownEffectsOrdState.update { shownEffectsOrd }
}

inline val EffectsStateHolder.shownEffectsFlow
    get() = shownEffectsOrdState.map { ShownEffects.entries[it] }