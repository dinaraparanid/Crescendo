package com.paranid5.crescendo.presentation.main.trimmer.views.effects

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import com.paranid5.crescendo.presentation.ui.theme.TransparentUtility

@Composable
fun <V : Number> EffectController(
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

    Spacer(Modifier.height(4.dp))

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
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current

    Text(
        text = text,
        modifier = modifier,
        color = colors.fontColor,
        fontSize = 12.sp
    )
}

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
    )

    Spacer(Modifier.width(8.dp))

    EffectSlider(
        initialValue = initialValue,
        minValue = minValue,
        maxValue = maxValue,
        steps = steps,
        setEffect = setEffect,
        modifier = Modifier.weight(1F)
    )
}

@Composable
private fun EffectIcon(
    iconPainter: Painter,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val colors = LocalAppColors.current

    Icon(
        painter = iconPainter,
        contentDescription = contentDescription,
        tint = colors.primary,
        modifier = modifier.size(20.dp)
    )
}

@Composable
private inline fun <V : Number> EffectSlider(
    initialValue: V,
    minValue: V,
    maxValue: V,
    steps: Int,
    crossinline setEffect: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    var curPosition by remember { mutableFloatStateOf(initialValue.toFloat()) }

    Slider(
        value = curPosition,
        valueRange = minValue.toFloat()..maxValue.toFloat(),
        steps = steps,
        modifier = modifier,
        colors = SliderDefaults.colors(
            thumbColor = colors.primary,
            activeTrackColor = colors.primary,
            inactiveTrackColor = TransparentUtility
        ),
        onValueChange = {
            curPosition = it
            setEffect(it)
        }
    )
}