package com.paranid5.crescendo.playing.view_model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface PlayingBackResult : Parcelable {

    @Parcelize
    data object ShowAudioEffects : PlayingBackResult

    @Parcelize
    data class ShowTrimmer(val trackUri: String) : PlayingBackResult
}
