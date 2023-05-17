package com.paranid5.mediastreamer.domain.utils.media

import android.content.Context
import com.arthenica.mobileffmpeg.FFmpeg
import com.paranid5.mediastreamer.data.VideoMetadata
import com.paranid5.mediastreamer.domain.video_cash_service.CashingResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

internal suspend inline fun mergeToMP4AndSetTagsAsync(
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
            return@async CashingResult.ConversionError

        val tagsTask = setVideoTagsAsync(
            context = context,
            videoFile = mp4StoreFile,
            videoMetadata = videoMetadata
        )

        audioTrack.delete()
        videoTrack.delete()

        tagsTask.join()
        CashingResult.DownloadResult.Success(mp4StoreFile)
    }
}