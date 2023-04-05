package com.paranid5.mediastreamer.domain

import android.content.Context
import android.util.Log
import android.util.SparseArray
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

@Deprecated("YoutubeExtractor should be created for every single task when it is used")
class YoutubeUrlExtractor<T>(
    context: Context,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main),
    private val videoExtractionChannel: Channel<T>? = null,
    private val onExtractionComplete: suspend (
        audioUrl: String,
        videoUrl: String,
        videoMeta: VideoMeta?,
    ) -> T
) : YouTubeExtractor(context) {
    private companion object {
        private val TAG = YoutubeUrlExtractor::class.simpleName!!
    }

    override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?, videoMeta: VideoMeta?) {
        if (ytFiles == null)
            return

        val audioTag = 140
        val audioUrl = ytFiles[audioTag].url!!

        val videoUrl = sequenceOf(22, 137, 18)
            .map(ytFiles::get)
            .filterNotNull()
            .map(YtFile::getUrl)
            .filterNotNull()
            .filter(String::isNotEmpty)
            .first()

        coroutineScope.launch {
            Log.d(TAG, "Launching extraction")
            val onExtractionCompleteResult = onExtractionComplete(audioUrl, videoUrl, videoMeta)
            Log.d(TAG, "Extraction Completed")
            videoExtractionChannel?.send(onExtractionCompleteResult)
            Log.d(TAG, "Result is sent")
        }
    }
}