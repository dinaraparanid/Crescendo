package com.paranid5.crescendo.domain.sources.tracks

import com.paranid5.crescendo.core.common.tracks.Track
import kotlinx.coroutines.flow.Flow

interface CurrentTrackSubscriber {
    val currentTrackFlow: Flow<Track?>
}