package com.paranid5.crescendo.cache.presentation.ui

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.paranid5.crescendo.cache.view_model.CacheState
import com.paranid5.crescendo.cache.view_model.CacheUiIntent
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.ui.foundation.AppRippleButton

@Composable
internal fun ConfirmButton(
    state: CacheState,
    onUiIntent: (CacheUiIntent) -> Unit,
    modifier: Modifier = Modifier,
    hideDialog: () -> Unit,
) = AppRippleButton(
    modifier = modifier,
    isEnabled = state.isCacheButtonClickable,
    colors = ButtonDefaults.buttonColors(
        containerColor = colors.button.onBackgroundPrimary,
        contentColor = colors.text.onBackgroundPrimary,
        disabledContainerColor = colors.button.onBackgroundPrimaryDisabled,
        disabledContentColor = colors.text.tertiriary,
    ),
    content = { ConfirmButtonLabel() },
    onClick = {
        onUiIntent(CacheUiIntent.StartCaching)
        hideDialog()
    },
)

@Composable
private fun ConfirmButtonLabel(modifier: Modifier = Modifier) =
    Text(
        text = stringResource(R.string.cache_dialog_accept_button_title),
        style = typography.regular,
        fontWeight = FontWeight.Bold,
        modifier = modifier,
    )
