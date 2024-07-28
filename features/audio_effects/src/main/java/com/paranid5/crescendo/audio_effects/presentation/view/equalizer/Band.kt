package com.paranid5.crescendo.audio_effects.presentation.view.equalizer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.audio_effects.presentation.AudioEffectsViewModel
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors
import com.paranid5.crescendo.domain.audio_effects.entity.EqualizerData
import com.paranid5.crescendo.utils.extensions.collectLatestAsState
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun Band(
    index: Int,
    presentLvlsDbState: SnapshotStateList<Float>,
    pointsState: SnapshotStateList<Offset>,
    modifier: Modifier = Modifier
) = Column(modifier) {
    BandDbLabel(
        index = index,
        presentLvlsDbState = presentLvlsDbState,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )

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
    presentLvlsDbState: SnapshotStateList<Float>,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAppColors.current
    val realLvlDb = presentLvlsDbState[index]

    Text(
        text = String.format("%.2f %s", realLvlDb, stringResource(R.string.decibel)),
        textAlign = TextAlign.Center,
        color = colors.primary,
        fontSize = 8.sp,
        maxLines = 1,
        modifier = modifier
    )
}

@Composable
private fun BandHzLabel(
    index: Int,
    modifier: Modifier = Modifier,
    viewModel: AudioEffectsViewModel = koinViewModel()
) {
    val colors = LocalAppColors.current
    val equalizerData by viewModel.equalizerState.collectLatestAsState()

    val bandHz by remember(equalizerData?.bandFrequencies, index) {
        derivedStateOf {
            val mdb = equalizerData?.bandFrequencies?.getOrNull(index) ?: 0
            mdb / EqualizerData.MILLIBELS_IN_DECIBEL
        }
    }

    Text(
        text = "$bandHz ${stringResource(R.string.hertz)}",
        textAlign = TextAlign.Center,
        color = colors.primary,
        fontSize = 8.sp,
        maxLines = 1,
        modifier = modifier,
    )
}