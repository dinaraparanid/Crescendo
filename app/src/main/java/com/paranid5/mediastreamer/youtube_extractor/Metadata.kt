package com.paranid5.mediastreamer.youtube_extractor

data class Metadata(
    val identifierTag: Int,
    val fileExt: String?,
    val audioBitrate: Int,
    val isDashContainer: Boolean,
    val audioCodec: ACodec? = null,
    val isHlsContent: Boolean = false
) {
    enum class ACodec {
        MP3, AAC, VORBIS, OPUS, NONE
    }
}