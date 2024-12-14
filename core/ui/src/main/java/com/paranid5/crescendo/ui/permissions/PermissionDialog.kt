package com.paranid5.crescendo.ui.permissions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.ui.foundation.AppRippleButton
import com.paranid5.crescendo.ui.foundation.AppText
import com.paranid5.crescendo.ui.permissions.description_providers.PermissionDescriptionProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal inline fun PermissionDialog(
    permissionDescriptionProvider: PermissionDescriptionProvider,
    isDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    isPermanentlyDeclined: Boolean = false,
    crossinline onGrantedClicked: () -> Unit = {},
    crossinline onGoToAppSettingsClicked: () -> Unit = {},
    noinline onDismiss: () -> Unit = {},
) = BasicAlertDialog(
    modifier = modifier,
    onDismissRequest = {
        onDismiss()
        isDialogShownState.value = false
    },
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensions.corners.medium),
        colors = CardDefaults.cardColors(containerColor = colors.background.primary),
    ) {
        Column(Modifier.fillMaxWidth()) {
            Spacer(Modifier.height(dimensions.padding.extraMedium))

            Title(Modifier.align(Alignment.CenterHorizontally))

            Spacer(Modifier.height(dimensions.padding.extraBig))

            Description(
                descriptionProvider = permissionDescriptionProvider,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensions.padding.medium),
            )

            Spacer(Modifier.height(dimensions.padding.extraBig))

            GrantPermissionButton(
                isPermanentlyDeclined = isPermanentlyDeclined,
                onGranted = onGrantedClicked,
                onGoToAppSettingsClicked = onGoToAppSettingsClicked,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            Spacer(Modifier.height(dimensions.padding.extraMedium))
        }
    }
}

@Composable
private fun Title(modifier: Modifier = Modifier) =
    AppText(
        modifier = modifier,
        text = stringResource(R.string.permission_required),
        style = typography.h.h4.copy(
            color = colors.text.primary,
            fontWeight = FontWeight.W700,
        ),
        maxLines = 1,
    )

@Composable
private fun Description(
    descriptionProvider: PermissionDescriptionProvider,
    modifier: Modifier = Modifier,
) = AppText(
    text = descriptionProvider.description,
    style = typography.body.copy(
        color = colors.text.primary,
    ),
    modifier = modifier,
)

@Composable
private inline fun GrantPermissionButton(
    isPermanentlyDeclined: Boolean,
    crossinline onGranted: () -> Unit,
    crossinline onGoToAppSettingsClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val textRes = remember(isPermanentlyDeclined) {
        when {
            isPermanentlyDeclined -> R.string.grant_permission
            else -> R.string.ok
        }
    }

    AppRippleButton(
        modifier = modifier,
        onClick = { if (isPermanentlyDeclined) onGoToAppSettingsClicked() else onGranted() },
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.button.onBackgroundPrimary,
            contentColor = colors.text.onBackgroundPrimary,
            disabledContainerColor = colors.button.onBackgroundPrimaryDisabled,
            disabledContentColor = colors.text.tertiriary,
        ),
    ) {
        AppText(
            text = stringResource(textRes),
            style = typography.regular.copy(
                fontWeight = FontWeight.W700,
            ),
        )
    }
}
