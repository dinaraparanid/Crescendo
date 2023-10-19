package com.paranid5.crescendo.presentation.ui.utils

import android.util.SparseArray
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile

@Composable
inline fun YouTubeUrlExtractor(url: String, crossinline onExtracted: (String) -> Unit) {
    val context = LocalContext.current

    LaunchedEffect(key1 = context) {
        object : YouTubeExtractor(context) {
            override fun onExtractionComplete(
                ytFiles: SparseArray<YtFile>?,
                videoMeta: VideoMeta?
            ) {
                if (ytFiles == null)
                    return

                val audioTag = 140
                val audioUrl = ytFiles[audioTag].url!!
                onExtracted(audioUrl)
            }
        }.extract(url)
    }
}