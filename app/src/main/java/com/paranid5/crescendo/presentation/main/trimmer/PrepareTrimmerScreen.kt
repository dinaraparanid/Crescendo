package com.paranid5.crescendo.presentation.main.trimmer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import arrow.core.curried
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.data.media_store.tracks.getTrackFromMediaStore
import com.paranid5.crescendo.navigation.Screens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PrepareTrimmerScreen(
    backStackEntry: NavBackStackEntry,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var track by remember { mutableStateOf<Track?>(null) }

    val trackPath by remember {
        derivedStateOf {
            backStackEntry
                .arguments
                ?.getString(Screens.Audio.Trimmer.TRACK_PATH_KEY)
        }
    }

    LaunchedEffect(trackPath) {
        track = withContext(Dispatchers.IO) {
            trackPath?.let(::getTrackFromMediaStore.curried()(context))
        }
    }

    track?.let {
        TrimmerScreen(
            track = it,
            modifier = modifier
        )
    }
}