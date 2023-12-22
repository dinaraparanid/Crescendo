package com.paranid5.crescendo.domain.media.files

import android.os.Environment
import com.arthenica.mobileffmpeg.FFmpeg
import com.paranid5.crescendo.domain.caching.CacheTrimRange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

suspend fun createAudioFileCatching(
    mediaDirectory: MediaDirectory,
    filename: String,
    ext: String
) = createFileCatching(mediaDirectory, filename, ext)
    .map { MediaFile.AudioFile(it) }

suspend fun MediaFile.AudioFile.trimmed(
    outputFilename: String,
    trimRange: CacheTrimRange
): Result<MediaFile.AudioFile> {
    val newFileRes = createAudioFileCatching(
        mediaDirectory = MediaDirectory(Environment.DIRECTORY_MUSIC),
        filename = outputFilename,
        ext = nameWithoutExtension
    )

    if (newFileRes.isFailure)
        return newFileRes

    return Result.success(newFileRes.getOrNull()!!.apply { trim(trimRange) })
}

private suspend inline fun MediaFile.AudioFile.trim(trimRange: CacheTrimRange) = coroutineScope {
    withContext(Dispatchers.IO) {
        FFmpeg.execute(
            "-ss ${trimRange.startPointSecs} " +
                    "-i $absolutePath " +
                    "-t ${trimRange.totalDurationSecs} $absolutePath"
        )
    }
}