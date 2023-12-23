package com.paranid5.crescendo.domain.media.files

import android.os.Environment
import android.util.Log
import com.arthenica.mobileffmpeg.FFmpeg
import com.paranid5.crescendo.domain.trimming.TrimRange
import com.paranid5.crescendo.domain.caching.Formats
import com.paranid5.crescendo.domain.caching.audioFileExt
import com.paranid5.crescendo.domain.trimming.FadeDurations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

private const val TAG = "AudioFiles"

suspend fun createAudioFileCatching(
    mediaDirectory: MediaDirectory,
    filename: String,
    ext: String
) = createFileCatching(mediaDirectory, filename, ext)
    .map { MediaFile.AudioFile(it) }

suspend fun MediaFile.AudioFile.trimmed(
    outputFilename: String,
    audioFormat: Formats,
    trimRange: TrimRange,
    fadeDurations: FadeDurations,
): Result<MediaFile.AudioFile> {
    val newFileRes = createAudioFileCatching(
        mediaDirectory = MediaDirectory(Environment.DIRECTORY_MUSIC),
        filename = outputFilename,
        ext = audioFormat.audioFileExt
    )

    if (newFileRes.isFailure)
        return newFileRes

    return runCatching {
        newFileRes
            .getOrNull()!!
            .also { outputFile ->
                trim(
                    inputPath = absolutePath,
                    outputPath = outputFile.absolutePath,
                    trimRange = trimRange,
                    fadeDurations = fadeDurations
                )
            }
    }
}

private suspend inline fun trim(
    inputPath: String,
    outputPath: String,
    trimRange: TrimRange,
    fadeDurations: FadeDurations
) = coroutineScope {
    withContext(Dispatchers.IO) {
        val command = ffmpegTrimCommand(
            inputPath = inputPath,
            outputPath = outputPath,
            trimRange = trimRange,
            fadeDurations = fadeDurations
        )

        Log.d(TAG, command)
        FFmpeg.execute(command)
    }
}

private fun ffmpegTrimCommand(
    inputPath: String,
    outputPath: String,
    trimRange: TrimRange,
    fadeDurations: FadeDurations
) = "-y -ss ${trimRange.startPointSecs} " +
        "-i \"$inputPath\" " +
        "-t ${trimRange.totalDurationSecs} " +
        "-af afade=in:0:d=${fadeDurations.fadeInSecs}," +
        "afade=out:st=${trimRange.totalDurationSecs - fadeDurations.fadeOutSecs}:d=${fadeDurations.fadeOutSecs} " +
        "\"$outputPath\""