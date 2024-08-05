package com.paranid5.crescendo.tracks.presentation.ui.bar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.tracks.view_model.TracksState
import com.paranid5.crescendo.ui.foundation.AppBarCardLabel

@Composable
internal fun TotalLabel(
    state: TracksState,
    modifier: Modifier = Modifier,
) = AppBarCardLabel(
    modifier = modifier,
    text = stringResource(
        R.string.list_top_bar_total,
        state.shownTracksNumber,
    )
)
