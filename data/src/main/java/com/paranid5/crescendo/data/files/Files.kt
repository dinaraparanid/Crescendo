package com.paranid5.crescendo.data.files

import android.os.Environment
import arrow.core.Either
import com.paranid5.crescendo.core.common.uri.Path
import com.paranid5.crescendo.data.files.MediaDirectoryPath.getFullMediaDirectoryPath
import com.paranid5.crescendo.domain.files.model.Filename
import com.paranid5.crescendo.domain.files.model.MediaDirectory
import com.paranid5.crescendo.domain.files.model.MediaFileExtension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

internal object Files {
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
        fullPath: Path,
        filename: Filename,
        ext: MediaFileExtension,
    ) = Either.catch { createFile(fullPath, filename, ext) }

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
        filename: Filename,
        ext: MediaFileExtension,
    ) = Either.catch { createFile(mediaDirectory, filename, ext) }

    private suspend inline fun generateFile(
        fullPath: Path,
        filename: Filename,
        ext: MediaFileExtension,
    ) = withContext(Dispatchers.IO) {
        tryGenerateFile(fullPath, filename, ext)
            ?: generateRepeatedFile(fullPath, filename, ext)
    }

    private fun tryGenerateFile(
        fullPath: Path,
        filename: Filename,
        ext: MediaFileExtension,
    ) = "$fullPath/$filename.$ext"
        .takeIf { File(it).exists().not() }
        ?.let(::File)

    private fun generateRepeatedFile(
        fullPath: Path,
        filename: Filename,
        ext: MediaFileExtension,
    ) = generateSequence(1) { it + 1 }
        .map { num -> "$fullPath/$filename($num).$ext" }
        .map(::File)
        .first { it.exists().not() }

    private suspend inline fun generateFile(
        mediaDirectory: MediaDirectory,
        filename: Filename,
        ext: MediaFileExtension,
    ) = generateFile(
        fullPath = getFullMediaDirectoryPath(mediaDirectory),
        filename = filename,
        ext = ext,
    )

    private suspend inline fun createFile(
        fullPath: Path,
        filename: Filename,
        ext: MediaFileExtension,
    ) = generateFile(
        fullPath = fullPath,
        filename = filename,
        ext = ext,
    ).also(File::createNewFile)

    private suspend inline fun createFile(
        mediaDirectory: MediaDirectory,
        filename: Filename,
        ext: MediaFileExtension,
    ) = generateFile(
        mediaDirectory = mediaDirectory,
        filename = filename,
        ext = ext,
    ).also(File::createNewFile)
}
