package com.paranid5.crescendo.core.media.files

object FFmpeg {
    init {
        System.loadLibrary("mp3lame")
        System.loadLibrary("avcodec")
        System.loadLibrary("avdevice")
        System.loadLibrary("avfilter")
        System.loadLibrary("avformat")
        System.loadLibrary("avutil")
        System.loadLibrary("swresample")
        System.loadLibrary("swscale")
        System.loadLibrary("native-lib")
    }

    private external fun execute(args: Array<String>): Int

    fun execute(command: String): Int = execute(
        arrayOf("ffmpeg", *command.split(' ').toTypedArray())
    )
}