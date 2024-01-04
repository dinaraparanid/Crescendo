package com.paranid5.crescendo.presentation.main.tracks.effects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.presentation.main.tracks.TracksViewModel
import com.paranid5.crescendo.presentation.main.tracks.allTracksFromMediaStore
import kotlinx.coroutines.delay

@Composable
fun LoadTracksFromMediaStoreEffect(
    refreshingState: MutableState<Boolean>,
    viewModel: TracksViewModel = koinActivityViewModel()
) {
    val context = LocalContext.current
    var refreshing by refreshingState

    LaunchedEffect(context, refreshing) {
        if (refreshing) {
            viewModel.setTracks(allTracksFromMediaStore(context))
            delay(500) // animation
            refreshing = false
        }
    }
}