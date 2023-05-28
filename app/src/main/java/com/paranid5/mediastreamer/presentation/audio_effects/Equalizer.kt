package com.paranid5.mediastreamer.presentation.audio_effects

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paranid5.mediastreamer.EQUALIZER_DATA
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.data.eq.EqualizerData
import com.paranid5.mediastreamer.data.eq.EqualizerParameters
import com.paranid5.mediastreamer.presentation.ui.theme.LocalAppColors
import com.paranid5.mediastreamer.presentation.ui.utils.Spinner
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

private const val TAG = "EqualizerView"

@Composable
internal fun Equalizer(modifier: Modifier = Modifier) = Column(modifier) {
    PresetSpinner(Modifier.fillMaxWidth())
    Bands(Modifier.padding(top = 15.dp).fillMaxWidth())
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
    val customPresetIndex by remember { derivedStateOf { equalizerData!!.presets.size } }
    val equalizerParam by remember { derivedStateOf { equalizerData!!.currentParameter } }
    val isEQParamBands by remember { derivedStateOf { equalizerParam == EqualizerParameters.BANDS } }

    var selectedItemInd by remember {
        mutableStateOf(
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
            selectedItemInd = curItemInd,
            modifier = Modifier.fillMaxWidth().align(Alignment.CenterStart),
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
            modifier = Modifier.align(Alignment.CenterEnd).height(20.dp)
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

        Row(Modifier.fillMaxWidth().align(Alignment.Center)) {
            equalizerData!!.bandLevels.indices.forEach {
                Band(
                    index = it,
                    presentLvlsDbState = presentLvlsDbState,
                    pointsState = pointsState,
                    modifier = Modifier.weight(1F).padding(horizontal = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun BandsCurve(
    pointsState: SnapshotStateList<Offset>,
    modifier: Modifier = Modifier
) {
    val primaryColor = LocalAppColors.current.value.primary
    Log.d(TAG, "${pointsState.toList()}")

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
    BandDbLabel(
        index = index,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )

    BandSlider(
        index = index,
        presentLvlsDbState = presentLvlsDbState,
        pointsState = pointsState,
        modifier = Modifier
            .padding(top = 10.dp)
            .size(width = 30.dp, height = 200.dp)
            .align(Alignment.CenterHorizontally)
    )

    BandHzLabel(
        index = index,
        modifier = Modifier
            .padding(top = 10.dp)
            .align(Alignment.CenterHorizontally)
    )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BandSlider(
    index: Int,
    presentLvlsDbState: SnapshotStateList<Float>,
    pointsState: SnapshotStateList<Offset>,
    modifier: Modifier = Modifier,
    equalizerDataState: MutableStateFlow<EqualizerData?> = koinInject(named(EQUALIZER_DATA)),
    audioEffectsUIHandler: AudioEffectsUIHandler = koinInject()
) {
    val context = LocalContext.current
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val primaryColor = LocalAppColors.current.value.primary

    val equalizerData by equalizerDataState.collectAsState()

    val minDb by remember { derivedStateOf { equalizerData!!.minBandLevel / 1000F } }
    val maxDb by remember { derivedStateOf { equalizerData!!.maxBandLevel / 1000F } }

    var sliderYPos by remember { mutableStateOf(0F) }
    var sliderHeight by remember { mutableStateOf(0) }

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
        Slider(
            value = presentLvlsDbState[index],
            valueRange = minDb..maxDb,
            colors = SliderDefaults.colors(activeTrackColor = primaryColor),
            modifier = Modifier
                .align(Alignment.Center)
                .onGloballyPositioned {
                    sliderYPos = it.positionInWindow().y
                    sliderHeight = it.size.height
                },
            onValueChange = { level ->
                equalizerData!!.bandLevels.forEachIndexed { ind, mdb ->
                    presentLvlsDbState[ind] = mdb / 1000F
                }

                presentLvlsDbState[index] = level

                val newLevels = equalizerData!!.bandLevels.toMutableList().also {
                    it[index] = (level * 1000).toInt().toShort()
                }

                audioEffectsUIHandler.storeAndSwitchToBandsAsync(context, newLevels)
            },
            thumb = {
                Image(
                    painter = painterResource(R.drawable.audio_band_button),
                    contentDescription = stringResource(R.string.equalizer_band),
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .height(20.dp)
                        .onGloballyPositioned {
                            pointsState[index] = it
                                .positionInWindow()
                                .let { offset ->
                                    offset.copy(y = offset.y + sliderHeight * 2 - sliderYPos - 30)
                                }
                        }
                )
            },
        )

        Image(
            modifier = Modifier.fillMaxWidth().align(Alignment.Center),
            painter = painterResource(R.drawable.audio_track_horizontal_night_transparent),
            contentDescription = stringResource(R.string.equalizer_band),
            contentScale = ContentScale.FillWidth
        )
    }
}

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