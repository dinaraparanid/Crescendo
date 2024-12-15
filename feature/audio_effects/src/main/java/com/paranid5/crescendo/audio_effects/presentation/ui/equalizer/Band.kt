package com.paranid5.crescendo.audio_effects.presentation.ui.equalizer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.paranid5.crescendo.ui.foundation.AppText
import java.util.Locale

@Composable
internal fun Band(
    index: Int,
    presentLvlsDbState: SnapshotStateList<Float>,
    pointsState: SnapshotStateList<Offset>,
    state: AudioEffectsState,
    onUiIntent: (AudioEffectsUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(dimensions.padding.medium),
) {
    BandDbLabel(
        index = index,
        presentLvlsDbState = presentLvlsDbState,
    )

    BandController(
        index = index,
        presentLvlsDbState = presentLvlsDbState,
        pointsState = pointsState,
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier.weight(1F),
    )

    BandHzLabel(
        index = index,
        state = state,
    )
}

@Composable
private fun BandDbLabel(
    index: Int,
    presentLvlsDbState: SnapshotStateList<Float>,
    modifier: Modifier = Modifier,
) {
    val realLvlDb = presentLvlsDbState[index]

    AppText(
        text = String.format(
            Locale.getDefault(),
            "%.2f %s",
            realLvlDb,
            stringResource(R.string.audio_effects_decibel),
        ),
        style = typography.captionSm.copy(
            textAlign = TextAlign.Center,
            color = colors.text.primary,
        ),
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

    AppText(
        modifier = modifier,
        text = "$bandHz ${stringResource(R.string.audio_effects_hertz)}",
        maxLines = 1,
        style = typography.captionSm.copy(
            textAlign = TextAlign.Center,
            color = colors.text.primary,
        ),
    )
}
