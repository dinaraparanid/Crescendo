package com.paranid5.mediastreamer.presentation.tracks

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.presentation.Screens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@Composable
fun TracksScreen(tracksViewModel: TracksViewModel, curScreenState: MutableStateFlow<Screens>) {
    curScreenState.update { Screens.Tracks }
    Text(text = stringResource(R.string.tracks))
}