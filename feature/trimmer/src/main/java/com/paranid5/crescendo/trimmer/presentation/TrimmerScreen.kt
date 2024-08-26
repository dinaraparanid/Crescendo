package com.paranid5.crescendo.trimmer.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.trimmer.presentation.effects.LifecycleEffect
import com.paranid5.crescendo.trimmer.presentation.effects.LoadTrackEffect
import com.paranid5.crescendo.trimmer.view_model.TrimmerViewModel
import com.paranid5.crescendo.trimmer.view_model.TrimmerViewModelImpl
import com.paranid5.crescendo.ui.foundation.AppLoadingBoxError
import com.paranid5.crescendo.ui.foundation.AppProgressIndicator
import com.paranid5.crescendo.ui.foundation.UiState
import com.paranid5.crescendo.ui.foundation.getOrNull
import com.paranid5.crescendo.utils.doNothing
import com.paranid5.crescendo.utils.extensions.collectLatestAsState
import org.koin.androidx.compose.koinViewModel

internal const val MIN_SPIKE_HEIGHT = 1F
internal const val DEFAULT_GRAPHICS_LAYER_ALPHA = 0.99F

internal const val CONTROLLER_RECT_WIDTH = 15F
internal const val CONTROLLER_RECT_OFFSET = 7F

internal const val CONTROLLER_CIRCLE_RADIUS = 25F
internal const val CONTROLLER_CIRCLE_CENTER = 16F
internal const val CONTROLLER_HEIGHT_OFFSET = CONTROLLER_CIRCLE_RADIUS + CONTROLLER_CIRCLE_CENTER

internal const val CONTROLLER_ARROW_CORNER_BACK_OFFSET = 8F
internal const val CONTROLLER_ARROW_CORNER_FRONT_OFFSET = 10F
internal const val CONTROLLER_ARROW_CORNER_OFFSET = 12F

internal const val PLAYBACK_RECT_WIDTH = 5F
internal const val PLAYBACK_RECT_OFFSET = 2F

internal const val PLAYBACK_CIRCLE_RADIUS = 12F
internal const val PLAYBACK_CIRCLE_CENTER = 8F

internal const val WAVEFORM_SPIKE_WIDTH_RATIO = 5
internal const val WAVEFORM_SCROLL_SPEED_UP = 200

internal const val WAVEFORM_PADDING =
    (CONTROLLER_CIRCLE_RADIUS +
            CONTROLLER_CIRCLE_CENTER / 2 +
            CONTROLLER_RECT_WIDTH).toInt()

@Composable
fun TrimmerScreen(
    trackPath: String,
    modifier: Modifier = Modifier,
    viewModel: TrimmerViewModel = koinViewModel<TrimmerViewModelImpl>(),
) {
    val state by viewModel.stateFlow.collectLatestAsState()
    val onUiIntent = viewModel::onUiIntent

    LifecycleEffect(onUiIntent = onUiIntent)
    LoadTrackEffect(trackPath = trackPath, onUiIntent = onUiIntent)

    Box(modifier) {
        val contentModifier = Modifier.fillMaxSize()

        when (state.trackState) {
            is UiState.Initial, is UiState.Loading ->
                AppProgressIndicator(contentModifier)

            is UiState.Error ->
                AppLoadingBoxError(contentModifier)

            is UiState.Data, is UiState.Refreshing ->
                state.trackState.getOrNull()?.let {
                    TrimmerScreenImpl(
                        state = state,
                        onUiIntent = onUiIntent,
                        modifier = contentModifier,
                    )
                }

            is UiState.Success -> doNothing
        }
    }
}
