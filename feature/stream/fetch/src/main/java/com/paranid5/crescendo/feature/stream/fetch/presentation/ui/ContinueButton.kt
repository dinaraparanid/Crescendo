package com.paranid5.crescendo.feature.stream.fetch.presentation.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.feature.stream.fetch.view_model.FetchStreamState
import com.paranid5.crescendo.ui.foundation.AppRippleButton

@Composable
internal fun ContinueButton(
    state: FetchStreamState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val appTextColors = colors.text

    val isEnabled = remember(state.isContinueButtonEnabled) {
        state.isContinueButtonEnabled
    }

    val textColor = remember(isEnabled, appTextColors) {
        if (isEnabled) appTextColors.onButton else appTextColors.tertiriary
    }

    AppRippleButton(
        modifier = modifier,
        onClick = onClick,
        isEnabled = isEnabled,
    ) {
        Text(
            text = stringResource(R.string.continue_),
            color = textColor,
            style = typography.regular,
        )
    }
}
