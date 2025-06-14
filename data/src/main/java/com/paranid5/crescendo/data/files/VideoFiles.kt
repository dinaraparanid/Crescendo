package com.paranid5.crescendo.data.files

import android.os.Environment
import arrow.core.Either
import arrow.core.raise.nullable
import com.paranid5.crescendo.core.common.trimming.TrimRange
import com.paranid5.crescendo.core.media.files.FFmpeg
import com.paranid5.crescendo.data.files.Files.createFileCatching
import com.paranid5.crescendo.domain.files.entity.Filename
import com.paranid5.crescendo.domain.files.entity.Formats
import com.paranid5.crescendo.domain.files.entity.MediaDirectory
import com.paranid5.crescendo.domain.files.entity.MediaFile
import com.paranid5.crescendo.domain.files.entity.MediaFileExtension
import com.paranid5.crescendo.domain.files.entity.fileExtension
import java.io.File

internal object VideoFiles {
    suspend fun createVideoFileCatching(
        filename: Filename,
        ext: MediaFileExtension,
        mediaDirectory: MediaDirectory,
    ): Either<Throwable, MediaFile.VideoFile> = createFileCatching(
        mediaDirectory = mediaDirectory,
        filename = filename,
        ext = ext,
    ).map(MediaFile::VideoFile)

    suspend fun MediaFile.VideoFile.toAudioFile(
        audioFormat: Formats,
        trimRange: TrimRange,
    ): MediaFile.AudioFile? = when (audioFormat) {
        Formats.MP3 -> toMP3Async(trimRange)
        Formats.WAV -> toWAVAsync(trimRange)
        Formats.AAC -> toAACAsync(trimRange)
        Formats.MP4 -> throw IllegalArgumentException("MP4 passed as an audio format")
    }

    private suspend inline fun MediaFile.VideoFile.toAudioFileImpl(
        audioFormat: Formats,
        crossinline ffmpegCmd: (File) -> String
    ): MediaFile.AudioFile? = nullable {
        val newFile = createFileCatching(
            mediaDirectory = MediaDirectory(Environment.DIRECTORY_MUSIC),
            filename = Filename(nameWithoutExtension),
            ext = audioFormat.fileExtension,
        ).getOrNull().bind()

        toAudioFile(newFile, ffmpegCmd)
    }

    private inline fun MediaFile.VideoFile.toAudioFile(
        newFile: File,
        crossinline ffmpegCmd: (File) -> String,
    ): MediaFile.AudioFile? = when (FFmpeg.execute(ffmpegCmd(newFile))) {
        0 -> {
            delete()
            MediaFile.AudioFile(newFile)
        }

        else -> {
            newFile.delete()
            null
        }
    }

    private suspend inline fun MediaFile.VideoFile.toMP3Async(trimRange: TrimRange): MediaFile.AudioFile? =
        toAudioFileImpl(audioFormat = Formats.MP3) { newFile ->
            "-y -i \"$absolutePath\" " +
                    trimRange.ffmpegStartParam +
                    trimRange.ffmpegDurationParam +
                    "-vn -acodec libmp3lame " +
                    "-qscale:a 2 \"${newFile.absolutePath}\""
        }

    private suspend inline fun MediaFile.VideoFile.toWAVAsync(trimRange: TrimRange): MediaFile.AudioFile? =
        toAudioFileImpl(audioFormat = Formats.WAV) { newFile ->
            "-y -i \"$absolutePath\" " +
                    trimRange.ffmpegStartParam +
                    trimRange.ffmpegDurationParam +
                    "-vn -acodec pcm_s16le " +
                    "-ar 44100 \"${newFile.absolutePath}\""
        }

    private suspend inline fun MediaFile.VideoFile.toAACAsync(trimRange: TrimRange): MediaFile.AudioFile? =
        toAudioFileImpl(audioFormat = Formats.AAC) { newFile ->
            "-y -i \"$absolutePath\" " +
                    trimRange.ffmpegStartParam +
                    trimRange.ffmpegDurationParam +
                    "-vn -c:a aac " +
                    "-b:a 256k \"${newFile.absolutePath}\""
        }

    private inline val TrimRange.ffmpegStartParam
        get() = "-ss ${startPointMillis}ms "

    private inline val TrimRange.ffmpegDurationParam
        get() = totalDurationMillis
            .takeIf { it > 0 }
            ?.let { "-to ${totalDurationMillis}ms " }
            .orEmpty()
}
