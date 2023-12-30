package com.paranid5.crescendo.presentation.main.fetch_stream.properties

import com.paranid5.crescendo.presentation.main.fetch_stream.FetchStreamViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

private val youtubeUrlRegex = Regex(
    "https://((www\\.youtube\\.com/((watch\\?v=)|(live/)))|(youtu\\.be/))\\S{11}(\\?si=\\S{16})?(\\?feature=\\S+)?(&t=\\d+s)?"
)

inline val FetchStreamViewModel.currentTextFlow
    get() = combine(
        savedUrlState,
        currentUrlFlow,
    ) { saved, current ->
        saved ?: current
    }

val FetchStreamViewModel.isConfirmButtonActiveFlow
    get() = currentTextFlow.map { it matches youtubeUrlRegex }