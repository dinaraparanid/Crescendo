package com.paranid5.mediastreamer.presentation.audio_effects

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.indicatorLine
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.presentation.ui.theme.LocalAppColors
import kotlinx.coroutines.flow.update
import org.koin.compose.koinInject

@Composable
internal fun PitchAndSpeed(
    viewModel: AudioEffectsViewModel,
    modifier: Modifier = Modifier,
) {
    val pitchInputText by viewModel
        .presenter
        .pitchTextState
        .collectAsState()

    val speedInputText by viewModel
        .presenter
        .speedTextState
        .collectAsState()

    Column(modifier) {
        PitchEditor(
            initialText = pitchInputText,
            viewModel = viewModel,
            modifier = Modifier.fillMaxWidth()
        )

        SpeedEditor(
            initialText = speedInputText,
            viewModel = viewModel,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PitchEditor(
    initialText: String?,
    viewModel: AudioEffectsViewModel,
    modifier: Modifier = Modifier,
) = AudioEffectEditor(
    initialText = initialText,
    effectTitleRes = R.string.pitch,
    onValueChanged = { newPitch ->
        storePitchAsync(newPitch)
        viewModel.presenter.pitchTextState.update { newPitch.toString().take(MAX_INPUT_LENGTH) }
    },
    modifier = modifier
)

@Composable
private fun SpeedEditor(
    initialText: String?,
    viewModel: AudioEffectsViewModel,
    modifier: Modifier = Modifier,
) = AudioEffectEditor(
    initialText = initialText,
    effectTitleRes = R.string.speed,
    onValueChanged = { newSpeed ->
        storeSpeedAsync(newSpeed)
        viewModel.presenter.speedTextState.update { newSpeed.toString().take(MAX_INPUT_LENGTH) }
    },
    modifier = modifier
)

private const val MAX_INPUT_LENGTH = 4

@Composable
private fun AudioEffectEditor(
    initialText: String?,
    @StringRes effectTitleRes: Int,
    onValueChanged: AudioEffectsUIHandler.(effectVal: Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val effectInputState = remember { mutableStateOf(initialText ?: "") }
    val effectValState = remember { mutableStateOf(effectInputState.value.toFloatOrNull() ?: 1F) }

    val textFieldText by remember {
        derivedStateOf { effectInputState.value.take(MAX_INPUT_LENGTH) }
    }

    Row(modifier) {
        Label(
            effectTitleRes = effectTitleRes,
            modifier = Modifier.align(Alignment.CenterVertically).width(80.dp)
        )

        AudioEffectSlider(
            effectTitleRes = effectTitleRes,
            effectValState = effectValState,
            effectInputState = effectInputState,
            onValueChanged = onValueChanged,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(top = 5.dp)
                .weight(1F)
        )

        AudioEffectTextField(
            value = textFieldText,
            effectInputState = effectInputState,
            effectValState = effectValState,
            onValueChanged = onValueChanged,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .width(50.dp)
                .padding(horizontal = 10.dp)
        )
    }
}

@Composable
private fun Label(
    @StringRes effectTitleRes: Int,
    modifier: Modifier = Modifier
) {
    val primaryColor = LocalAppColors.current.value.primary

    Text(
        text = stringResource(effectTitleRes),
        textAlign = TextAlign.Center,
        color = primaryColor,
        fontSize = 12.sp,
        maxLines = 1,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AudioEffectSlider(
    @StringRes effectTitleRes: Int,
    effectValState: MutableFloatState,
    effectInputState: MutableState<String>,
    onValueChanged: AudioEffectsUIHandler.(effectVal: Float) -> Unit,
    modifier: Modifier = Modifier,
    audioEffectsUIHandler: AudioEffectsUIHandler = koinInject(),
) {
    val primaryColor = LocalAppColors.current.value.primary
    var effectVal by effectValState
    var effectInput by effectInputState

    Box(modifier) {
        Slider(
            value = effectVal,
            valueRange = 0.25F..2F,
            colors = SliderDefaults.colors(activeTrackColor = primaryColor),
            modifier = Modifier.fillMaxWidth().align(Alignment.Center),
            onValueChange = { newEffectVal ->
                effectInput = newEffectVal.toString().take(MAX_INPUT_LENGTH)
                effectVal = newEffectVal
                onValueChanged(audioEffectsUIHandler, effectVal)
            },
            thumb = {
                Image(
                    painter = painterResource(R.drawable.audio_band_button),
                    contentDescription = stringResource(R.string.pitch),
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier.align(Alignment.Center).height(20.dp),
                )
            },
        )

        Image(
            modifier = Modifier.fillMaxWidth().align(Alignment.Center),
            painter = painterResource(R.drawable.audio_track_horizontal_night_transparent),
            contentDescription = stringResource(effectTitleRes),
            contentScale = ContentScale.FillWidth
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AudioEffectTextField(
    value: String,
    effectInputState: MutableState<String>,
    effectValState: MutableFloatState,
    onValueChanged: AudioEffectsUIHandler.(effectVal: Float) -> Unit,
    modifier: Modifier = Modifier,
    audioEffectsUIHandler: AudioEffectsUIHandler = koinInject()
) {
    val primaryColor = LocalAppColors.current.value.primary
    var effectInput by effectInputState
    var effectVal by effectValState
    val textFieldInteractionSource = remember { MutableInteractionSource() }

    val colors = TextFieldDefaults.colors(
        cursorColor = primaryColor,
        focusedTextColor = primaryColor,
        unfocusedTextColor = primaryColor,
        focusedIndicatorColor = primaryColor,
        unfocusedIndicatorColor = primaryColor,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
    )

    BasicTextField(
        value = value,
        maxLines = 1,
        textStyle = TextStyle(color = primaryColor, fontSize = 12.sp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier.indicatorLine(
            enabled = true,
            isError = false,
            interactionSource = textFieldInteractionSource,
            colors = colors
        ),
        onValueChange = { newEffectStr ->
            if (newEffectStr.length > MAX_INPUT_LENGTH)
                return@BasicTextField

            effectInput = newEffectStr

            if (audioEffectsUIHandler.isParamInputValid(newEffectStr)) {
                val newEffectVal = newEffectStr.toFloat()
                onValueChanged(audioEffectsUIHandler, newEffectVal)
                effectVal = newEffectVal
            }
        },
    ) {
        TextFieldDefaults.DecorationBox(
            value = value,
            innerTextField = it,
            enabled = true,
            singleLine = true,
            visualTransformation = VisualTransformation.None,
            interactionSource = textFieldInteractionSource,
            contentPadding = PaddingValues(0.dp),
            colors = colors,
            container = {},
        )
    }
}