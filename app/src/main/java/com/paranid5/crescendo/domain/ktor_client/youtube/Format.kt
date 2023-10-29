package com.paranid5.crescendo.domain.ktor_client.youtube

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Format(
    @JvmField val itag: Int,
    @JvmField val ext: String,
    @JvmField val isDashContainer: Boolean,
    @JvmField val height: Int = -1,
    @JvmField val fps: Int = 30,
    @JvmField val audioBitrate: Int = -1,
    @JvmField val isHlsContent: Boolean = false,
    @JvmField var videoCodec: VCodec? = null,
    @JvmField var audioCodec: ACodec? = null
) : Parcelable {
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