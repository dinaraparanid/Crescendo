package com.paranid5.crescendo.core.media.files

import com.arthenica.ffmpegkit.FFmpegKit

object FFmpeg {
    fun execute(command: String): Int = FFmpegKit.execute(command).returnCode.value
}
