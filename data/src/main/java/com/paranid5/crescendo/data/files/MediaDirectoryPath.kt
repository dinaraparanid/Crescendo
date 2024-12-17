package com.paranid5.crescendo.data.files

import android.os.Build
import android.os.Environment
import com.paranid5.crescendo.core.common.uri.Path
import com.paranid5.crescendo.domain.files.model.MediaDirectory

internal object MediaDirectoryPath {
    fun getFullMediaDirectoryPath(mediaDirectory: String): Path =
        Path(Environment.getExternalStoragePublicDirectory(mediaDirectory).absolutePath)

    fun getFullMediaDirectoryPath(mediaDirectory: MediaDirectory): Path =
        getFullMediaDirectoryPath(mediaDirectory.toString())

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
}