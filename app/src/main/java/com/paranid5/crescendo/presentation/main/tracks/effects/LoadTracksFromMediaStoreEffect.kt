package com.paranid5.crescendo.presentation.main.tracks.effects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.presentation.main.tracks.TracksViewModel
import com.paranid5.crescendo.presentation.main.tracks.allTracksFromMediaStore
import com.paranid5.crescendo.presentation.main.tracks.properties.setTracks

@Composable
fun LoadTracksFromMediaStoreEffect(viewModel: TracksViewModel = koinActivityViewModel()) {
    val context = LocalContext.current

    LaunchedEffect(context) {
        viewModel.setTracks(allTracksFromMediaStore(context))
    }
}