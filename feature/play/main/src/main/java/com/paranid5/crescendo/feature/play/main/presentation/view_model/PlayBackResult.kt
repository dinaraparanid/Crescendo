package com.paranid5.crescendo.feature.play.main.presentation.view_model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface PlayBackResult : Parcelable {

    @Parcelize
    data class ShowTrimmer(val trackUri: String) : PlayBackResult
}
