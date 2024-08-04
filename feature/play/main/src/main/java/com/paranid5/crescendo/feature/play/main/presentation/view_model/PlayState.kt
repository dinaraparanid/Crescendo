package com.paranid5.crescendo.feature.play.main.presentation.view_model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class PlayState(
    val searchQuery: String = "",
    val pagerState: PagerState = PagerState.TRACKS,
) : Parcelable {
    enum class PagerState { TRACKS, ARTISTS, ALBUMS }

    val isSearchActive = searchQuery.isNotEmpty()
}
