package com.paranid5.crescendo.audio_effects.presentation.ui.equalizer

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
import com.paranid5.crescendo.audio_effects.view_model.AudioEffectsState
import com.paranid5.crescendo.audio_effects.view_model.AudioEffectsUiIntent
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.domain.audio_effects.entity.EqualizerData.Companion.MILLIBELS_IN_DECIBEL
import java.util.Locale

@Composable
internal fun Band(
    index: Int,
    presentLvlsDbState: SnapshotStateList<Float>,
    pointsState: SnapshotStateList<Offset>,
    state: AudioEffectsState,
    onUiIntent: (AudioEffectsUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = Column(modifier) {
    BandDbLabel(
        index = index,
        presentLvlsDbState = presentLvlsDbState,
        modifier = Modifier.align(Alignment.CenterHorizontally),
    )

    Spacer(Modifier.height(dimensions.padding.medium))

    BandController(
        index = index,
        presentLvlsDbState = presentLvlsDbState,
        pointsState = pointsState,
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier
            .fillMaxWidth()
            .weight(1F),
    )

    Spacer(Modifier.height(dimensions.padding.medium))

    BandHzLabel(
        index = index,
        state = state,
        modifier = Modifier.align(Alignment.CenterHorizontally),
    )
}

@Composable
private fun BandDbLabel(
    index: Int,
    presentLvlsDbState: SnapshotStateList<Float>,
    modifier: Modifier = Modifier,
) {
    val realLvlDb = presentLvlsDbState[index]

    Text(
        text = String.format(
            Locale.getDefault(),
            "%.2f %s",
            realLvlDb,
            stringResource(R.string.audio_effects_decibel),
        ),
        textAlign = TextAlign.Center,
        color = colors.text.primary,
        style = typography.captionSm,
        maxLines = 1,
        modifier = modifier,
    )
}

@Composable
private fun BandHzLabel(
    index: Int,
    state: AudioEffectsState,
    modifier: Modifier = Modifier,
) {
    val bandFrequencies = remember(state.equalizerUiState?.bandFrequencies) {
        state.equalizerUiState?.bandFrequencies
    }

    val bandHz by remember(bandFrequencies, index) {
        derivedStateOf {
            val mdb = bandFrequencies?.getOrNull(index) ?: 0
            mdb / MILLIBELS_IN_DECIBEL
        }
    }

    Text(
        text = "$bandHz ${stringResource(R.string.audio_effects_hertz)}",
        textAlign = TextAlign.Center,
        color = colors.text.primary,
        style = typography.captionSm,
        maxLines = 1,
        modifier = modifier,
    )
}
