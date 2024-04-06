package com.paranid5.crescendo.ui.track.item.properties

import android.net.Uri
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.navigation.LocalNavController
import com.paranid5.crescendo.navigation.Screens
import com.paranid5.crescendo.ui.composition_locals.playing.LocalPlayingSheetState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun TrimTrackProperty(
    trackPath: String,
    modifier: Modifier = Modifier,
) {
    val navController = LocalNavController.current
    val playingSheetState = LocalPlayingSheetState.current
    val coroutineScope = rememberCoroutineScope()

    DropdownMenuItem(
        modifier = modifier,
        text = { Text(stringResource(R.string.trim_track)) },
        onClick = {
            navController.navigateIfNotSame(Screens.Audio.Trimmer(Uri.encode(trackPath)))
            coroutineScope.launch { playingSheetState?.bottomSheetState?.collapse() }
        }
    )
}