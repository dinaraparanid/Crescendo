package com.paranid5.crescendo.presentation.main.current_playlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.domain.tracks.DefaultTrack
import com.paranid5.crescendo.presentation.main.current_playlist.effects.TrackDismissEffect
import com.paranid5.crescendo.presentation.main.current_playlist.views.CurrentPlaylistBar
import com.paranid5.crescendo.presentation.main.current_playlist.views.CurrentPlaylistTrackList
import com.paranid5.crescendo.presentation.ui.theme.TransparentUtility

@Composable
fun CurrentPlaylistScreen(
    viewModel: CurrentPlaylistViewModel,
    modifier: Modifier = Modifier,
) {
    val playlistDismissMediatorState = remember {
        mutableStateOf(emptyList<DefaultTrack>())
    }

    val playlistDismissMediator by playlistDismissMediatorState

    val trackIndexDismissMediatorState = remember {
        mutableIntStateOf(0)
    }

    val trackIndexDismissMediator by trackIndexDismissMediatorState

    val trackPathDismissKeyState = remember {
        mutableStateOf("")
    }

    val trackPathDismissKey by trackPathDismissKeyState

    TrackDismissEffect(
        viewModel = viewModel,
        trackPathDismissKey = trackPathDismissKey,
        playlistDismissMediator = playlistDismissMediator,
        trackIndexDismissMediator = trackIndexDismissMediator
    )

    CurrentPlaylistScreenContent(
        playlistDismissMediatorState = playlistDismissMediatorState,
        trackIndexDismissMediatorState = trackIndexDismissMediatorState,
        trackPathDismissKeyState = trackPathDismissKeyState,
        viewModel = viewModel,
        modifier = modifier
    )
}

@Composable
private fun CurrentPlaylistScreenContent(
    playlistDismissMediatorState: MutableState<List<DefaultTrack>>,
    trackIndexDismissMediatorState: MutableState<Int>,
    trackPathDismissKeyState: MutableState<String>,
    viewModel: CurrentPlaylistViewModel,
    modifier: Modifier = Modifier,
) = Column(modifier) {
    CurrentPlaylistBar(
        viewModel = viewModel,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
    )

    CurrentPlaylistBarSeparator(Modifier.height(2.dp))

    CurrentPlaylistTrackList(
        playlistDismissMediatorState = playlistDismissMediatorState,
        trackIndexDismissMediatorState = trackIndexDismissMediatorState,
        trackPathDismissKeyState = trackPathDismissKeyState,
        viewModel = viewModel,
        modifier = Modifier.padding(
            top = 16.dp,
            start = 8.dp,
            end = 4.dp,
            bottom = 8.dp
        ),
    )
}

@Composable
private fun CurrentPlaylistBarSeparator(modifier: Modifier = Modifier) =
    Spacer(
        modifier
            .fillMaxWidth()
            .background(TransparentUtility)
    )