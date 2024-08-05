package com.paranid5.crescendo.tracks.view_model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed interface TracksBackResult : Parcelable {

    @Parcelize
    data class ShowTrimmer(val trackUri: String) : TracksBackResult
}
