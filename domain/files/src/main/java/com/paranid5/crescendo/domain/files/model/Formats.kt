package com.paranid5.crescendo.domain.files.model

enum class Formats { MP3, WAV, AAC, MP4 }

val Formats.fileExtension
    get() = MediaFileExtension(
        when (this) {
            Formats.MP3 -> "mp3"
            Formats.AAC -> "aac"
            Formats.WAV -> "wav"
            Formats.MP4 -> "mp4"
        }
    )

val Formats.mimeType
    get() = MimeType(
        when (this) {
            Formats.MP3 -> "audio/mpeg"
            Formats.AAC -> "audio/aac"
            Formats.WAV -> "audio/x-wav"
            Formats.MP4 -> "video/mp4"
        }
    )