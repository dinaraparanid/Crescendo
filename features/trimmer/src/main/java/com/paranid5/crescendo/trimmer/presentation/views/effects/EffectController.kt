package com.paranid5.crescendo.trimmer.presentation.views.effects

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography

@Composable
internal fun <V : Number> EffectController(
    label: String,
    iconPainter: Painter,
    valueState: V,
    minValue: V,
    maxValue: V,
    setEffect: (Float) -> Unit,
    modifier: Modifier = Modifier,
    steps: Int = 0
) = Column(modifier) {
    EffectLabel(label)

    Spacer(Modifier.height(dimensions.padding.extraSmall))

    EffectIconSlider(
        iconPainter = iconPainter,
        initialValue = valueState,
        minValue = minValue,
        maxValue = maxValue,
        setEffect = setEffect,
        steps = steps,
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.CenterHorizontally)
    )
}

@Composable
private fun EffectLabel(
    text: String,
    modifier: Modifier = Modifier,
) = Text(
    text = text,
    modifier = modifier,
    color = colors.text.primary,
    style = typography.caption,
)

@Composable
private inline fun <V : Number> EffectIconSlider(
    iconPainter: Painter,
    initialValue: V,
    minValue: V,
    maxValue: V,
    steps: Int,
    crossinline setEffect: (Float) -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) = Row(modifier) {
    EffectIcon(
        iconPainter = iconPainter,
        contentDescription = contentDescription,
        modifier = Modifier.align(Alignment.CenterVertically),
    )

    Spacer(Modifier.width(dimensions.padding.small))

    EffectSlider(
        initialValue = initialValue,
        minValue = minValue,
        maxValue = maxValue,
        steps = steps,
        setEffect = setEffect,
        modifier = Modifier
            .weight(1F)
            .align(Alignment.CenterVertically),
    )
}

@Composable
private fun EffectIcon(
    iconPainter: Painter,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) = Icon(
    painter = iconPainter,
    contentDescription = contentDescription,
    tint = colors.primary,
    modifier = modifier,
)

@Composable
private inline fun <V : Number> EffectSlider(
    initialValue: V,
    minValue: V,
    maxValue: V,
    steps: Int,
    crossinline setEffect: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var curPosition by remember {
        mutableFloatStateOf(initialValue.toFloat())
    }

    Slider(
        value = curPosition,
        valueRange = minValue.toFloat()..maxValue.toFloat(),
        steps = steps,
        modifier = modifier,
        colors = SliderDefaults.colors(
            thumbColor = colors.primary,
            activeTrackColor = colors.primary,
            inactiveTrackColor = colors.utils.transparentUtility,
        ),
        onValueChange = {
            curPosition = it
            setEffect(it)
        }
    )
}