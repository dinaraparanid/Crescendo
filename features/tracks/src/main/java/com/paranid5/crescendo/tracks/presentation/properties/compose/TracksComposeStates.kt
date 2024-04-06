package com.paranid5.crescendo.tracks.presentation.properties.compose

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.core.common.tracks.TrackOrder
import com.paranid5.crescendo.tracks.TracksViewModel
import com.paranid5.crescendo.tracks.presentation.properties.shownTracksFlow
import com.paranid5.crescendo.tracks.presentation.properties.shownTracksNumberFlow
import com.paranid5.crescendo.utils.extensions.collectLatestAsState
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun TracksViewModel.collectTracksAsState() =
    tracksState.collectLatestAsState()

@Composable
internal fun TracksViewModel.collectTrackOrderAsState() =
    trackOrderFlow.collectLatestAsState(initial = TrackOrder.default)

@Composable
internal fun TracksViewModel.collectShownTracksAsState() =
    shownTracksFlow.collectLatestAsState(initial = persistentListOf())

@Composable
internal fun TracksViewModel.collectShownTracksNumberAsState() =
    shownTracksNumberFlow.collectLatestAsState(initial = 0)
