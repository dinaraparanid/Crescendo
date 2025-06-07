package com.paranid5.crescendo.core.media.files

import android.os.Environment
import android.util.Log
import arrow.core.Either
import arrow.core.flatMap
import com.paranid5.crescendo.core.common.caching.Formats
import com.paranid5.crescendo.core.common.caching.fileExtension
import com.paranid5.crescendo.core.common.media.MediaFileExtension
import com.paranid5.crescendo.core.common.trimming.FadeDurations
import com.paranid5.crescendo.core.common.trimming.PitchAndSpeed
import com.paranid5.crescendo.core.common.trimming.TrimRange
import com.paranid5.crescendo.core.common.trimming.totalDurationSecs

private const val TAG = "AudioFiles"

@Deprecated("Will be removed")
suspend fun createAudioFileCatching(
    mediaDirectory: MediaDirectory,
    filename: String,
    ext: MediaFileExtension,
) = createFileCatching(mediaDirectory, filename, ext)
    .map { MediaFile.AudioFile(it) }

@Deprecated("Will be removed")
suspend fun MediaFile.AudioFile.trimmedCatching(
    outputFilename: String,
    audioFormat: Formats,
    trimRange: TrimRange,
    pitchAndSpeed: PitchAndSpeed,
    fadeDurations: FadeDurations,
) = createAudioFileCatching(
    mediaDirectory = MediaDirectory(Environment.DIRECTORY_MUSIC),
    filename = outputFilename,
    ext = audioFormat.fileExtension
).flatMap { outputFile ->
    Either.catch {
        trim(
            inputPath = absolutePath,
            outputPath = outputFile.absolutePath,
            trimRange = trimRange,
            pitchAndSpeed = pitchAndSpeed,
            fadeDurations = fadeDurations
        )

        outputFile
    }
}

private fun trim(
    inputPath: String,
    outputPath: String,
    trimRange: TrimRange,
    pitchAndSpeed: PitchAndSpeed,
    fadeDurations: FadeDurations,
) {
    val command = ffmpegTrimCommand(
        inputPath = inputPath,
        outputPath = outputPath,
        trimRange = trimRange,
        pitchAndSpeed = pitchAndSpeed,
        fadeDurations = fadeDurations
    )

    Log.d(TAG, command)
    val status = FFmpeg.execute(command)
    Log.d(TAG, "FFmpeg status: $status")
    require(status == 0)
}

private fun ffmpegTrimCommand(
    inputPath: String,
    outputPath: String,
    trimRange: TrimRange,
    pitchAndSpeed: PitchAndSpeed,
    fadeDurations: FadeDurations
) = "-y -ss ${trimRange.startPointMillis}ms " +
        "-i \"$inputPath\" " +
        "-t ${trimRange.totalDurationMillis}ms " +
        "-af asetrate=44100*${pitchAndSpeed.pitch}," +
        "aresample=44100," +
        "atempo=${pitchAndSpeed.speed}/${pitchAndSpeed.pitch}," +
        "afade=in:0:d=${fadeDurations.fadeInSecs}," +
        "afade=out:st=${trimRange.totalDurationSecs - fadeDurations.fadeOutSecs}:d=${fadeDurations.fadeOutSecs} " +
        "\"$outputPath\""