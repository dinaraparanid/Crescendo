package com.paranid5.crescendo.presentation.playing

import com.paranid5.crescendo.presentation.BasePresenter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayingPresenter(
    isPlayingState: MutableStateFlow<Boolean>,
    amplitudes: List<Int>
) : BasePresenter {
    val isPlayingState = isPlayingState.asStateFlow()
    val amplitudesState = MutableStateFlow(amplitudes)
}