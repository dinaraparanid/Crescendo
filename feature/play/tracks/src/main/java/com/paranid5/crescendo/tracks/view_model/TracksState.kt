package com.paranid5.crescendo.tracks.view_model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.paranid5.crescendo.core.common.AppConstants.NoItems
import com.paranid5.crescendo.core.common.tracks.TrackOrder
import com.paranid5.crescendo.ui.foundation.UiState
import com.paranid5.crescendo.ui.foundation.fold
import com.paranid5.crescendo.ui.foundation.map
import com.paranid5.crescendo.ui.track.ui_state.TrackUiState
import com.paranid5.crescendo.utils.extensions.filterToImmutableList
import com.paranid5.crescendo.utils.extensions.matches
import kotlinx.collections.immutable.ImmutableList
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class TracksState(
    val query: String = "",
    val allTracksState: UiState<ImmutableList<TrackUiState>> = UiState.Undefined,
    val trackOrder: TrackOrder = TrackOrder.default,
    val currentTrack: TrackUiState? = null,
    val backResult: TracksBackResult? = null,
) : Parcelable {

    @IgnoredOnParcel
    val shownTracksState = allTracksState.map { list ->
        list.filterToImmutableList { it matches query }
    }

    @IgnoredOnParcel
    val shownTracksNumber = shownTracksState.fold(
        ifPresent = { it.size },
        ifEmpty = { NoItems },
    )
}
