package com.paranid5.crescendo.domain.media.files

import android.os.Build
import android.os.Environment

@JvmInline
value class MediaDirectory(val value: String) : CharSequence {
    override val length
        get() = value.length

    override fun get(index: Int) = value[index]

    override fun subSequence(startIndex: Int, endIndex: Int) =
        value.subSequence(startIndex, endIndex)
}

/** @return absolute path of the media directory */

fun getFullMediaDirectory(mediaDirectory: String) = MediaDirectory(
    Environment
        .getExternalStoragePublicDirectory(mediaDirectory)
        .absolutePath
)

/** @return absolute path of the media directory */

fun getFullMediaDirectory(mediaDirectory: MediaDirectory) =
    getFullMediaDirectory(mediaDirectory.value)

/**
 * For [Build.VERSION_CODES.Q]+ it is [Environment.DIRECTORY_MOVIES],
 * for older models it is either [Environment.DIRECTORY_MUSIC] or [Environment.DIRECTORY_MOVIES]
 */

fun getInitialVideoDirectory(isAudio: Boolean) = MediaDirectory(
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> Environment.DIRECTORY_MOVIES
        isAudio -> Environment.DIRECTORY_MUSIC
        else -> Environment.DIRECTORY_MOVIES
    }
)