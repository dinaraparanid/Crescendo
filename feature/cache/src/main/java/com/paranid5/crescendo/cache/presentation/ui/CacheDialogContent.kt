package com.paranid5.crescendo.cache.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.cache.view_model.CacheState
import com.paranid5.crescendo.cache.view_model.CacheUiIntent
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions

@Composable
internal fun CacheDialogContent(
    state: CacheState,
    onUiIntent: (CacheUiIntent) -> Unit,
    modifier: Modifier = Modifier,
    hideDialog: () -> Unit,
) = Column(modifier) {
    Spacer(Modifier.height(dimensions.padding.extraMedium))

    CacheDialogLabel(Modifier.align(Alignment.CenterHorizontally))

    Spacer(Modifier.height(dimensions.padding.extraBig))

    FilenameInput(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.padding.medium),
    )

    Spacer(Modifier.height(dimensions.padding.medium))

    SaveOptionsMenu(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.padding.medium),
    )

    Spacer(Modifier.height(dimensions.padding.extraBig))

    ConfirmButton(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier.align(Alignment.CenterHorizontally),
        hideDialog = hideDialog,
    )

    Spacer(Modifier.height(dimensions.padding.medium))
}
