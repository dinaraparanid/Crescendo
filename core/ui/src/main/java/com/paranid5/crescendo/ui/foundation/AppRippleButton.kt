package com.paranid5.crescendo.ui.foundation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme

@Composable
fun AppRippleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    rippleColor: Color = AppTheme.colors.selection.selected,
    rippleBounded: Boolean = true,
    isEnabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = ButtonDefaults.shape,
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = AppTheme.colors.button.primary,
        contentColor = AppTheme.colors.text.onButton,
        disabledContainerColor = AppTheme.colors.button.disabled,
        disabledContentColor = AppTheme.colors.text.tertiriary,
    ),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit,
) = Button(
    onClick = onClick,
    enabled = isEnabled,
    shape = shape,
    interactionSource = interactionSource,
    border = border,
    colors = colors,
    elevation = elevation,
    contentPadding = contentPadding,
    content = content,
    modifier = modifier
        .clip(shape)
        .indication(
            interactionSource = interactionSource,
            indication = rememberRipple(
                bounded = rippleBounded,
                color = rippleColor,
            )
        ),
)

@Composable
fun AppOutlinedRippleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    rippleColor: Color = AppTheme.colors.selection.selected,
    rippleBounded: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = ButtonDefaults.shape,
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit,
) = OutlinedButton(
    onClick = onClick,
    shape = shape,
    interactionSource = interactionSource,
    border = border,
    colors = colors,
    elevation = elevation,
    contentPadding = contentPadding,
    content = content,
    modifier = modifier
        .clip(shape)
        .indication(
            interactionSource = interactionSource,
            indication = rememberRipple(
                bounded = rippleBounded,
                color = rippleColor,
            )
        ),
)
