package com.paranid5.crescendo.presentation.audio_effects

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.paranid5.crescendo.EQUALIZER_DATA
import com.paranid5.crescendo.R
import com.paranid5.crescendo.data.eq.EqualizerData
import com.paranid5.crescendo.data.eq.EqualizerParameters
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import com.paranid5.crescendo.presentation.ui.utils.Spinner
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

private const val TAG = "EqualizerView"

@Composable
fun Equalizer(modifier: Modifier = Modifier) = Column(modifier) {
    PresetSpinner(Modifier.fillMaxWidth())
    Spacer(Modifier.height(15.dp))
    Bands(Modifier.fillMaxWidth())
}

@Composable
private fun PresetSpinner(
    modifier: Modifier = Modifier,
    equalizerDataState: MutableStateFlow<EqualizerData?> = koinInject(named(EQUALIZER_DATA)),
    audioEffectsUIHandler: AudioEffectsUIHandler = koinInject()
) {
    val context = LocalContext.current
    val primaryColor = LocalAppColors.current.value.primary

    val equalizerData by equalizerDataState.collectAsState()

    val customPresetIndex by remember {
        derivedStateOf { equalizerData!!.presets.size }
    }

    val equalizerParam by remember {
        derivedStateOf { equalizerData!!.currentParameter }
    }

    val isEQParamBands by remember {
        derivedStateOf { equalizerParam == EqualizerParameters.BANDS }
    }

    var selectedItemInd by remember {
        mutableIntStateOf(
            when (equalizerData!!.currentParameter) {
                EqualizerParameters.BANDS -> customPresetIndex
                EqualizerParameters.PRESET -> equalizerData!!.currentPreset.toInt()
                EqualizerParameters.NIL -> 0
            }
        )
    }

    val curItemInd by remember {
        derivedStateOf { if (isEQParamBands) customPresetIndex else selectedItemInd }
    }

    Box(modifier) {
        Spinner(
            items = equalizerData!!.presets.toList() + stringResource(R.string.custom),
            selectedItemIndexes = listOf(curItemInd),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterStart),
            onItemSelected = { ind, _ ->
                selectedItemInd = ind

                when (ind) {
                    customPresetIndex -> audioEffectsUIHandler.switchToBandsAsync(context)

                    else -> audioEffectsUIHandler.storeAndSwitchToPresetAsync(
                        context = context,
                        preset = ind.toShort()
                    )
                }
            }
        )

        Image(
            painter = painterResource(R.drawable.arrow_down),
            contentDescription = stringResource(R.string.eq_presets),
            colorFilter = ColorFilter.tint(primaryColor),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .height(20.dp)
        )
    }
}

@Composable
private fun Bands(
    modifier: Modifier = Modifier,
    equalizerDataState: MutableStateFlow<EqualizerData?> = koinInject(named(EQUALIZER_DATA))
) {
    val equalizerData by equalizerDataState.collectAsState()
    val equalizerPreset by remember { derivedStateOf { equalizerData!!.currentPreset } }

    val pointsState = remember {
        mutableStateListOf(*Array(equalizerData!!.bandLevels.size) { Offset.Zero })
    }

    val presentLvlsDbState = remember {
        mutableStateListOf(*equalizerData!!.bandLevels.map { it / 1000F }.toTypedArray())
    }

    LaunchedEffect(equalizerPreset) {
        equalizerData!!.bandLevels.forEachIndexed { index, mdb ->
            presentLvlsDbState[index] = mdb / 1000F
        }
    }

    Box(modifier) {
        BandsCurve(
            pointsState = pointsState,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        )

        Row(
            Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {
            Spacer(Modifier.width(10.dp))

            equalizerData!!.bandLevels.indices.forEach {
                Band(
                    index = it,
                    presentLvlsDbState = presentLvlsDbState,
                    pointsState = pointsState,
                    modifier = Modifier.weight(1F)
                )

                Spacer(Modifier.width(10.dp))
            }
        }
    }
}

@Suppress("UnnecessaryVariable")
@SuppressLint("LogConditional")
@Composable
private fun BandsCurve(
    pointsState: SnapshotStateList<Offset>,
    modifier: Modifier = Modifier
) {
    val primaryColor = LocalAppColors.current.value.primary
    Log.d(TAG, "Bands: ${pointsState.toList()}")

    val path = Path().apply {
        moveTo(pointsState[0].x, pointsState[0].y)

        (1 until pointsState.size).forEach { i ->
            val (prevX, prevY) = pointsState[i - 1]
            val (curX, curY) = pointsState[i]

            val conX1 = (prevX + curX) / 2F
            val conX2 = (prevX + curX) / 2F

            val conY1 = prevY
            val conY2 = curY

            cubicTo(
                x1 = conX1,
                y1 = conY1,
                x2 = conX2,
                y2 = conY2,
                x3 = curX,
                y3 = curY
            )
        }
    }

    Canvas(modifier) {
        drawPath(
            path = path,
            color = primaryColor,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
}

@Composable
private fun Band(
    index: Int,
    presentLvlsDbState: SnapshotStateList<Float>,
    pointsState: SnapshotStateList<Offset>,
    modifier: Modifier = Modifier
) = Column(modifier) {
    BandDbLabel(index, Modifier.align(Alignment.CenterHorizontally))

    Spacer(Modifier.height(10.dp))

    BandController(
        index = index,
        presentLvlsDbState = presentLvlsDbState,
        pointsState = pointsState,
        modifier = Modifier
            .fillMaxWidth()
            .weight(1F)
    )

    Spacer(Modifier.height(10.dp))

    BandHzLabel(index, Modifier.align(Alignment.CenterHorizontally))
}

@Composable
private fun BandDbLabel(
    index: Int,
    modifier: Modifier = Modifier,
    equalizerDataState: MutableStateFlow<EqualizerData?> = koinInject(named(EQUALIZER_DATA)),
) {
    val primaryColor = LocalAppColors.current.value.primary
    val equalizerData by equalizerDataState.collectAsState()
    val realLvlDb by remember { derivedStateOf { equalizerData!!.bandLevels[index] / 1000F } }

    Text(
        text = String.format("%.2f %s", realLvlDb, stringResource(R.string.decibel)),
        textAlign = TextAlign.Center,
        color = primaryColor,
        fontSize = 8.sp,
        maxLines = 1,
        modifier = modifier
    )
}

@Composable
private fun BandController(
    index: Int,
    presentLvlsDbState: SnapshotStateList<Float>,
    pointsState: SnapshotStateList<Offset>,
    modifier: Modifier = Modifier,
    equalizerDataState: MutableStateFlow<EqualizerData?> = koinInject(named(EQUALIZER_DATA)),
) {
    val equalizerData by equalizerDataState.collectAsState()

    val minDb by remember { derivedStateOf { equalizerData!!.minBandLevel / 1000F } }
    val maxDb by remember { derivedStateOf { equalizerData!!.maxBandLevel / 1000F } }

    val sliderYPos = remember { mutableFloatStateOf(0F) }
    val sliderWidth = remember { mutableIntStateOf(1) }
    val sliderHeight = remember { mutableIntStateOf(1) }

    val bandTrackModel = getBandTrackModel(
        width = sliderWidth.intValue,
        height = sliderHeight.intValue
    )

    Box(
        modifier
            .graphicsLayer {
                rotationZ = 270F
                transformOrigin = TransformOrigin(0F, 0F)
            }
            .layout { measurable, constraints ->
                val placeable = try {
                    measurable.measure(
                        Constraints(
                            minWidth = constraints.minHeight,
                            maxWidth = constraints.maxHeight,
                            minHeight = constraints.minWidth,
                            maxHeight = constraints.maxHeight,
                        )
                    )
                } catch (ignored: IllegalArgumentException) {
                    // screen rotation
                    null
                }

                layout(placeable?.height ?: 1, placeable?.width ?: 1) {
                    placeable?.place(-placeable.width, 0)
                }
            }
    ) {
        BandSlider(
            index = index,
            presentLvlsDbState = presentLvlsDbState,
            pointsState = pointsState,
            minDb = minDb,
            maxDb = maxDb,
            sliderYPos = sliderYPos,
            sliderWidth = sliderWidth,
            sliderHeight = sliderHeight,
            equalizerData = equalizerData,
            modifier = Modifier.align(Alignment.Center),
            thumbModifier = Modifier.align(Alignment.Center)
        )

        EqualizerTrack(bandTrackModel = bandTrackModel, modifier = Modifier.fillMaxSize())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BandSlider(
    index: Int,
    presentLvlsDbState: SnapshotStateList<Float>,
    pointsState: SnapshotStateList<Offset>,
    minDb: Float,
    maxDb: Float,
    sliderYPos: MutableFloatState,
    sliderWidth: MutableIntState,
    sliderHeight: MutableIntState,
    equalizerData: EqualizerData?,
    modifier: Modifier = Modifier,
    thumbModifier: Modifier = Modifier,
    audioEffectsUIHandler: AudioEffectsUIHandler = koinInject()
) {
    val context = LocalContext.current
    val primaryColor = LocalAppColors.current.value.primary

    Slider(
        value = presentLvlsDbState[index],
        valueRange = minDb..maxDb,
        colors = SliderDefaults.colors(activeTrackColor = primaryColor),
        modifier = modifier.onGloballyPositioned {
            sliderYPos.floatValue = it.positionInWindow().y
            sliderWidth.intValue = it.size.width
            sliderHeight.intValue = it.size.height
        },
        onValueChange = { level ->
            updateEQLevelAsync(
                level,
                index,
                presentLvlsDbState,
                equalizerData,
                audioEffectsUIHandler,
                context
            )
        },
        thumb = {
            EqualizerThumb(
                index = index,
                pointsState = pointsState,
                sliderWidth = sliderWidth.intValue,
                sliderYPos = sliderYPos.floatValue,
                modifier = thumbModifier
            )
        },
    )
}

private fun updateEQLevelAsync(
    level: Float,
    index: Int,
    presentLvlsDbState: SnapshotStateList<Float>,
    equalizerData: EqualizerData?,
    audioEffectsUIHandler: AudioEffectsUIHandler,
    context: Context
): Job {
    equalizerData!!.bandLevels.forEachIndexed { ind, mdb ->
        presentLvlsDbState[ind] = mdb / 1000F
    }

    presentLvlsDbState[index] = level

    val newLevels = equalizerData.bandLevels.toMutableList().also {
        it[index] = (level * 1000).toInt().toShort()
    }

    return audioEffectsUIHandler.storeAndSwitchToBandsAsync(context, newLevels)
}

@Composable
private fun EqualizerThumb(
    index: Int,
    pointsState: SnapshotStateList<Offset>,
    sliderWidth: Int,
    sliderYPos: Float,
    modifier: Modifier = Modifier
) = Image(
    painter = painterResource(R.drawable.audio_band_button),
    contentDescription = stringResource(R.string.equalizer_band),
    contentScale = ContentScale.FillHeight,
    modifier = modifier
        .height(20.dp)
        .onGloballyPositioned {
            pointsState[index] = it
                .positionInWindow()
                .let { offset ->
                    offset.copy(
                        y = offset.y + sliderWidth / 2
                                - sliderYPos - it.size.width / 2
                    )
                }
        }
)

@Composable
private fun EqualizerTrack(bandTrackModel: ImageRequest, modifier: Modifier = Modifier) =
    AsyncImage(
        model = bandTrackModel,
        contentDescription = stringResource(R.string.equalizer_band),
        contentScale = ContentScale.Fit,
        alignment = Alignment.Center,
        modifier = modifier
    )

@Composable
private fun BandHzLabel(
    index: Int,
    modifier: Modifier = Modifier,
    equalizerDataState: MutableStateFlow<EqualizerData?> = koinInject(named(EQUALIZER_DATA)),
) {
    val primaryColor = LocalAppColors.current.value.primary
    val equalizerData by equalizerDataState.collectAsState()

    Text(
        text = "${equalizerData!!.bandFrequencies[index] / 1000} ${stringResource(R.string.hertz)}",
        textAlign = TextAlign.Center,
        color = primaryColor,
        fontSize = 8.sp,
        maxLines = 1,
        modifier = modifier
    )
}