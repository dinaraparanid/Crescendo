package com.paranid5.crescendo.feature.stream.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.feature.stream.presentation.ui.StreamPager
import com.paranid5.crescendo.feature.stream.view_model.StreamViewModel
import com.paranid5.crescendo.feature.stream.view_model.StreamViewModelImpl
import com.paranid5.crescendo.utils.extensions.collectLatestAsState
import org.koin.androidx.compose.koinViewModel

@Composable
fun StreamScreen(
    modifier: Modifier = Modifier,
    viewModel: StreamViewModel = koinViewModel<StreamViewModelImpl>(),
) {
    val state by viewModel.stateFlow.collectLatestAsState()
    val onUiIntent = viewModel::onUiIntent

    Column(modifier.verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(dimensions.padding.big))

        StreamPager(
            state = state,
            onUiIntent = onUiIntent,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
