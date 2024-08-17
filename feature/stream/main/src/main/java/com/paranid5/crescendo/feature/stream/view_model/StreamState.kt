package com.paranid5.crescendo.feature.stream.view_model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class StreamState(
    val pagerState: PagerState = PagerState.FETCH,
) : Parcelable {
    enum class PagerState { FETCH, RECENT }
}
