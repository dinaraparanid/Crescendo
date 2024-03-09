package com.paranid5.crescendo.presentation.main.tracks.views.properties

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.navigation.LocalNavController
import com.paranid5.crescendo.core.impl.presentation.composition_locals.playing.LocalPlayingSheetState
import com.paranid5.crescendo.navigation.Screens
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.properties.setTrackAndResetPositions
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TrimTrackProperty(
    track: com.paranid5.crescendo.core.common.tracks.Track,
    modifier: Modifier = Modifier,
    trimmerViewModel: TrimmerViewModel = koinActivityViewModel()
) {
    val navController = com.paranid5.crescendo.navigation.LocalNavController.current
    val playingSheetState = LocalPlayingSheetState.current
    val coroutineScope = rememberCoroutineScope()

    DropdownMenuItem(
        modifier = modifier,
        text = { Text(stringResource(R.string.trim_track)) },
        onClick = {
            trimmerViewModel.setTrackAndResetPositions(track)
            navController.navigateIfNotSame(com.paranid5.crescendo.navigation.Screens.Audio.Trimmer)
            coroutineScope.launch { playingSheetState?.bottomSheetState?.collapse() }
        }
    )
}