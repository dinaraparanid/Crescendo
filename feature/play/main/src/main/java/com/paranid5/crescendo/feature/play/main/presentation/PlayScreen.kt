package com.paranid5.crescendo.feature.play.main.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.feature.play.main.presentation.ui.PlaySearchBar
import com.paranid5.crescendo.feature.play.main.presentation.view_model.PlayViewModel
import com.paranid5.crescendo.feature.play.main.presentation.view_model.PlayViewModelImpl
import com.paranid5.crescendo.utils.extensions.collectLatestAsState
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlayScreen(
    modifier: Modifier = Modifier,
    viewModel: PlayViewModel = koinViewModel<PlayViewModelImpl>()
) {
    val state by viewModel.stateFlow.collectLatestAsState()
    val onUiIntent = viewModel::onUiIntent

    Column(modifier) {
        PlaySearchBar(
            state = state,
            onUiIntent = onUiIntent,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensions.padding.extraMedium),
        )

        Spacer(Modifier.height(dimensions.padding.extraBig))
    }
}

