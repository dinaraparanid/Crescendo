package com.paranid5.crescendo.presentation.main.audio_effects.view.equalizer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.EQUALIZER_DATA
import com.paranid5.crescendo.R
import com.paranid5.crescendo.domain.eq.EqualizerData
import com.paranid5.crescendo.presentation.main.audio_effects.AudioEffectsViewModel
import com.paranid5.crescendo.presentation.ui.extensions.collectLatestAsState
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@Composable
fun Band(
    index: Int,
    presentLvlsDbState: SnapshotStateList<Float>,
    pointsState: SnapshotStateList<Offset>,
    viewModel: AudioEffectsViewModel,
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
        viewModel = viewModel,
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
    equalizerDataState: MutableStateFlow<EqualizerData?> = koinInject(named(EQUALIZER_DATA)),
) {
    val colors = LocalAppColors.current
    val equalizerData by equalizerDataState.collectLatestAsState()
    val bandHz = (equalizerData?.bandFrequencies?.get(index) ?: 0) / 1000

    Text(
        text = "$bandHz ${stringResource(R.string.hertz)}",
        textAlign = TextAlign.Center,
        color = colors.primary,
        fontSize = 8.sp,
        maxLines = 1,
        modifier = modifier
    )
}