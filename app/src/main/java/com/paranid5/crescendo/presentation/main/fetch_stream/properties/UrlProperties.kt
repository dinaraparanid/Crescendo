package com.paranid5.crescendo.presentation.main.fetch_stream.properties

import com.paranid5.crescendo.presentation.main.fetch_stream.FetchStreamViewModel
import com.paranid5.crescendo.presentation.main.fetch_stream.states.UrlStateHolder
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

private val youtubeUrlRegex = Regex(
    "https://((www\\.youtube\\.com/((watch\\?v=)|(live/)))|(youtu\\.be/))\\S{11}(\\?si=\\S{16})?(\\?feature=\\S+)?(&t=\\d+s)?"
)

inline val UrlStateHolder.currentTextFlow
    get() = combine(
        currentTextStateByVM,
        currentTextFlowByStorage
    ) { vm, storage ->
        vm ?: storage
    }

inline val FetchStreamViewModel.currentTextFlow
    get() = urlStateHolder.currentTextFlow

fun FetchStreamViewModel.setCurrentText(currentText: String) =
    urlStateHolder.setCurrentText(currentText)

val FetchStreamViewModel.isConfirmButtonActiveFlow
    get() = currentTextFlow.map { it matches youtubeUrlRegex }