package com.paranid5.crescendo.presentation.main.audio_effects

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.R
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.presentation.ui.extensions.decreaseBrightness
import com.paranid5.crescendo.presentation.ui.extensions.simpleShadow
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import org.koin.compose.koinInject

@Composable
fun UpBar(
    modifier: Modifier = Modifier,
    audioEffectsUIHandler: AudioEffectsUIHandler = koinInject(),
    storageHandler: StorageHandler = koinInject(),
) {
    val context = LocalContext.current
    val primaryColor = LocalAppColors.current.colorScheme.primary
    val argbPrimaryColor = primaryColor.toArgb()
    val areAudioEffectsEnabled by storageHandler.areAudioEffectsEnabledState.collectAsState()

    Box(modifier) {
        Text(
            text = stringResource(R.string.audio_effects),
            color = primaryColor,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.Center).simpleShadow(color = primaryColor)
        )

        Switch(
            modifier = Modifier.align(Alignment.CenterEnd),
            checked = areAudioEffectsEnabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = primaryColor,
                checkedTrackColor = Color(argbPrimaryColor.decreaseBrightness(0.5F)),
                checkedBorderColor = Color(argbPrimaryColor.decreaseBrightness(0.25F))
            ),
            onCheckedChange = {
                audioEffectsUIHandler.storeAudioEffectsEnabledAsync(context, isEnabled = it)
            }
        )
    }
}