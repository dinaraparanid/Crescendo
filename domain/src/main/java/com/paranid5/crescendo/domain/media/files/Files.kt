package com.paranid5.crescendo.domain.media.files

import android.os.Environment
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.io.File

private const val TAG = "Files"

private suspend inline fun generateFile(
    fullPath: String,
    filename: String,
    ext: String
) = withContext(Dispatchers.IO) {
    tryGenerateFile(fullPath, filename, ext)
        ?: generateRepeatedFile(fullPath, filename, ext)
}

private fun tryGenerateFile(
    fullPath: String,
    filename: String,
    ext: String
) = "$fullPath/$filename.$ext"
    .takeIf { !File(it).exists() }
    ?.let(::File)
    ?.also { Log.d(TAG, "New file ${it.absolutePath}") }

private fun generateRepeatedFile(
    fullPath: String,
    filename: String,
    ext: String
) = generateSequence(1) { it + 1 }
    .map { num -> "$fullPath/$filename($num).$ext" }
    .map(::File)
    .first { !it.exists() }
    .also { Log.d(TAG, "New file ${it.absolutePath}") }

private suspend inline fun generateFile(
    mediaDirectory: MediaDirectory,
    filename: String,
    ext: String
) = generateFile(
    fullPath = getFullMediaDirectory(mediaDirectory).value,
    filename = filename,
    ext = ext
)

private suspend inline fun createFile(
    fullPath: String,
    filename: String,
    ext: String
) = generateFile(fullPath, filename, ext).also(File::createNewFile)

private suspend inline fun createFile(
    mediaDirectory: MediaDirectory,
    filename: String,
    ext: String
) = generateFile(mediaDirectory, filename, ext).also(File::createNewFile)

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

suspend fun createFileCatching(
    fullPath: String,
    filename: String,
    ext: String
) = coroutineScope {
    runCatching { createFile(fullPath, filename, ext) }
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

suspend fun createFileCatching(
    mediaDirectory: MediaDirectory,
    filename: String,
    ext: String
) = coroutineScope {
    runCatching { createFile(mediaDirectory, filename, ext) }
}