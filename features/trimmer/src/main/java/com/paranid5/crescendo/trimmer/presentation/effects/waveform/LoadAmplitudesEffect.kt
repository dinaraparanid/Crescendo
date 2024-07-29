package com.paranid5.crescendo.trimmer.presentation.effects.waveform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectTrackPathAsState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import linc.com.amplituda.Amplituda
import linc.com.amplituda.callback.AmplitudaErrorListener
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun LoadAmplitudesEffect(
    viewModel: TrimmerViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val trackPath by viewModel.collectTrackPathAsState()

    val amplituda by remember(context) {
        derivedStateOf { Amplituda(context) }
    }

    LaunchedEffect(trackPath) {
        withContext(Dispatchers.IO) {
            if (trackPath != null) viewModel.updateAmplitudes(
                amplituda
                    .processAudio(trackPath)
                    .get(AmplitudaErrorListener { it.printStackTrace() })
                    .amplitudesAsList()
                    .toImmutableList()
            )
        }
    }
}