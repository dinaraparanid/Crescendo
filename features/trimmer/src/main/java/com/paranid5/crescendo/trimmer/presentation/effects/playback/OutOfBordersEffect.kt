package com.paranid5.crescendo.trimmer.presentation.effects.playback

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectEndPosInMillisAsState
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectIsPlayerInitializedAsState
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectPlaybackPosInMillisAsState
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectStartPosInMillisAsState
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun OutOfBordersEffect(viewModel: TrimmerViewModel = koinViewModel()) {
    val isPlayerInitialized by viewModel.collectIsPlayerInitializedAsState()
    val startPos by viewModel.collectStartPosInMillisAsState()
    val endPos by viewModel.collectEndPosInMillisAsState()
    val currentPos by viewModel.collectPlaybackPosInMillisAsState()

    LaunchedEffect(currentPos, startPos, endPos) {
        if (isPlayerInitialized && currentPos !in startPos..endPos)
            viewModel.pausePlayback()
    }
}