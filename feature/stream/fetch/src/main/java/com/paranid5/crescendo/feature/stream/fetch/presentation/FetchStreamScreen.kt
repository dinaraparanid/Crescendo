package com.paranid5.crescendo.feature.stream.fetch.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.feature.stream.fetch.presentation.ui.ButtonsContainer
import com.paranid5.crescendo.feature.stream.fetch.presentation.ui.FetchCard
import com.paranid5.crescendo.feature.stream.fetch.presentation.ui.HowToUseCard
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

    Column(modifier) {
        FetchCard(
            state = state,
            onUiIntent = onUiIntent,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(dimensions.padding.extraMedium))

        ButtonsContainer(
            state = state,
            onUiIntent = onUiIntent,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(dimensions.padding.large))

        HowToUseCard(modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(dimensions.padding.extraMedium))
    }
}
