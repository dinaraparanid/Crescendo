package com.paranid5.crescendo.feature.stream.fetch.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.feature.stream.fetch.view_model.FetchStreamViewModel
import com.paranid5.crescendo.feature.stream.fetch.view_model.FetchStreamViewModelImpl
import com.paranid5.crescendo.utils.extensions.collectLatestAsState
import org.koin.androidx.compose.koinViewModel

@Composable
fun FetchStreamScreen(
    modifier: Modifier = Modifier,
    viewModel: FetchStreamViewModel = koinViewModel<FetchStreamViewModelImpl>(),
) {
    val state by viewModel.stateFlow.collectLatestAsState()
    val onUiIntent = viewModel::onUiIntent

    Box(modifier) {
        Text("TODO: Fetch Screen", Modifier.align(Alignment.Center))
    }
}
