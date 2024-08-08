package com.paranid5.crescendo.core.common

enum class AudioStatus {
    STREAMING, PLAYING;

    inline fun <R> fold(ifStream: () -> R, ifTrack: () -> R) =
        when (this) {
            STREAMING -> ifStream()
            PLAYING -> ifTrack()
        }
}