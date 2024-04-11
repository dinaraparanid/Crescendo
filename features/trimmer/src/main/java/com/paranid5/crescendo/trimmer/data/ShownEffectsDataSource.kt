package com.paranid5.crescendo.trimmer.data

import com.paranid5.crescendo.trimmer.domain.entities.ShownEffects
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

internal interface ShownEffectsDataSource {
    val shownEffectsOrdState: StateFlow<Int>
    fun setShownEffectsOrd(shownEffectsOrd: Int)
}

internal class ShownEffectsDataSourceImpl : ShownEffectsDataSource {
    private val _shownEffectsOrdState by lazy {
        MutableStateFlow(0)
    }

    override val shownEffectsOrdState by lazy {
        _shownEffectsOrdState.asStateFlow()
    }

    override fun setShownEffectsOrd(shownEffectsOrd: Int) =
        _shownEffectsOrdState.update { shownEffectsOrd }
}

internal inline val ShownEffectsDataSource.shownEffectsFlow
    get() = shownEffectsOrdState.map { ShownEffects.entries[it] }

internal fun ShownEffectsDataSource.setShownEffects(effects: ShownEffects) =
    setShownEffectsOrd(effects.ordinal)