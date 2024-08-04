package com.paranid5.crescendo.ui.utils

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.paranid5.crescendo.core.common.navigation.Navigator
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun OnBackPressed(
    navigator: Navigator<*>,
    onBackPressedCallback: suspend (isScreenStackEmpty: Boolean) -> Unit = {},
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val callback = rememberBackPressedCallback(
        navigator = navigator,
        onBackPressedCallback = onBackPressedCallback,
    )

    val backDispatcher =
        LocalOnBackPressedDispatcherOwner.current!!.onBackPressedDispatcher

    DisposableEffect(lifecycleOwner, backDispatcher) {
        backDispatcher.addCallback(lifecycleOwner, callback)
        onDispose { callback.remove() }
    }
}

@Composable
private fun rememberBackPressedCallback(
    navigator: Navigator<*>,
    onBackPressedCallback: suspend (isScreenStackEmpty: Boolean) -> Unit = {}
): OnBackPressedCallback {
    val coroutineScope = rememberCoroutineScope()
    var task by remember { mutableStateOf<Job?>(null) }

    return remember(onBackPressedCallback, navigator) {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                task?.cancel()
                task = coroutineScope.launch {
                    onBackPressedCallback(
                        navigator.onBackPressed() == null
                    )
                }
            }
        }
    }
}
