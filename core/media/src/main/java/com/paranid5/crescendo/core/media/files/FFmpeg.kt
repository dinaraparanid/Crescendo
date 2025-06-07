package com.paranid5.crescendo.core.media.files

import com.arthenica.ffmpegkit.FFmpegKit

object FFmpeg {
    fun execute(command: String) = FFmpegKit.execute(command).returnCode.value
}
