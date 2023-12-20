package com.paranid5.crescendo.domain.media

import android.os.Environment
import android.util.Log
import com.arthenica.mobileffmpeg.FFmpeg
import com.paranid5.crescendo.domain.caching.CacheTrimRange
import com.paranid5.crescendo.domain.caching.Formats
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.io.File

private const val TAG = "Audio"

sealed class MediaFile(value: File) : File(value.absolutePath) {
    companion object {
        private const val serialVersionUID: Long = -4175671868438928438L
    }

    class VideoFile(value: File) : MediaFile(value) {
        companion object {
            private const val serialVersionUID: Long = 6914322109341150479L
        }
    }

    class AudioFile(value: File) : MediaFile(value) {
        companion object {
            private const val serialVersionUID: Long = 2578824723183659601L
        }
    }
}

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
        val ext = audioFormat.audioFileExt

        val newFile = createMediaFileCatching(
            mediaDirectory = MediaDirectory(Environment.DIRECTORY_MUSIC),
            filename = nameWithoutExtension,
            ext = ext
        ).getOrNull() ?: return@async null

        Log.d(TAG, "Converting to file: ${newFile.absolutePath}")
        toAudioFile(newFile, ffmpegCmd)
    }
}

private inline val Formats.audioFileExt
    get() = when (this) {
        Formats.MP3 -> "mp3"
        Formats.AAC -> "aac"
        Formats.WAV -> "wav"
        Formats.MP4 -> throw IllegalArgumentException("MP4 passed as an audio format")
    }

private inline fun MediaFile.VideoFile.toAudioFile(
    newFile: MediaFile.VideoFile,
    crossinline ffmpegCmd: (File) -> String
) = when (FFmpeg.execute(ffmpegCmd(newFile))) {
    0 -> {
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

suspend fun MediaFile.VideoFile.toMP3Async(trimRange: CacheTrimRange) =
    toAudioFileImplAsync(audioFormat = Formats.MP3) { newFile ->
        "-y -i $absolutePath -ss ${trimRange.startPoint} -to ${trimRange.endPoint} -vn -acodec libmp3lame -qscale:a 2 ${newFile.absolutePath}"
    }

/**
 * Converts video file to .wav audio file with ffmpeg
 * @return .wav file if conversion was successful, otherwise null
 */

suspend fun MediaFile.VideoFile.toWAVAsync(trimRange: CacheTrimRange) =
    toAudioFileImplAsync(audioFormat = Formats.WAV) { newFile ->
        "-y -i $absolutePath -ss ${trimRange.startPoint} -to ${trimRange.endPoint} -vn -acodec pcm_s16le -ar 44100 ${newFile.absolutePath}"
    }

/**
 * Converts video file to .aac audio file with ffmpeg
 * @return .aac file if conversion was successful, otherwise null
 */

suspend fun MediaFile.VideoFile.toAACAsync(trimRange: CacheTrimRange) =
    toAudioFileImplAsync(audioFormat = Formats.AAC) { newFile ->
        "-y -i $absolutePath -ss ${trimRange.startPoint} -to ${trimRange.endPoint} -vn -c:a aac -b:a 256k ${newFile.absolutePath}"
    }

/**
 * Converts video file to an audio file with ffmpeg
 * @param audioFormat audio file format
 * @return file if conversion was successful, otherwise null
 */

suspend fun MediaFile.VideoFile.toAudioFileAsync(
    audioFormat: Formats,
    trimRange: CacheTrimRange
): Deferred<MediaFile.AudioFile?> {
    Log.d(TAG, "Audio conversion to $audioFormat")

    return when (audioFormat) {
        Formats.MP3 -> toMP3Async(trimRange)
        Formats.WAV -> toWAVAsync(trimRange)
        Formats.AAC -> toAACAsync(trimRange)
        Formats.MP4 -> throw IllegalArgumentException("MP4 passed as an audio format")
    }
}