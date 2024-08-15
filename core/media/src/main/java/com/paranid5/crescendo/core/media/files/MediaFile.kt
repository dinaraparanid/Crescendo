package com.paranid5.crescendo.core.media.files

import java.io.File

sealed class MediaFile(value: File) : File(value.absolutePath) {
    companion object {
        private const val serialVersionUID: Long = -4175671868438928438L
    }

    class VideoFile(value: File) : MediaFile(value) {
        companion object {
            private const val serialVersionUID: Long = 6914322109341150479L
        }
    }

    class AudioFile(value: File) : MediaFile(value) {
        companion object {
            private const val serialVersionUID: Long = 2578824723183659601L
        }
    }
}
