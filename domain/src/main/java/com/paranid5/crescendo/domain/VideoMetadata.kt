package com.paranid5.crescendo.domain

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.paranid5.yt_url_extractor_kt.VideoMeta
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent

@Serializable
@Parcelize
@Immutable
data class VideoMetadata(
    @JvmField val title: String? = null,
    @JvmField val author: String? = null,
    @JvmField val covers: List<String> = listOf(),
    @JvmField val lenInMillis: Long = 0,
    @JvmField val isLiveStream: Boolean = false
) : Parcelable {
    internal companion object : KoinComponent

    constructor(youtubeMeta: VideoMeta) : this(
        title = youtubeMeta.title,
        author = youtubeMeta.author,
        lenInMillis = youtubeMeta.videoLengthSecs * 1000,
        isLiveStream = youtubeMeta.isLiveStream,
        covers = youtubeMeta.run {
            listOf(maxResImageUrl, hqImageUrl, mqImageUrl, sdImageUrl, thumbnailUrl)
        }
    )
}