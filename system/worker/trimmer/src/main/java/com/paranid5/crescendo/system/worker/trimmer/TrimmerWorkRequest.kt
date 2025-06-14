package com.paranid5.crescendo.system.worker.trimmer

import com.paranid5.crescendo.core.common.tracks.DefaultTrack
import com.paranid5.crescendo.core.common.trimming.FadeDurations
import com.paranid5.crescendo.core.common.trimming.PitchAndSpeed
import com.paranid5.crescendo.core.common.trimming.TrimRange
import com.paranid5.crescendo.domain.files.entity.Filename
import com.paranid5.crescendo.domain.files.entity.Formats
import kotlinx.serialization.Serializable

@Serializable
data class TrimmerWorkRequest(
    val track: DefaultTrack,
    val outputFilename: Filename,
    val audioFormat: Formats,
    val trimRange: TrimRange,
    val pitchAndSpeed: PitchAndSpeed,
    val fadeDurations: FadeDurations,
)
