package com.paranid5.crescendo.tracks.presentation.views.bar

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.tracks.presentation.TracksViewModel
import com.paranid5.crescendo.tracks.presentation.properties.compose.collectShownTracksNumberAsState
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun TracksNumberLabel(
    modifier: Modifier = Modifier,
    viewModel: TracksViewModel = koinViewModel(),
) {
    val shownTracksNumber by viewModel.collectShownTracksNumberAsState()

    Text(
        text = "${stringResource(R.string.tracks)}: $shownTracksNumber",
        color = colors.primary,
        textAlign = TextAlign.Start,
        modifier = modifier
    )
}