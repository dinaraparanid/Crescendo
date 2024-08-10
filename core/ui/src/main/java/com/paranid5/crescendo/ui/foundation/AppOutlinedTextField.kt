package com.paranid5.crescendo.ui.foundation

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography

@Composable
fun AppOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = colors.text.primary,
    style: TextStyle = typography.regular,
    placeholder: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) = BasicTextField(
    value = value,
    singleLine = true,
    onValueChange = onValueChange,
    modifier = modifier,
    textStyle = style.copy(color = color),
    keyboardOptions = keyboardOptions,
    decorationBox = @Composable { innerTextField ->
        OutlinedTextFieldDecorationBox(
            value = value,
            innerTextField = innerTextField,
            placeholder = placeholder,
            label = label,
        )
    }
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OutlinedTextFieldDecorationBox(
    value: String,
    textFieldColors: TextFieldColors = outlinedTextColors,
    innerTextField: @Composable () -> Unit,
    placeholder: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
) = OutlinedTextFieldDefaults.DecorationBox(
    value = value,
    innerTextField = innerTextField,
    placeholder = placeholder,
    label = label,
    singleLine = true,
    enabled = true,
    interactionSource = remember { MutableInteractionSource() },
    visualTransformation = VisualTransformation.None,
    colors = textFieldColors,
)

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
        focusedBorderColor = colors.primary,
        unfocusedBorderColor = colors.primary,
        disabledBorderColor = colors.primary,
        errorBorderColor = colors.primary,
    )