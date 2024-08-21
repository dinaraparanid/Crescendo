package com.paranid5.crescendo.feature.stream.fetch.presentation.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.feature.stream.fetch.view_model.FetchStreamState
import com.paranid5.crescendo.ui.foundation.AppRippleButton

@Composable
internal fun ContinueButton(
    state: FetchStreamState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) = AppRippleButton(
    modifier = modifier,
    onClick = onClick,
    isEnabled = state.isContinueButtonEnabled,
    contentPadding = PaddingValues(vertical = AppTheme.dimensions.padding.medium),
) {
    Text(
        text = stringResource(R.string.continue_),
        style = typography.regular,
    )
}
