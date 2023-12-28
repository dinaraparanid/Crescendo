package com.paranid5.crescendo.presentation.main.audio_effects.view

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.R
import com.paranid5.crescendo.presentation.main.audio_effects.AudioEffectsUIHandler
import com.paranid5.crescendo.presentation.main.audio_effects.AudioEffectsViewModel
import com.paranid5.crescendo.presentation.main.audio_effects.properties.compose.collectAreAudioEffectsEnabledAsState
import com.paranid5.crescendo.presentation.main.audio_effects.properties.compose.collectAudioStatusAsState
import com.paranid5.crescendo.presentation.ui.extensions.decreaseBrightness
import com.paranid5.crescendo.presentation.ui.extensions.simpleShadow
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun UpBar(
    viewModel: AudioEffectsViewModel,
    modifier: Modifier = Modifier,
) = Box(modifier) {
    AudioEffectsLabel(Modifier.align(Alignment.Center))

    AudioEffectsSwitch(
        viewModel = viewModel,
        modifier = Modifier.align(Alignment.CenterEnd),
    )
}

@Composable
private fun AudioEffectsLabel(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    Text(
        text = stringResource(R.string.audio_effects),
        color = colors.primary,
        fontSize = 20.sp,
        modifier = modifier.simpleShadow(color = colors.primary)
    )
}

@Composable
private fun AudioEffectsSwitch(
    viewModel: AudioEffectsViewModel,
    modifier: Modifier = Modifier,
    audioEffectsUIHandler: AudioEffectsUIHandler = koinInject(),
) {
    val context = LocalContext.current
    val colors = LocalAppColors.current
    val coroutineScope = rememberCoroutineScope()

    val primaryColor = colors.primary

    val argbPrimaryColor by remember(primaryColor) {
        derivedStateOf { primaryColor.toArgb() }
    }

    val areAudioEffectsEnabled by viewModel.collectAreAudioEffectsEnabledAsState()
    val audioStatus by viewModel.collectAudioStatusAsState()

    Switch(
        modifier = modifier,
        checked = areAudioEffectsEnabled,
        colors = SwitchDefaults.colors(
            checkedThumbColor = primaryColor,
            checkedTrackColor = Color(argbPrimaryColor.decreaseBrightness(0.5F)),
            checkedBorderColor = Color(argbPrimaryColor.decreaseBrightness(0.25F))
        ),
        onCheckedChange = {
            coroutineScope.launch {
                audioEffectsUIHandler.storeAudioEffectsEnabled(
                    context = context,
                    isEnabled = it,
                    audioStatus = audioStatus!!
                )
            }
        }
    )
}