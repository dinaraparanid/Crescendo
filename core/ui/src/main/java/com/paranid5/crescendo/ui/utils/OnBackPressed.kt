package com.paranid5.crescendo.ui.utils

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.paranid5.crescendo.navigation.LocalNavController
import kotlinx.coroutines.launch

@Composable
fun OnBackPressed(onBackPressedCallback: suspend (isScreenStackEmpty: Boolean) -> Unit = {}) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val callback = rememberCallback(onBackPressedCallback)

    val backDispatcher =
        LocalOnBackPressedDispatcherOwner.current!!.onBackPressedDispatcher

    DisposableEffect(lifecycleOwner, backDispatcher) {
        backDispatcher.addCallback(lifecycleOwner, callback)
        onDispose { callback.remove() }
    }
}

@Composable
private fun rememberCallback(
    onBackPressedCallback: suspend (isScreenStackEmpty: Boolean) -> Unit = {}
): OnBackPressedCallback {
    val navHostController = LocalNavController.current
    val coroutineScope = rememberCoroutineScope()

    return remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                coroutineScope.launch {
                    onBackPressedCallback(
                        navHostController.onBackPressed() == null
                    )
                }
            }
        }
    }
}