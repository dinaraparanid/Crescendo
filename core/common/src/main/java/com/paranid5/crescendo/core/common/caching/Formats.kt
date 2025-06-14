package com.paranid5.crescendo.core.common.caching

import com.paranid5.crescendo.core.common.media.MediaFileExtension
import com.paranid5.crescendo.core.common.media.MimeType

@Deprecated("Will be removed")
enum class Formats { MP3, WAV, AAC, MP4 }

@Deprecated("Will be removed")
val Formats.fileExtension
    get() = MediaFileExtension(
        when (this) {
            Formats.MP3 -> "mp3"
            Formats.AAC -> "aac"
            Formats.WAV -> "wav"
            Formats.MP4 -> "mp4"
        }
    )

@Deprecated("Will be removed")
val Formats.mimeType
    get() = MimeType(
        when (this) {
            Formats.MP3 -> "audio/mpeg"
            Formats.AAC -> "audio/aac"
            Formats.WAV -> "audio/x-wav"
            Formats.MP4 -> "video/mp4"
        }
    )
