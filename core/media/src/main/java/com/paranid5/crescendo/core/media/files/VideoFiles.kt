package com.paranid5.crescendo.core.media.files

import android.os.Environment
import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.SessionState
import com.paranid5.crescendo.core.common.caching.Formats
import com.paranid5.crescendo.core.common.caching.fileExtension
import com.paranid5.crescendo.core.common.media.MediaFileExtension
import com.paranid5.crescendo.core.common.trimming.TrimRange
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.io.File

private const val TAG = "VideoFiles"

@Deprecated("Will be removed")
suspend fun createVideoFileCatching(
    filename: String,
    ext: MediaFileExtension,
    mediaDirectory: MediaDirectory = MediaDirectory(Environment.DIRECTORY_MOVIES)
) = createFileCatching(mediaDirectory, filename, ext)
    .map { MediaFile.VideoFile(it) }

/**
 * Converts video file to an audio file with ffmpeg
 * @param audioFormat audio file format
 * @param ffmpegCmd ffmpeg cmd command to execute
 * @return file if conversion was successful, otherwise null
 */

private suspend inline fun MediaFile.VideoFile.toAudioFileImplAsync(
    audioFormat: Formats,
    crossinline ffmpegCmd: (File) -> String
) = coroutineScope {
    async(Dispatchers.IO) {
        val ext = audioFormat.fileExtension

        val newFile = createFileCatching(
            mediaDirectory = MediaDirectory(Environment.DIRECTORY_MUSIC),
            filename = nameWithoutExtension,
            ext = ext
        ).getOrNull() ?: return@async null

        Log.d(TAG, "Converting to file: ${newFile.absolutePath}")
        toAudioFile(newFile, ffmpegCmd)
    }
}

private inline fun MediaFile.VideoFile.toAudioFile(
    newFile: File,
    crossinline ffmpegCmd: (File) -> String
) = when (FFmpegKit.execute(ffmpegCmd(newFile)).state) {
    SessionState.FAILED -> {
        delete()
        MediaFile.AudioFile(newFile)
    }

    else -> {
        newFile.delete()
        null
    }
}

/**
 * Converts video file to .mp3 audio file with ffmpeg
 * @return .mp3 file if conversion was successful, otherwise null
 */

@Deprecated("Will be removed")
suspend fun MediaFile.VideoFile.toMP3Async(trimRange: TrimRange) =
    toAudioFileImplAsync(audioFormat = Formats.MP3) { newFile ->
        "-y -i \"$absolutePath\" " +
                trimRange.ffmpegStartParam +
                trimRange.ffmpegDurationParam +
                "-vn -acodec libmp3lame " +
                "-qscale:a 2 \"${newFile.absolutePath}\""
    }

/**
 * Converts video file to .wav audio file with ffmpeg
 * @return .wav file if conversion was successful, otherwise null
 */

@Deprecated("Will be removed")
suspend fun MediaFile.VideoFile.toWAVAsync(trimRange: TrimRange) =
    toAudioFileImplAsync(audioFormat = Formats.WAV) { newFile ->
        "-y -i \"$absolutePath\" " +
                trimRange.ffmpegStartParam +
                trimRange.ffmpegDurationParam +
                "-vn -acodec pcm_s16le " +
                "-ar 44100 \"${newFile.absolutePath}\""
    }

/**
 * Converts video file to .aac audio file with ffmpeg
 * @return .aac file if conversion was successful, otherwise null
 */

@Deprecated("Will be removed")
suspend fun MediaFile.VideoFile.toAACAsync(trimRange: TrimRange) =
    toAudioFileImplAsync(audioFormat = Formats.AAC) { newFile ->
        "-y -i \"$absolutePath\" " +
                trimRange.ffmpegStartParam +
                trimRange.ffmpegDurationParam +
                "-vn -c:a aac " +
                "-b:a 256k \"${newFile.absolutePath}\""
    }

/**
 * Converts video file to an audio file with ffmpeg
 * @param audioFormat audio file format
 * @return file if conversion was successful, otherwise null
 */

@Deprecated("Will be removed")
suspend fun MediaFile.VideoFile.toAudioFileAsync(
    audioFormat: Formats,
    trimRange: TrimRange,
): Deferred<MediaFile.AudioFile?> {
    Log.d(TAG, "Audio conversion to $audioFormat")

    return when (audioFormat) {
        Formats.MP3 -> toMP3Async(trimRange)
        Formats.WAV -> toWAVAsync(trimRange)
        Formats.AAC -> toAACAsync(trimRange)
        Formats.MP4 -> throw IllegalArgumentException("MP4 passed as an audio format")
    }
}

private inline val TrimRange.ffmpegStartParam
    get() = "-ss ${startPointMillis}ms "

private inline val TrimRange.ffmpegDurationParam
    get() = totalDurationMillis
        .takeIf { it > 0 }
        ?.let { "-to ${totalDurationMillis}ms " }
        .orEmpty()
