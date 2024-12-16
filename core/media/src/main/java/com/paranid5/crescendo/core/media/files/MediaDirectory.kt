package com.paranid5.crescendo.core.media.files

import android.os.Build
import android.os.Environment

@Deprecated("Will be removed")
@JvmInline
value class MediaDirectory(val value: String) : CharSequence {
    override val length
        get() = value.length

    override fun get(index: Int) = value[index]

    override fun subSequence(startIndex: Int, endIndex: Int) =
        value.subSequence(startIndex, endIndex)
}

/** @return absolute path of the media directory */

@Deprecated("Will be removed")
fun getFullMediaDirectory(mediaDirectory: String) = MediaDirectory(
    Environment
        .getExternalStoragePublicDirectory(mediaDirectory)
        .absolutePath
)

/** @return absolute path of the media directory */

@Deprecated("Will be removed")
fun getFullMediaDirectory(mediaDirectory: MediaDirectory) =
    getFullMediaDirectory(mediaDirectory.value)

/**
 * For [Build.VERSION_CODES.Q]+ it is [Environment.DIRECTORY_MOVIES],
 * for older models it is either [Environment.DIRECTORY_MUSIC] or [Environment.DIRECTORY_MOVIES]
 */

@Deprecated("Will be removed")
fun getInitialVideoDirectory(isAudio: Boolean) = MediaDirectory(
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> Environment.DIRECTORY_MOVIES
        isAudio -> Environment.DIRECTORY_MUSIC
        else -> Environment.DIRECTORY_MOVIES
    }
)