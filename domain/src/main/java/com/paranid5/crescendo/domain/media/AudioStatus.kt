package com.paranid5.crescendo.domain.media

enum class AudioStatus {
    STREAMING, PLAYING;

    inline fun handle(streamAction: () -> Unit, trackAction: () -> Unit) =
        when (this) {
            STREAMING -> streamAction()
            PLAYING -> trackAction()
        }
}