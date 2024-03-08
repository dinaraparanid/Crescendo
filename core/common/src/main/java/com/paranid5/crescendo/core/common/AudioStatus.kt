package com.paranid5.crescendo.core.common

enum class AudioStatus {
    STREAMING, PLAYING;

    fun handle(streamAction: () -> Unit, trackAction: () -> Unit) =
        when (this) {
            STREAMING -> streamAction()
            PLAYING -> trackAction()
        }
}