package com.paranid5.crescendo.core.common.udf

interface StatePublisher<State> {
    fun updateState(func: State.() -> State)
}
