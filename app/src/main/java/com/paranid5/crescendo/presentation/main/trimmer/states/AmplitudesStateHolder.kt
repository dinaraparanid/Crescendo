package com.paranid5.crescendo.presentation.main.trimmer.states

import com.paranid5.crescendo.data.StorageHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AmplitudesStateHolder(
    private val storageHandler: StorageHandler,
    scope: CoroutineScope
) : CoroutineScope by scope {
    val amplitudesState by lazy { storageHandler.amplitudesState }

    fun setAmplitudesAsync(amplitudes: List<Int>) = launch(Dispatchers.IO) {
        storageHandler.storeAmplitudes(amplitudes)
    }
}