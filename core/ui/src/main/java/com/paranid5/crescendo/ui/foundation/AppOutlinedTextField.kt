package com.paranid5.crescendo.ui.foundation

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import com.paranid5.crescendo.core.resources.ui.theme.AppTextSelectionColors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography

@Composable
fun AppOutlinedTextField(
    value: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    color: Color = colors.text.onTextField,
    style: TextStyle = typography.regular,
    textFieldColors: TextFieldColors = outlinedTextColors,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    label: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onValueChange: (input: String) -> Unit,
) = CompositionLocalProvider(LocalTextSelectionColors provides AppTextSelectionColors) {
    BasicTextField(
        value = value,
        singleLine = true,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = style.copy(color = color),
        cursorBrush = SolidColor(colors.selection.selected),
        keyboardOptions = keyboardOptions,
        decorationBox = @Composable { innerTextField ->
            OutlinedTextFieldDecorationBox(
                value = value,
                isError = isError,
                textFieldColors = textFieldColors,
                innerTextField = innerTextField,
                label = label,
                trailingIcon = trailingIcon,
                supportingText = supportingText,
                shape = shape,
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OutlinedTextFieldDecorationBox(
    value: String,
    isError: Boolean,
    textFieldColors: TextFieldColors,
    shape: Shape,
    innerTextField: @Composable () -> Unit,
    label: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    supportingText: @Composable (() -> Unit)?,
) {
    val interactionSource = remember { MutableInteractionSource() }

    OutlinedTextFieldDefaults.DecorationBox(
        value = value,
        innerTextField = innerTextField,
        label = label,
        trailingIcon = trailingIcon,
        supportingText = supportingText,
        singleLine = true,
        enabled = true,
        isError = isError,
        interactionSource = interactionSource,
        visualTransformation = VisualTransformation.None,
        colors = textFieldColors,
        container = {
            OutlinedTextFieldDefaults.ContainerBox(
                enabled = true,
                isError = isError,
                interactionSource = interactionSource,
                colors = textFieldColors,
                shape = shape,
            )
        }
    )
}

private inline val outlinedTextColors: TextFieldColors
    @Composable
    get() = OutlinedTextFieldDefaults.colors(
        focusedTextColor = colors.text.primary,
        unfocusedTextColor = colors.text.primary,
        disabledTextColor = colors.text.primary,
        errorTextColor = colors.text.primary,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
        focusedBorderColor = colors.selection.selected,
        unfocusedBorderColor = colors.selection.selected,
        disabledBorderColor = colors.selection.selected,
        errorBorderColor = colors.error,
        cursorColor = colors.selection.selected,
        focusedTrailingIconColor = colors.selection.selected,
        unfocusedTrailingIconColor = colors.selection.selected,
        disabledTrailingIconColor = colors.selection.selected,
        errorTrailingIconColor = colors.error,
        focusedLabelColor = colors.text.primary,
        unfocusedLabelColor = colors.text.primary,
        disabledLabelColor = colors.text.primary,
        errorLabelColor = colors.error,
        selectionColors = AppTextSelectionColors,
    )
