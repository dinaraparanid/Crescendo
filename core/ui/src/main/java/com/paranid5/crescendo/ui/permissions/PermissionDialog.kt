package com.paranid5.crescendo.ui.permissions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
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
) {
    BasicAlertDialog(
        onDismissRequest = {
            onDismiss()
            isDialogShownState.value = false
        },
        modifier = modifier
            .background(colors.background.primary)
            .wrapContentSize()
            .border(
                width = dimensions.corners.big,
                color = Color.Transparent,
                shape = RoundedCornerShape(dimensions.corners.big)
            )
    ) {
        Column(Modifier.fillMaxWidth()) {
            Title(Modifier.align(Alignment.CenterHorizontally))
            Spacer(Modifier.height(dimensions.corners.medium))
            Description(descriptionProvider = permissionDescriptionProvider)
            Spacer(Modifier.height(dimensions.padding.medium))
            GrantPermissionButton(
                isPermanentlyDeclined = isPermanentlyDeclined,
                onGranted = onGrantedClicked,
                onGoToAppSettingsClicked = onGoToAppSettingsClicked,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}

@Composable
private fun Title(modifier: Modifier = Modifier) =
    Text(
        text = stringResource(R.string.permission_required),
        modifier = modifier.padding(vertical = dimensions.padding.extraMedium),
        color = colors.primary,
        style = typography.h.h3,
        maxLines = 1,
    )

@Composable
private fun Description(
    descriptionProvider: PermissionDescriptionProvider,
    modifier: Modifier = Modifier
) = Text(
    text = descriptionProvider.description,
    modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = dimensions.padding.medium)
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

    Button(
        modifier = modifier.padding(vertical = dimensions.padding.medium),
        onClick = { if (isPermanentlyDeclined) onGoToAppSettingsClicked() else onGranted() },
        colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
    ) {
        Text(
            text = stringResource(textRes),
            color = colors.text.primary,
            style = typography.regular,
        )
    }
}