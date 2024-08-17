package com.paranid5.crescendo.cache.presentation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.cache.presentation.effect.UpdateUrlEffect
import com.paranid5.crescendo.cache.presentation.ui.CacheDialogContent
import com.paranid5.crescendo.cache.view_model.CacheViewModel
import com.paranid5.crescendo.cache.view_model.CacheViewModelImpl
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.utils.extensions.collectLatestAsState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@NonRestartableComposable
@Composable
fun CacheDialog(
    url: String,
    modifier: Modifier = Modifier,
    viewModel: CacheViewModel = koinViewModel<CacheViewModelImpl>(),
    hide: () -> Unit,
) {
    val state by viewModel.stateFlow.collectLatestAsState()
    val onUiIntent = viewModel::onUiIntent

    UpdateUrlEffect(url = url, onUiIntent = onUiIntent)

    BasicAlertDialog(
        onDismissRequest = hide,
        modifier = modifier
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(dimensions.corners.extraMedium),
            colors = CardDefaults.cardColors(containerColor = colors.background.primary),
        ) {
            CacheDialogContent(
                state = state,
                onUiIntent = onUiIntent,
                modifier = Modifier.fillMaxWidth(),
                hideDialog = hide,
            )
        }
    }
}
