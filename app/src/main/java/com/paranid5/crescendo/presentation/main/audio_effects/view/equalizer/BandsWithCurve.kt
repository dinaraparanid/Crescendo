package com.paranid5.crescendo.presentation.main.audio_effects.view.equalizer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.paranid5.crescendo.EQUALIZER_DATA
import com.paranid5.crescendo.domain.eq.EqualizerData
import com.paranid5.crescendo.presentation.ui.extensions.collectLatestAsState
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@Composable
fun BandsWithCurve(
    modifier: Modifier = Modifier,
    equalizerDataState: MutableStateFlow<EqualizerData?> = koinInject(named(EQUALIZER_DATA))
) {
    val equalizerData by equalizerDataState.collectLatestAsState()

    val pointsState = remember {
        mutableStateListOf(*Array(equalizerData?.bandLevels?.size ?: 0) { Offset.Zero })
    }

    Box(modifier) {
        BandsCurve(
            pointsState = pointsState,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        )

        Bands(
            pointsState = pointsState,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        )
    }
}