package com.paranid5.crescendo.presentation.main.trimmer

import com.paranid5.crescendo.domain.caching.Formats
import com.paranid5.crescendo.domain.media.files.MediaFile
import com.paranid5.crescendo.domain.media.files.trimmed
import com.paranid5.crescendo.domain.trimming.FadeDurations
import com.paranid5.crescendo.domain.trimming.TrimRange
import com.paranid5.crescendo.presentation.UIHandler
import java.io.File

class TrimmerUIHandler : UIHandler {
    suspend fun trimTrack(
        trackPath: String,
        outputFilename: String,
        audioFormat: Formats,
        trimRange: TrimRange,
        fadeDurations: FadeDurations
    ) = MediaFile.AudioFile(File(trackPath))
        .trimmed(outputFilename, audioFormat, trimRange, fadeDurations)
}