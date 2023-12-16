package com.paranid5.crescendo.domain.media

enum class AudioStatus { STREAMING, PLAYING }

inline fun AudioStatus?.handle(streamAction: () -> Unit, trackAction: () -> Unit) =
    when (this) {
        AudioStatus.STREAMING -> streamAction()
        AudioStatus.PLAYING -> trackAction()
        else -> null
    }

inline fun AudioStatus?.handleOrIgnore(streamAction: () -> Unit, trackAction: () -> Unit) =
    handle(streamAction, trackAction) ?: Unit

inline fun AudioStatus?.handleOrThrow(streamAction: () -> Unit, trackAction: () -> Unit) =
    handle(streamAction, trackAction) ?: throw AudioStatusNotInitializedException()

class AudioStatusNotInitializedException :
    NullPointerException("Audio status was not initialized") {
    companion object {
        private const val serialVersionUID = 2807526787062406209L
    }
}