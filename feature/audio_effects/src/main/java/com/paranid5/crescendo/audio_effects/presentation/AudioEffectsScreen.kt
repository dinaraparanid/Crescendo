package com.paranid5.crescendo.audio_effects.presentation

import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.paranid5.crescendo.audio_effects.presentation.effects.LifecycleEffect
import com.paranid5.crescendo.audio_effects.presentation.ui.BassAndReverb
import com.paranid5.crescendo.audio_effects.presentation.ui.Equalizer
import com.paranid5.crescendo.audio_effects.presentation.ui.PitchAndSpeed
import com.paranid5.crescendo.audio_effects.presentation.ui.TopBar
import com.paranid5.crescendo.audio_effects.view_model.AudioEffectsState
import com.paranid5.crescendo.audio_effects.view_model.AudioEffectsUiIntent
import com.paranid5.crescendo.audio_effects.view_model.AudioEffectsViewModel
import com.paranid5.crescendo.audio_effects.view_model.AudioEffectsViewModelImpl
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.ui.foundation.AppProgressIndicator
import com.paranid5.crescendo.ui.foundation.UiState
import com.paranid5.crescendo.utils.doNothing
import com.paranid5.crescendo.utils.extensions.collectLatestAsState
import org.koin.androidx.compose.koinViewModel

@Composable
fun AudioEffectsScreen(
    modifier: Modifier = Modifier,
    viewModel: AudioEffectsViewModel = koinViewModel<AudioEffectsViewModelImpl>(),
) {
    val state by viewModel.stateFlow.collectLatestAsState()
    val onUiIntent = viewModel::onUiIntent

    LifecycleEffect(onUiIntent = onUiIntent)

    @Composable
    fun impl() = when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> AudioEffectsScreenLandscape(
            state = state,
            onUiIntent = onUiIntent,
            modifier = modifier,
        )

        else -> AudioEffectsScreenPortrait(
            state = state,
            onUiIntent = onUiIntent,
            modifier = modifier,
        )
    }

    when (state.uiState) {
        is UiState.Data, is UiState.Success, is UiState.Refreshing -> impl()
        is UiState.Initial, is UiState.Loading -> AppProgressIndicator(modifier = modifier)
        is UiState.Error -> doNothing
    }
}

@Composable
private fun AudioEffectsScreenPortrait(
    state: AudioEffectsState,
    onUiIntent: (AudioEffectsUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = Column(modifier) {
    TopBar(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.padding.extraMedium),
    )

    Spacer(Modifier.height(dimensions.padding.medium))

    PitchAndSpeed(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier
            .fillMaxWidth()
            .weight(1F)
            .padding(horizontal = dimensions.padding.small),
    )

    Spacer(Modifier.height(dimensions.padding.small))

    Equalizer(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier
            .fillMaxWidth()
            .weight(3F),
    )

    BassAndReverbWithSpacerPortraitCompat(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun BassAndReverbWithSpacerPortraitCompat(
    state: AudioEffectsState,
    onUiIntent: (AudioEffectsUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = when (Build.VERSION.SDK_INT) {
    Build.VERSION_CODES.Q -> // Not available for Android 10
        Spacer(Modifier.height(dimensions.padding.enormous))

    else -> {
        Spacer(Modifier.height(dimensions.padding.medium))

        BassAndReverb(
            state = state,
            onUiIntent = onUiIntent,
            modifier = modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(dimensions.padding.medium))
    }
}

@Composable
private fun AudioEffectsScreenLandscape(
    state: AudioEffectsState,
    onUiIntent: (AudioEffectsUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = Column(modifier) {
    TopBar(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(Modifier.height(dimensions.padding.medium))

    AudioEffectsScreenLandscapeContent(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = dimensions.padding.medium),
    )
}

@Composable
private fun AudioEffectsScreenLandscapeContent(
    state: AudioEffectsState,
    onUiIntent: (AudioEffectsUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = Row(modifier) {
    Equalizer(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier.weight(1F),
    )

    Spacer(Modifier.width(dimensions.padding.medium))

    SubEffectsLandscape(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier.weight(1F),
    )
}

@Composable
private fun SubEffectsLandscape(
    state: AudioEffectsState,
    onUiIntent: (AudioEffectsUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = Column(modifier) {
    PitchAndSpeed(
        state = state,
        onUiIntent = onUiIntent,
        Modifier
            .fillMaxWidth()
            .weight(1F)
    )

    BassAndReverbLandscapeCompat(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier
            .fillMaxWidth()
            .weight(1F)
            .padding(end = dimensions.padding.enormous),
    )
}

@Composable
private fun BassAndReverbLandscapeCompat(
    state: AudioEffectsState,
    onUiIntent: (AudioEffectsUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (Build.VERSION.SDK_INT != Build.VERSION_CODES.Q) {
        Spacer(Modifier.height(dimensions.padding.medium))

        BassAndReverb(
            state = state,
            onUiIntent = onUiIntent,
            modifier = modifier.fillMaxWidth(),
        )
    }
}
