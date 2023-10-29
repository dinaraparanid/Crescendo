package com.paranid5.crescendo.domain.ktor_client.youtube

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoMeta(
    val videoId: String,
    val title: String,
    val author: String,
    val channelId: String,
    val videoLengthSecs: Long,
    val viewCount: Long,
    val isLiveStream: Boolean,
    val shortDescription: String
) : Parcelable {
    private companion object {
        private const val IMAGE_BASE_URL = "http://i.ytimg.com/vi/"
    }

    /** 120 x 90 */
    val thumbnailUrl
        get() = "$IMAGE_BASE_URL$videoId/default.jpg"

    /** 320 x 180 */
    val mqImageUrl
        get() = "$IMAGE_BASE_URL$videoId/mqdefault.jpg"

    /** 480 x 360 */
    val hqImageUrl
        get() = "$IMAGE_BASE_URL$videoId/hqdefault.jpg"

    /** 640 x 480 */
    val sdImageUrl
        get() = "$IMAGE_BASE_URL$videoId/sddefault.jpg"

    /** Max Res */
    val maxResImageUrl
        get() = "$IMAGE_BASE_URL$videoId/maxresdefault.jpg"
}