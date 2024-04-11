package com.paranid5.crescendo.trimmer.presentation.properties.compose

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel
import com.paranid5.crescendo.trimmer.domain.entities.ShownEffects
import com.paranid5.crescendo.trimmer.data.shownEffectsFlow
import com.paranid5.crescendo.utils.extensions.collectLatestAsState

@Composable
internal fun TrimmerViewModel.collectShownEffectsAsState(initial: ShownEffects = ShownEffects.NONE) =
    shownEffectsFlow.collectLatestAsState(initial)