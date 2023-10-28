package com.paranid5.crescendo.domain.ktor_client.youtube

data class Format(
    val itag: Int,
    val ext: String,
    val isDashContainer: Boolean,
    val height: Int = -1,
    val fps: Int = 30,
    val audioBitrate: Int = -1,
    val isHlsContent: Boolean = false,
    var videoCodec: VCodec? = null,
    var audioCodec: ACodec? = null
) {
    enum class VCodec {
        H263,
        H264,
        MPEG4,
        VP8,
        VP9,
        NONE
    }

    enum class ACodec {
        MP3,
        AAC,
        VORBIS,
        OPUS,
        NONE
    }
}