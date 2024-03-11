package com.paranid5.crescendo.audio_effects.presentation.view.equalizer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.paranid5.crescendo.audio_effects.presentation.view.getBandTrackModel
import com.paranid5.crescendo.core.common.eq.EqualizerData
import com.paranid5.crescendo.core.impl.di.EQUALIZER_DATA
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.utils.extensions.collectLatestAsState
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@Composable
internal fun BandController(
    index: Int,
    presentLvlsDbState: SnapshotStateList<Float>,
    pointsState: SnapshotStateList<Offset>,
    modifier: Modifier = Modifier,
    equalizerDataState: MutableStateFlow<EqualizerData?> = koinInject(named(EQUALIZER_DATA)),
) {
    val equalizerData by equalizerDataState.collectLatestAsState()

    val minDb by remember(equalizerData?.minBandLevel) {
        derivedStateOf { (equalizerData?.minBandLevel ?: 0) / 1000F }
    }

    val maxDb by remember(equalizerData?.maxBandLevel) {
        derivedStateOf { (equalizerData?.maxBandLevel ?: 0) / 1000F }
    }

    val sliderYPosState = remember { mutableFloatStateOf(0F) }
    val sliderWidthState = remember { mutableIntStateOf(1) }
    val sliderHeightState = remember { mutableIntStateOf(1) }

    val bandTrackModel = getBandTrackModel(
        width = sliderWidthState.intValue,
        height = sliderHeightState.intValue
    )

    Box(modifier.controllerModifier()) {
        BandSlider(
            index = index,
            presentLvlsDbState = presentLvlsDbState,
            pointsState = pointsState,
            minDb = minDb,
            maxDb = maxDb,
            sliderYPosState = sliderYPosState,
            sliderWidthState = sliderWidthState,
            sliderHeightState = sliderHeightState,
            equalizerData = equalizerData,
            modifier = Modifier.align(Alignment.Center),
            thumbModifier = Modifier.align(Alignment.Center)
        )

        EqualizerTrack(
            bandTrackModel = bandTrackModel,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun EqualizerTrack(bandTrackModel: ImageRequest, modifier: Modifier = Modifier) =
    AsyncImage(
        model = bandTrackModel,
        contentDescription = stringResource(R.string.equalizer_band),
        contentScale = ContentScale.Fit,
        alignment = Alignment.Center,
        modifier = modifier
    )

private fun Modifier.controllerModifier() =
    this
        .rotatedController()
        .placedController()

private fun Modifier.rotatedController() =
    this.graphicsLayer {
        rotationZ = 270F
        transformOrigin = TransformOrigin(0F, 0F)
    }

private fun Modifier.placedController() =
    this.layout { measurable, constraints ->
        val placeable = placeableControllerOrNull(measurable, constraints)

        layout(placeable?.height ?: 1, placeable?.width ?: 1) {
            placeable?.place(-placeable.width, 0)
        }
    }

private fun placeableControllerOrNull(measurable: Measurable, constraints: Constraints) =
    try {
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