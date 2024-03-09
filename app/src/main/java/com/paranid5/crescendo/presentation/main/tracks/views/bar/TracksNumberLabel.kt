package com.paranid5.crescendo.presentation.main.tracks.views.bar

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.presentation.main.tracks.TracksViewModel
import com.paranid5.crescendo.presentation.main.tracks.properties.compose.collectShownTracksNumberAsState
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors

@Composable
fun TracksNumberLabel(
    modifier: Modifier = Modifier,
    viewModel: TracksViewModel = koinActivityViewModel(),
) {
    val colors = LocalAppColors.current
    val shownTracksNumber by viewModel.collectShownTracksNumberAsState()

    Text(
        text = "${stringResource(R.string.tracks)}: $shownTracksNumber",
        color = colors.primary,
        textAlign = TextAlign.Start,
        modifier = modifier
    )
}