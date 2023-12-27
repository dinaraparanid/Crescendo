package com.paranid5.crescendo.presentation.main.audio_effects.view.pitch_speed

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.indicatorLine
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.presentation.main.audio_effects.AudioEffectsUIHandler
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AudioEffectTextField(
    value: String,
    effectInputState: MutableState<String>,
    effectValState: MutableFloatState,
    onValueChanged: (effectVal: Float) -> Unit,
    modifier: Modifier = Modifier,
    audioEffectsUIHandler: AudioEffectsUIHandler = koinInject()
) {
    val colors = LocalAppColors.current

    val textFieldInteractionSource = remember {
        MutableInteractionSource()
    }

    val textColors = textColors()

    BasicTextField(
        value = value,
        maxLines = 1,
        textStyle = TextStyle(color = colors.primary, fontSize = 12.sp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier.indicatorLine(
            enabled = true,
            isError = false,
            interactionSource = textFieldInteractionSource,
            colors = textColors
        ),
        onValueChange = { newEffectStr ->
            tryUpdateEffect(
                newEffectStr = newEffectStr,
                effectInputState = effectInputState,
                effectValState = effectValState,
                audioEffectsUIHandler = audioEffectsUIHandler,
                onValueChanged = onValueChanged
            )
        },
    ) {
        DecorationBox(
            value = value,
            innerTextField = it,
            textFieldInteractionSource = textFieldInteractionSource,
            colors = textColors
        )
    }
}

@Composable
private fun textColors(): TextFieldColors {
    val colors = LocalAppColors.current

    return TextFieldDefaults.colors(
        cursorColor = colors.primary,
        focusedTextColor = colors.primary,
        unfocusedTextColor = colors.primary,
        focusedIndicatorColor = colors.primary,
        unfocusedIndicatorColor = colors.primary,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DecorationBox(
    value: String,
    innerTextField: @Composable () -> Unit,
    textFieldInteractionSource: MutableInteractionSource,
    colors: TextFieldColors
) {
    TextFieldDefaults.DecorationBox(
        value = value,
        innerTextField = innerTextField,
        enabled = true,
        singleLine = true,
        visualTransformation = VisualTransformation.None,
        interactionSource = textFieldInteractionSource,
        contentPadding = PaddingValues(0.dp),
        colors = colors,
        container = {},
    )
}

private fun tryUpdateEffect(
    newEffectStr: String,
    effectInputState: MutableState<String>,
    effectValState: MutableFloatState,
    audioEffectsUIHandler: AudioEffectsUIHandler,
    onValueChanged: (effectVal: Float) -> Unit
) {
    if (newEffectStr.length > MAX_INPUT_LENGTH)
        return

    effectInputState.value = newEffectStr

    if (audioEffectsUIHandler.isParamInputValid(newEffectStr)) {
        val newEffectVal = newEffectStr.toFloat()
        onValueChanged(newEffectVal)
        effectValState.floatValue = newEffectVal
    }
}