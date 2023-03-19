package com.paranid5.mediastreamer.presentation.ui

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.paranid5.mediastreamer.presentation.LocalNavController

@Composable
fun OnBackPressedHandler(onBackPressedCallback: (isStackEmpty: Boolean) -> Unit = {}) {
    val navHostController = LocalNavController.current

    val callback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() =
                onBackPressedCallback(navHostController.onBackPressed() == null)
        }
    }

    val backDispatcher =
        LocalOnBackPressedDispatcherOwner.current!!.onBackPressedDispatcher

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, backDispatcher) {
        backDispatcher.addCallback(lifecycleOwner, callback)
        onDispose { callback.remove() }
    }
}