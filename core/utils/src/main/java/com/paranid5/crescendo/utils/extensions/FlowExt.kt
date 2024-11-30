package com.paranid5.crescendo.utils.extensions

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Composable
fun <T : R, R> Flow<T>.collectLatestAsState(
    initial: R,
    context: CoroutineContext = EmptyCoroutineContext,
): State<R> = produceState(initial, this, context) {
    when (context) {
        EmptyCoroutineContext -> collectLatest { value = it }
        else -> withContext(context) { collectLatest { value = it } }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun <T> StateFlow<T>.collectLatestAsState(
    context: CoroutineContext = EmptyCoroutineContext,
): State<T> = collectLatestAsState(value, context)

@Composable
fun <T> Flow<T>.collectLatestAsStateWithLifecycle(
    initialValue: T,
    lifecycle: Lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
): State<T> = produceState(initialValue, this, lifecycle, minActiveState, context) {
    lifecycle.repeatOnLifecycle(minActiveState) {
        when (context) {
            EmptyCoroutineContext -> collectLatest { value = it }
            else -> withContext(context) { collectLatest { value = it } }
        }
    }
}

@Suppress("StateFlowValueCalledInComposition")
@Composable
fun <T> StateFlow<T>.collectLatestAsStateWithLifecycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
): State<T> = collectLatestAsStateWithLifecycle(
    initialValue = value,
    lifecycle = lifecycleOwner.lifecycle,
    minActiveState = minActiveState,
    context = context,
)
