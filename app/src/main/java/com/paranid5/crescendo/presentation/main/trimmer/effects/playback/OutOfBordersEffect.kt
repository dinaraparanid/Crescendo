package com.paranid5.crescendo.presentation.main.trimmer.effects.playback

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.properties.compose.collectEndPosInMillisAsState
import com.paranid5.crescendo.presentation.main.trimmer.properties.compose.collectIsPlayerInitializedAsState
import com.paranid5.crescendo.presentation.main.trimmer.properties.compose.collectPlaybackPosInMillisAsState
import com.paranid5.crescendo.presentation.main.trimmer.properties.compose.collectStartPosInMillisAsState
import org.koin.androidx.compose.koinViewModel

@Composable
fun OutOfBordersEffect(viewModel: TrimmerViewModel = koinViewModel()) {
    val isPlayerInitialized by viewModel.collectIsPlayerInitializedAsState()
    val startPos by viewModel.collectStartPosInMillisAsState()
    val endPos by viewModel.collectEndPosInMillisAsState()
    val currentPos by viewModel.collectPlaybackPosInMillisAsState()

    LaunchedEffect(currentPos, startPos, endPos) {
        if (isPlayerInitialized && currentPos !in startPos..endPos)
            viewModel.pausePlayback()
    }
}