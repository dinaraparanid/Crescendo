package com.paranid5.crescendo.presentation.main.trimmer.properties.compose

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.core.impl.trimming.ShownEffects
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.states.shownEffectsFlow
import com.paranid5.crescendo.utils.extensions.collectLatestAsState

@Composable
fun TrimmerViewModel.collectShownEffectsAsState(initial: ShownEffects = ShownEffects.NONE) =
    shownEffectsFlow.collectLatestAsState(initial)