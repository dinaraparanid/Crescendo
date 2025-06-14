package com.paranid5.crescendo.system.worker.trimmer

import android.os.Environment
import android.util.Log
import arrow.core.Either
import arrow.core.flatMap
import com.paranid5.crescendo.core.common.trimming.FadeDurations
import com.paranid5.crescendo.core.common.trimming.PitchAndSpeed
import com.paranid5.crescendo.core.common.trimming.TrimRange
import com.paranid5.crescendo.core.common.trimming.totalDurationSecs
import com.paranid5.crescendo.core.media.files.FFmpeg
import com.paranid5.crescendo.domain.files.MediaFilesRepository
import com.paranid5.crescendo.domain.files.entity.Filename
import com.paranid5.crescendo.domain.files.entity.Formats
import com.paranid5.crescendo.domain.files.entity.MediaDirectory
import com.paranid5.crescendo.domain.files.entity.MediaFile
import com.paranid5.crescendo.domain.files.entity.fileExtension

private const val TAG = "TrimAudioFileUseCase"

internal class TrimAudioFileUseCase(
    private val mediaFilesRepository: MediaFilesRepository,
) {

    suspend operator fun invoke(
        file: MediaFile.AudioFile,
        outputFilename: Filename,
        audioFormat: Formats,
        trimRange: TrimRange,
        pitchAndSpeed: PitchAndSpeed,
        fadeDurations: FadeDurations,
    ) = mediaFilesRepository.createAudioFile(
        mediaDirectory = MediaDirectory(Environment.DIRECTORY_MUSIC),
        filename = outputFilename,
        ext = audioFormat.fileExtension,
    ).flatMap { outputFile ->
        Either.catch {
            trim(
                inputPath = file.absolutePath,
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

        if (BuildConfig.DEBUG) Log.d(TAG, command)
        val status = FFmpeg.execute(command)
        if (BuildConfig.DEBUG) Log.d(TAG, "FFmpeg status: $status")
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
}
