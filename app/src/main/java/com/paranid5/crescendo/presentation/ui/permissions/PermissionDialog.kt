package com.paranid5.crescendo.presentation.ui.permissions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.presentation.ui.permissions.description_providers.PermissionDescriptionProvider
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors

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
    val colors = LocalAppColors.current

    BasicAlertDialog(
        onDismissRequest = {
            onDismiss()
            isDialogShownState.value = false
        },
        modifier = modifier
            .background(colors.background)
            .wrapContentSize()
            .border(
                width = 30.dp,
                color = Color.Transparent,
                shape = RoundedCornerShape(30.dp)
            )
    ) {
        Column(Modifier.fillMaxWidth()) {
            Title(Modifier.align(Alignment.CenterHorizontally))
            Spacer(Modifier.height(10.dp))
            Description(descriptionProvider = permissionDescriptionProvider)
            Spacer(Modifier.height(10.dp))
            GrantPermissionButton(
                isPermanentlyDeclined = isPermanentlyDeclined,
                onGranted = onGrantedClicked,
                onGoToAppSettingsClicked = onGoToAppSettingsClicked,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun Title(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    Text(
        text = stringResource(R.string.permission_required),
        modifier = modifier.padding(vertical = 15.dp),
        color = colors.primary,
        maxLines = 1,
        fontSize = 18.sp
    )
}

@Composable
private fun Description(
    descriptionProvider: PermissionDescriptionProvider,
    modifier: Modifier = Modifier
) = Text(
    text = descriptionProvider.description,
    modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp)
)

@Composable
private inline fun GrantPermissionButton(
    isPermanentlyDeclined: Boolean,
    crossinline onGranted: () -> Unit,
    crossinline onGoToAppSettingsClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAppColors.current

    Button(
        modifier = modifier.padding(vertical = 10.dp),
        onClick = { if (isPermanentlyDeclined) onGoToAppSettingsClicked() else onGranted() },
        colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
    ) {
        Text(
            stringResource(
                id = when {
                    isPermanentlyDeclined -> R.string.grant_permission
                    else -> R.string.ok
                }
            )
        )
    }
}