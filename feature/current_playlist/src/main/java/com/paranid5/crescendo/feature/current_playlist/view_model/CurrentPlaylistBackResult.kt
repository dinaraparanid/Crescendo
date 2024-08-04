package com.paranid5.crescendo.feature.current_playlist.view_model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface CurrentPlaylistBackResult : Parcelable {

    @Parcelize
    data class ShowTrimmer(val trackUri: String) : CurrentPlaylistBackResult
}
