package com.paranid5.crescendo.presentation.main.trimmer.effects.waveform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.properties.setAmplitudes
import com.paranid5.crescendo.presentation.main.trimmer.properties.trackPathOrNullFlow
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import linc.com.amplituda.Amplituda
import linc.com.amplituda.callback.AmplitudaErrorListener

@Composable
fun LoadAmplitudesEffect(viewModel: TrimmerViewModel) {
    val context = LocalContext.current
    val trackPath by viewModel.trackPathOrNullFlow.collectAsState(initial = null)
    val amplituda by remember { derivedStateOf { Amplituda(context) } }

    LaunchedEffect(key1 = trackPath) {
        withContext(Dispatchers.IO) {
            if (trackPath != null) viewModel.setAmplitudes(
                amplituda
                    .processAudio(trackPath)
                    .get(AmplitudaErrorListener { it.printStackTrace() })
                    .amplitudesAsList()
                    .toImmutableList()
            )
        }
    }
}