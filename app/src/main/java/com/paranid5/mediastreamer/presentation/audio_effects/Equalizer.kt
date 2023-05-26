package com.paranid5.mediastreamer.presentation.audio_effects

import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paranid5.mediastreamer.EQUALIZER_DATA
import com.paranid5.mediastreamer.domain.StorageHandler
import com.paranid5.mediastreamer.domain.stream_service.EqualizerData
import com.paranid5.mediastreamer.domain.stream_service.StreamService
import com.paranid5.mediastreamer.presentation.ui.utils.Spinner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@Composable
internal fun Equalizer(
    modifier: Modifier = Modifier,
    equalizerDataState: MutableStateFlow<EqualizerData?> = koinInject(named(EQUALIZER_DATA)),
    storageHandler: StorageHandler = koinInject()
) {
    val coroutineScope = rememberCoroutineScope()
    val equalizerData by equalizerDataState.collectAsState()

    Log.d("EqualizerView", "EQ Data: $equalizerData")

    if (equalizerData == null)
        return

    val customPresetIndex = equalizerData!!.presets.size - 1

    val selectedItemInd = when (val preset = equalizerData!!.curPreset.toInt()) {
        StreamService.CUSTOM_EQ_PRESET -> customPresetIndex
        else -> preset
    }

    Row(modifier) {
        Spinner(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
            items = equalizerData!!.presets.toList(),
            selectedItemInd = selectedItemInd,
            onItemSelected = { ind, _ ->
                coroutineScope.launch {
                    storageHandler.storeEqualizerPreset(
                        eqPreset = when (ind) {
                            customPresetIndex -> StreamService.CUSTOM_EQ_PRESET
                            else -> ind
                        }
                    )
                }
            }
        )
    }
}