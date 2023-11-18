package com.paranid5.crescendo.data

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.paranid5.crescendo.STREAM_WITH_NO_NAME
import com.paranid5.crescendo.UNKNOWN_STREAMER
import com.paranid5.yt_url_extractor_kt.VideoMeta
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.qualifier.named

@Serializable
@Parcelize
@Immutable
data class VideoMetadata(
    @JvmField val title: String = get(named(STREAM_WITH_NO_NAME)),
    @JvmField val author: String = get(named(UNKNOWN_STREAMER)),
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