package com.paranid5.crescendo.presentation.ui.utils

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null
) {
    val colors = LocalAppColors.current.value

    BasicTextField(
        value = value,
        singleLine = true,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = TextStyle(color = colors.inverseSurface),
        decorationBox = @Composable { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = value,
                innerTextField = innerTextField,
                placeholder = placeholder,
                label = label,
                singleLine = true,
                enabled = true,
                interactionSource = remember { MutableInteractionSource() },
                visualTransformation = VisualTransformation.None,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colors.inverseSurface,
                    unfocusedTextColor = colors.inverseSurface,
                    disabledTextColor = colors.inverseSurface,
                    errorTextColor = colors.inverseSurface,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.primary,
                    disabledBorderColor = colors.primary,
                    errorBorderColor = colors.primary
                ),
            )
        }
    )
}