package com.paranid5.crescendo.system.worker.trimmer

import com.paranid5.crescendo.core.common.caching.Formats
import com.paranid5.crescendo.core.common.tracks.DefaultTrack
import com.paranid5.crescendo.core.common.trimming.FadeDurations
import com.paranid5.crescendo.core.common.trimming.PitchAndSpeed
import com.paranid5.crescendo.core.common.trimming.TrimRange
import kotlinx.serialization.Serializable

@Serializable
data class TrimmerWorkRequest(
    val track: DefaultTrack,
    val outputFilename: String,
    val audioFormat: Formats,
    val trimRange: TrimRange,
    val pitchAndSpeed: PitchAndSpeed,
    val fadeDurations: FadeDurations,
)