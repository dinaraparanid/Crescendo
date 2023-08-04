package com.paranid5.crescendo.domain.utils.media

import android.os.Build
import android.os.Environment
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
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

fun getInitialMediaDirectory(isAudio: Boolean) = MediaDirectory(
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> Environment.DIRECTORY_MOVIES
        isAudio -> Environment.DIRECTORY_MUSIC
        else -> Environment.DIRECTORY_MOVIES
    }
)

internal suspend inline fun generateMediaFile(
    fullPath: String,
    filename: String,
    ext: String
) = withContext(Dispatchers.IO) {
    "$fullPath/$filename.$ext"
        .takeIf { !File(it).exists() }
        ?.let(::File)
        ?.also { Log.d(TAG, "New file ${it.absolutePath}") }
        ?.let(MediaFile::VideoFile)
        ?: generateSequence(1) { it + 1 }
            .map { num -> "$fullPath/$filename($num).$ext" }
            .map(::File)
            .first { !it.exists() }
            .also { Log.d(TAG, "New file ${it.absolutePath}") }
            .let(MediaFile::VideoFile)
}

internal suspend inline fun generateMediaFile(
    mediaDirectory: MediaDirectory,
    filename: String,
    ext: String
) = generateMediaFile(
    fullPath = getFullMediaDirectory(mediaDirectory).value,
    filename = filename,
    ext = ext
)

private suspend inline fun createMediaFile(
    fullPath: String,
    filename: String,
    ext: String
) = generateMediaFile(fullPath, filename, ext).also(File::createNewFile)

private suspend inline fun createMediaFile(
    mediaDirectory: MediaDirectory,
    filename: String,
    ext: String
) = generateMediaFile(mediaDirectory, filename, ext).also(File::createNewFile)

/**
 * Creates new media file by given parameters.
 * If file with the same filename and extension already exists,
 * will try to create `[filename](try_number).[ext]` until such file not found
 *
 * @param fullPath absolute path to put file
 * @param filename desired filename
 * @param ext file extension
 * @return created empty media file
 */

internal suspend inline fun createMediaFileCatching(
    fullPath: String,
    filename: String,
    ext: String
) = coroutineScope {
    runCatching { createMediaFile(fullPath, filename, ext) }
}

/**
 * Creates new media file by given parameters.
 * If file with the same filename and extension already exists,
 * will try to create `[filename](try_number).[ext]` until such file not found
 *
 * @param mediaDirectory directory to put file
 * (either [Environment.DIRECTORY_MUSIC] for audio or
 * [Environment.DIRECTORY_MOVIES] and [Environment.DIRECTORY_DCIM] for video)
 * @param filename desired filename
 * @param ext file extension
 * @return created empty media file
 */

internal suspend inline fun createMediaFileCatching(
    mediaDirectory: MediaDirectory,
    filename: String,
    ext: String
) = coroutineScope {
    runCatching { createMediaFile(mediaDirectory, filename, ext) }
}