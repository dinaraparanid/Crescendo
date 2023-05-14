package com.paranid5.mediastreamer.domain.utils.media

import android.os.Build
import android.os.Environment
import android.util.Log
import java.io.File

private const val TAG = "Files"

@JvmInline
value class MediaDirectory(val value: String) : CharSequence {
    override val length
        get() = value.length

    override fun get(index: Int) = value[index]

    override fun subSequence(startIndex: Int, endIndex: Int) =
        value.subSequence(startIndex, endIndex)
}

/** @return absolute path of the media directory */

fun getFullMediaDirectory(mediaDirectory: MediaDirectory) = MediaDirectory(
    Environment
        .getExternalStoragePublicDirectory(mediaDirectory.value)
        .absolutePath
)

/**
 * For [Build.VERSION_CODES.Q]+ it is [Environment.DIRECTORY_MOVIES],
 * for older models it is either [Environment.DIRECTORY_MUSIC] or [Environment.DIRECTORY_MOVIES]
 */

fun getInitialMediaDirectory(isAudio: Boolean) = MediaDirectory(
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> Environment.DIRECTORY_MOVIES
        isAudio -> Environment.DIRECTORY_MUSIC
        else -> Environment.DIRECTORY_MOVIES
    }
)

private fun createMediaFile(mediaDirectory: MediaDirectory, filename: String, ext: String) =
    "${getFullMediaDirectory(mediaDirectory)}/$filename.$ext"
        .takeIf { !File(it).exists() }
        ?.let(::File)
        ?.also { Log.d(TAG, "Creating file ${it.absolutePath}") }
        ?.also(File::createNewFile)
        ?.let(MediaFile::VideoFile)
        ?: generateSequence(1) { it + 1 }
            .map { num -> "${getFullMediaDirectory(mediaDirectory)}/${filename}($num).$ext" }
            .map(::File)
            .first { !it.exists() }
            .also { Log.d(TAG, "Creating file ${it.absolutePath}") }
            .also(File::createNewFile)
            .let(MediaFile::VideoFile)

/**
 * Creates new media file by given parameters.
 * If file with the same filename and extension already exists,
 * will try to create `[filename](try_number).[ext]` until such file not found
 *
 * @param mediaDirectory directory to put file
 * (either [Environment.DIRECTORY_MUSIC] for audio or [Environment.DIRECTORY_MOVIES] for video)
 * @param filename desired filename
 * @param ext file extension
 * @return created empty media file
 */

fun createMediaFileCatching(mediaDirectory: MediaDirectory, filename: String, ext: String) =
    runCatching { createMediaFile(mediaDirectory, filename, ext) }