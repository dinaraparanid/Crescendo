package com.paranid5.mediastreamer.presentation.tracks

import com.paranid5.mediastreamer.data.tracks.Track
import com.paranid5.mediastreamer.presentation.BasePresenter
import kotlinx.coroutines.flow.MutableStateFlow

class TracksPresenter(tracks: List<Track>) : BasePresenter {
    val tracksState = MutableStateFlow(tracks)
}