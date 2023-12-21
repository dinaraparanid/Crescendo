package com.paranid5.crescendo.presentation.main.trimmer.properties

import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel

inline val TrimmerViewModel.amplitudesState
    get() = amplitudesStateHolder.amplitudesState

fun TrimmerViewModel.setAmplitudesAsync(amplitudes: List<Int>) =
    amplitudesStateHolder.setAmplitudesAsync(amplitudes)

suspend inline fun TrimmerViewModel.setAmplitudes(amplitudes: List<Int>) =
    amplitudesStateHolder.setAmplitudesAsync(amplitudes).join()