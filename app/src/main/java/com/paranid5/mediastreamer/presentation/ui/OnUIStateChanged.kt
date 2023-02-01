package com.paranid5.mediastreamer.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.paranid5.mediastreamer.presentation.StateChangedCallback
import com.paranid5.mediastreamer.presentation.UIHandler
import kotlinx.coroutines.launch

@Composable
fun <H : UIHandler> OnUIStateChanged(
    lifecycleOwner: LifecycleOwner,
    vararg callbacks: StateChangedCallback<H>
) = DisposableEffect(lifecycleOwner) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            callbacks.forEach { it() }
        }
    }

    onDispose {
        callbacks.forEach { (uiHandler, _, onDispose, _) ->
            onDispose?.invoke(uiHandler)
        }
    }
}