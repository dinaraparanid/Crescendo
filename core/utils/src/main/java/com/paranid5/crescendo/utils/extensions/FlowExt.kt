package com.paranid5.crescendo.utils.extensions

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
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
