package com.paranid5.crescendo.audio_effects.presentation.ui.pitch_speed

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import com.paranid5.crescendo.audio_effects.domain.isParamInputValid
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AudioEffectTextField(
    value: String,
    effectInputState: MutableState<String>,
    effectValState: MutableFloatState,
    onValueChanged: (effectVal: Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val textFieldInteractionSource = remember {
        MutableInteractionSource()
    }

    val textColors = textColors()

    BasicTextField(
        value = value,
        maxLines = 1,
        textStyle = typography.caption.copy(color = colors.text.primary),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier.indicatorLine(
            enabled = true,
            isError = false,
            interactionSource = textFieldInteractionSource,
            colors = textColors,
        ),
        onValueChange = { newEffectStr ->
            tryUpdateEffect(
                newEffectStr = newEffectStr,
                effectInputState = effectInputState,
                effectValState = effectValState,
                onValueChanged = onValueChanged,
            )
        },
    ) {
        DecorationBox(
            value = value,
            innerTextField = it,
            textFieldInteractionSource = textFieldInteractionSource,
            colors = textColors,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DecorationBox(
    value: String,
    textFieldInteractionSource: MutableInteractionSource,
    colors: TextFieldColors,
    innerTextField: @Composable () -> Unit,
) = TextFieldDefaults.DecorationBox(
    value = value,
    innerTextField = innerTextField,
    enabled = true,
    singleLine = true,
    visualTransformation = VisualTransformation.None,
    interactionSource = textFieldInteractionSource,
    contentPadding = PaddingValues(dimensions.padding.zero),
    colors = colors,
    container = {},
)

@Composable
private fun textColors() = TextFieldDefaults.colors(
    cursorColor = colors.selection.selected,
    focusedTextColor = colors.text.primary,
    unfocusedTextColor = colors.text.primary,
    focusedIndicatorColor = colors.primary,
    unfocusedIndicatorColor = colors.primary,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
)

private fun tryUpdateEffect(
    newEffectStr: String,
    effectInputState: MutableState<String>,
    effectValState: MutableFloatState,
    onValueChanged: (effectVal: Float) -> Unit,
) {
    if (newEffectStr.length > MaxInputLength)
        return

    effectInputState.value = newEffectStr

    if (isParamInputValid(newEffectStr)) {
        val newEffectVal = newEffectStr.toFloat()
        onValueChanged(newEffectVal)
        effectValState.floatValue = newEffectVal
    }
}
