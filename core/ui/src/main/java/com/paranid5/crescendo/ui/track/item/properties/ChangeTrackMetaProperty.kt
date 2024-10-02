package com.paranid5.crescendo.ui.track.item.properties

import android.net.Uri
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.ui.composition_locals.playing.LocalPlayingSheetState
import kotlinx.coroutines.launch

@Composable
internal fun ChangeTrackMetaProperty(
    trackPath: String,
    modifier: Modifier = Modifier,
    showMetaEditor: (trackUri: String) -> Unit,
) {
    val playingSheetState = LocalPlayingSheetState.current
    val coroutineScope = rememberCoroutineScope()

    DropdownMenuItem(
        modifier = modifier,
        leadingIcon = { PropertyIcon(ImageVector.vectorResource(R.drawable.ic_edit)) },
        text = { PropertyText(stringResource(R.string.track_kebab_change_meta)) },
        onClick = {
            showMetaEditor(Uri.encode(trackPath))
            coroutineScope.launch { playingSheetState?.bottomSheetState?.collapse() }
        },
    )
}
