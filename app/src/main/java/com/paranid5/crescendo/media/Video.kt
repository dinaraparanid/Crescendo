package com.paranid5.crescendo.media

import android.content.Context
import android.provider.MediaStore
import com.arthenica.mobileffmpeg.FFmpeg
import com.paranid5.crescendo.domain.VideoMetadata
import com.paranid5.crescendo.domain.caching.CacheTrimRange
import com.paranid5.crescendo.domain.caching.CachingResult
import com.paranid5.crescendo.domain.caching.Formats
import com.paranid5.crescendo.domain.media.MediaFile
import com.paranid5.crescendo.domain.media.convertToAudioFileAsync
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

suspend fun mergeToMP4AndSetTagsAsync(
    context: Context,
    audioTrack: MediaFile,
    videoTrack: MediaFile.VideoFile,
    mp4StoreFile: MediaFile.VideoFile,
    videoMetadata: VideoMetadata
) = coroutineScope {
    async(Dispatchers.IO) {
        val status = FFmpeg.execute(
            "-y -i ${videoTrack.absolutePath} " +
                    "-y -i ${audioTrack.absolutePath} " +
                    "-c copy ${mp4StoreFile.absolutePath}"
        )

        if (status != 0)
            return@async CachingResult.ConversionError

        val tagsTask = setVideoTagsAsync(
            context = context,
            videoFile = mp4StoreFile,
            videoMetadata = videoMetadata
        )

        audioTrack.delete()
        videoTrack.delete()

        tagsTask.join()
        CachingResult.DownloadResult.Success(mp4StoreFile)
    }
}

/**
 * Converts video file to an audio file with ffmpeg according to the [audioFormat].
 * Adds new file to the [MediaStore] with provided [videoMetadata] tags.
 * For [Formats.MP3] sets tags (with cover) after conversion.
 * Finally, scans file with [android.media.MediaScannerConnection]
 *
 * @param videoMetadata metadata to set
 * @param audioFormat audio file format
 * @return file if conversion was successful, otherwise null
 */

suspend fun MediaFile.VideoFile.convertToAudioFileAndSetTagsAsync(
    context: Context,
    videoMetadata: VideoMetadata,
    audioFormat: Formats,
    trimRange: CacheTrimRange
) = coroutineScope {
    async(Dispatchers.IO) {
        val audioFile = convertToAudioFileAsync(audioFormat, trimRange).await()
            ?: return@async null

        setAudioTagsAsync(
            context,
            audioFile,
            videoMetadata,
            audioFormat
        ).join()
        audioFile
    }
}