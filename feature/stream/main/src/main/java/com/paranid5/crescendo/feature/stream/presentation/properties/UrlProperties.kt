package com.paranid5.crescendo.feature.stream.presentation.properties

//private val youtubeUrlRegex = Regex(
//    "https://((www\\.youtube\\.com/((watch\\?v=)|(live/)))|(youtu\\.be/))\\S{11}(\\?si=\\S{16})?(\\?feature=\\S+)?(&t=\\d+s)?"
//)
//
//internal inline val StreamViewModelImpl.currentTextFlow
//    get() = combine(
//        savedUrlState,
//        playingUrlFlow,
//    ) { saved, current ->
//        saved ?: current
//    }
//
//internal val StreamViewModelImpl.isConfirmButtonActiveFlow
//    get() = currentTextFlow.map { it matches youtubeUrlRegex }