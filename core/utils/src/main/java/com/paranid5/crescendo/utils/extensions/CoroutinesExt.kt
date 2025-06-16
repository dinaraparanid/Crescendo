package com.paranid5.crescendo.utils.extensions

import android.util.Log
import com.paranid5.core.common.BuildConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun CoroutineScope.launchInScope(
    dispatcher: CoroutineDispatcher = Dispatchers.Unconfined,
    exceptionHandler: CoroutineExceptionHandler = defaultExceptionHandler(),
    block: suspend CoroutineScope.() -> Unit,
): Job {
    val coroutineContext = dispatcher + exceptionHandler
    return launch(coroutineContext, block = block)
}

/**
 * Helps to handle all exceptions in coroutines,
 * but also satisfies structured concurrency rules for coroutines
 */

inline fun <R> runCatchingNonCancellation(
    finallyBlock: () -> Unit = {},
    block: () -> R,
): Result<R> = try {
    Result.success(block())
} catch (e: Throwable) {
    if (BuildConfig.DEBUG) e.printStackTrace()

    when (e) {
        is InterruptedException, is CancellationException -> throw e
        else -> Result.failure(e)
    }
} finally {
    finallyBlock()
}

private fun defaultExceptionHandler(): CoroutineExceptionHandler =
    CoroutineExceptionHandler { _, throwable ->
        Log.e("defaultExceptionHandler", "$throwable")
    }
