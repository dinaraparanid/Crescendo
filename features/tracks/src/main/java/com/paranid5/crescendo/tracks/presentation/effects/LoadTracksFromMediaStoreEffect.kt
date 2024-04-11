package com.paranid5.crescendo.tracks.presentation.effects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.paranid5.crescendo.tracks.presentation.TracksViewModel
import com.paranid5.crescendo.data.media_store.tracks.allTracksFromMediaStore
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun LoadTracksFromMediaStoreEffect(
    refreshingState: MutableState<Boolean>,
    viewModel: TracksViewModel = koinViewModel()
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