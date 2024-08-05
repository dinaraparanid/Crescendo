package com.paranid5.crescendo.feature.play.main.presentation.view_model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class PlayState(
    val searchQuery: String = "",
    val pagerState: PagerState = PagerState.TRACKS,
    val backResult: PlayBackResult? = null,
) : Parcelable {
    enum class PagerState { TRACKS, ARTISTS, ALBUMS }

    @IgnoredOnParcel
    val isSearchActive = searchQuery.isNotEmpty()
}
