package com.paranid5.mediastreamer.presentation.audio_effects

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paranid5.mediastreamer.EQUALIZER_DATA
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.domain.StorageHandler
import com.paranid5.mediastreamer.domain.stream_service.EqualizerData
import com.paranid5.mediastreamer.domain.stream_service.StreamService
import com.paranid5.mediastreamer.presentation.ui.theme.LocalAppColors
import com.paranid5.mediastreamer.presentation.ui.utils.Spinner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@Composable
internal fun Equalizer(
    modifier: Modifier = Modifier,
    equalizerDataState: MutableStateFlow<EqualizerData?> = koinInject(named(EQUALIZER_DATA)),
) {
    val equalizerData by equalizerDataState.collectAsState()

    Log.d("EqualizerView", "EQ Data: $equalizerData")

    equalizerData?.let { eqData ->
        Column(modifier) {
            PresetSpinner(equalizerData = eqData, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun PresetSpinner(
    equalizerData: EqualizerData,
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = koinInject()
) {
    val primaryColor = LocalAppColors.current.value.primary
    val coroutineScope = rememberCoroutineScope()
    val customPresetIndex = equalizerData.presets.size - 1

    val selectedItemInd = when (val preset = equalizerData.curPreset.toInt()) {
        StreamService.CUSTOM_EQ_PRESET -> customPresetIndex
        else -> preset
    }

    Box(modifier) {
        Spinner(
            items = equalizerData.presets.toList(),
            selectedItemInd = selectedItemInd,
            modifier = Modifier.fillMaxWidth().align(Alignment.CenterStart),
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

        Image(
            painter = painterResource(R.drawable.arrow_down),
            contentDescription = stringResource(R.string.eq_presets),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(primaryColor),
            modifier = Modifier.align(Alignment.CenterEnd).padding(2.dp)
        )
    }
}