package com.paranid5.crescendo.core.media.files

object FFmpeg {
    init {
        System.loadLibrary("avcodec")
        System.loadLibrary("avdevice")
        System.loadLibrary("avfilter")
        System.loadLibrary("avformat")
        System.loadLibrary("avutil")
        System.loadLibrary("swresample")
        System.loadLibrary("swscale")
    }

    private external fun executeImpl(rawCommand: String): Int

    fun execute(command: String): Int = executeImpl("ffmpeg $command")
}