package com.paranid5.mediastreamer

import android.content.Context
import android.util.SparseArray
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile

class YoutubeAudioUrlExtractor(
    context: Context,
    private val onExtractionComplete: (
        audioUrl: String,
        videoUrl: String,
        videoMeta: VideoMeta?
    ) -> Unit
) : YouTubeExtractor(context) {
    override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?, videoMeta: VideoMeta?) {
        ytFiles?.let { youtubeFiles ->
            val audioTag = 140
            val audioUrl = youtubeFiles[audioTag].url!!

            val videoUrl = sequenceOf(22, 137, 18)
                .map(youtubeFiles::get)
                .filterNotNull()
                .map(YtFile::getUrl)
                .filterNotNull()
                .filter(String::isNotEmpty)
                .first()

            onExtractionComplete(audioUrl, videoUrl, videoMeta)
        }
    }
}