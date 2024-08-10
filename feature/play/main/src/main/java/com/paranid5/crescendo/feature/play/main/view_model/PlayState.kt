package com.paranid5.crescendo.feature.play.main.view_model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class PlayState(
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val pagerState: PagerState = PagerState.TRACKS,
    @IgnoredOnParcel val backResult: PlayBackResult? = null,
) : Parcelable {
    enum class PagerState { TRACKS, ARTISTS, ALBUMS }
}
