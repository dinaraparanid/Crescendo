package com.paranid5.crescendo.presentation.main.trimmer.effects.playback

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.properties.endPosInMillisState
import com.paranid5.crescendo.presentation.main.trimmer.properties.isPlayerInitializedState
import com.paranid5.crescendo.presentation.main.trimmer.properties.playbackPosInMillisState
import com.paranid5.crescendo.presentation.main.trimmer.properties.startPosInMillisState

@Composable
fun OutOfBordersEffect(viewModel: TrimmerViewModel) {
    val isPlayerInitialized by viewModel.isPlayerInitializedState.collectAsState()
    val startPos by viewModel.startPosInMillisState.collectAsState()
    val endPos by viewModel.endPosInMillisState.collectAsState()
    val currentPos by viewModel.playbackPosInMillisState.collectAsState()

    LaunchedEffect(currentPos, startPos, endPos) {
        if (isPlayerInitialized && currentPos !in startPos..endPos)
            viewModel.pausePlayback()
    }
}