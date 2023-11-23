package com.paranid5.crescendo.presentation.main.audio_effects

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.R
import com.paranid5.crescendo.data.utils.extensions.toAngle
import com.paranid5.crescendo.domain.StorageHandler
import com.paranid5.crescendo.domain.utils.extensions.PresetReverbExt
import com.paranid5.crescendo.presentation.ui.theme.Disabled
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import org.koin.compose.koinInject
import kotlin.math.PI
import kotlin.math.atan2

private const val TAG = "BassAndReverbView"

@SuppressLint("LogConditional")
@Composable
fun BassAndReverb(
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = koinInject(),
    audioEffectsUIHandler: AudioEffectsUIHandler = koinInject()
) {
    val context = LocalContext.current
    var bassInitialValue by remember { mutableFloatStateOf(0F) }
    var reverbInitialPreset by remember { mutableFloatStateOf(0F) }
    val reverbPresets = PresetReverbExt.presets

    LaunchedEffect(Unit) {
        bassInitialValue = storageHandler.bassStrengthState.value.toFloat()
        reverbInitialPreset = storageHandler.reverbPresetState.value.toFloat()
        Log.d(TAG, "BASS INIT: $bassInitialValue")
    }

    Row(modifier.fillMaxWidth()) {
        AudioControllerWithLabel(
            initialValue = bassInitialValue,
            contentDescription = stringResource(R.string.bass),
            valueRange = 0F..1000F,
            modifier = Modifier.weight(1F)
        ) {
            val value = it.toInt().toShort()
            audioEffectsUIHandler.storeAndSendBassStrengthAsync(context, value)
            Log.d(TAG, "Bass: $value")
        }

        Spacer(Modifier.width(20.dp))

        AudioControllerWithLabel(
            initialValue = reverbInitialPreset,
            contentDescription = stringResource(R.string.reverb),
            valueRange = 0F..reverbPresets.size.toFloat(),
            modifier = Modifier.weight(1F)
        ) {
            val value = it.toInt().toShort()
            audioEffectsUIHandler.storeAndSendReverbPresetAsync(context, value)
            Log.d(TAG, "Reverb: $value")
        }
    }
}

@SuppressLint("LogConditional")
@Composable
private fun AudioController(
    initialValue: Float,
    contentDescription: String,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0F..1F,
    angleRange: ClosedFloatingPointRange<Float> = -135F..135F,
    onValueChange: (Float) -> Unit
) {
    val primaryColor = LocalAppColors.current.value.primary

    var inputValue by remember { mutableFloatStateOf(initialValue) }

    var rotationAngle by remember {
        mutableFloatStateOf(initialValue.toAngle(valueRange, angleRange))
    }

    val angleRangeDistance by remember {
        derivedStateOf { angleRange.endInclusive - angleRange.start }
    }

    var fixedRotationAngle by remember {
        mutableFloatStateOf(rotationAngle + angleRangeDistance / 2)
    }

    val arcStartAngle by remember {
        derivedStateOf { angleRange.start + 270 }
    }

    Log.d(TAG, "Bass: value $inputValue angle $rotationAngle")
    Log.d(TAG, "Fixed angle: $fixedRotationAngle")

    var touchX by remember { mutableFloatStateOf(0F) }
    var touchY by remember { mutableFloatStateOf(0F) }
    var touchOffset by remember { mutableStateOf(Offset.Zero) }

    var centerX by remember { mutableFloatStateOf(0F) }
    var centerY by remember { mutableFloatStateOf(0F) }

    val anglePercent by remember {
        derivedStateOf { angleRangeDistance / 100 }
    }

    val valuePercent by remember {
        derivedStateOf { (valueRange.endInclusive - valueRange.start) / 100 }
    }

    LaunchedEffect(initialValue) {
        inputValue = initialValue
        rotationAngle = initialValue.toAngle(valueRange, angleRange)
    }

    LaunchedEffect(rotationAngle) {
        fixedRotationAngle = rotationAngle + angleRangeDistance / 2
    }

    Box(modifier) {
        Canvas(
            Modifier
                .size(85.dp)
                .align(Alignment.Center)
        ) {
            drawArc(
                color = Disabled,
                startAngle = arcStartAngle,
                sweepAngle = angleRangeDistance,
                useCenter = true
            )

            drawArc(
                color = primaryColor,
                startAngle = arcStartAngle,
                sweepAngle = fixedRotationAngle,
                useCenter = true
            )
        }

        Image(
            painter = painterResource(R.drawable.audio_controller),
            contentDescription = contentDescription,
            modifier = Modifier
                .align(Alignment.Center)
                .onGloballyPositioned {
                    val boundsInWindow = it.boundsInWindow()
                    centerX = boundsInWindow.size.width / 2F
                    centerY = boundsInWindow.size.height / 2F
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, offset ->
                        change.consume()
                        touchOffset += offset

                        val degrees = -atan2(
                            centerX - touchOffset.x,
                            centerY - touchOffset.y
                        ) * (180 / PI).toFloat()

                        if (degrees !in angleRange)
                            return@detectDragGestures

                        touchX = touchOffset.x
                        touchY = touchOffset.y

                        val rotationPercents = (degrees - angleRange.start) / anglePercent
                        rotationAngle = degrees
                        inputValue = valuePercent * rotationPercents
                        onValueChange(inputValue)
                    }
                }
                .rotate(rotationAngle)
        )
    }
}

@Composable
private fun AudioControllerWithLabel(
    initialValue: Float,
    contentDescription: String,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0F..1F,
    angleRange: ClosedFloatingPointRange<Float> = -135F..135F,
    onValueChange: (Float) -> Unit
) {
    val primaryColor = LocalAppColors.current.value.primary

    Column(modifier) {
        AudioController(
            initialValue = initialValue,
            contentDescription = contentDescription,
            valueRange = valueRange,
            angleRange = angleRange,
            onValueChange = onValueChange,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(100.dp)
        )

        Text(
            text = contentDescription,
            color = primaryColor,
            fontSize = 10.sp,
            maxLines = 1,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}