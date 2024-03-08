package com.paranid5.crescendo.core.common.caching

import com.paranid5.crescendo.core.common.trimming.TrimRange

data class VideoCacheData(
    val url: String,
    val desiredFilename: String,
    val format: Formats,
    val trimRange: TrimRange,
    private val id: Long = System.currentTimeMillis()
) {
    companion object {
        val NIL = VideoCacheData("", "", Formats.MP3, TrimRange())
    }
}