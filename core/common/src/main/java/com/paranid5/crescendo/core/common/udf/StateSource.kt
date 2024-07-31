package com.paranid5.crescendo.core.common.udf

import kotlinx.coroutines.flow.StateFlow

interface StateSource<State> {
    val stateFlow: StateFlow<State>

    fun updateState(func: State.() -> State)
}
