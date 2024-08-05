package com.paranid5.crescendo.utils.extensions

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

inline fun <T> MutableStateFlow<T>.updateState(function: T.() -> T) = update { function(it) }
