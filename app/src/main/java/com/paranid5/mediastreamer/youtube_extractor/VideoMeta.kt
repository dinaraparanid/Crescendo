package com.paranid5.mediastreamer.youtube_extractor

data class VideoMeta(
    val videoId: String?,
    val title: String?,
    val author: String?,
    val channelId: String?,
    val videoLenInSecs: Long,
    val viewCount: Long,
    val isLiveStream: Boolean,
    val shortDescription: String
) {
    companion object {
        private const val IMAGE_BASE_URL = "http://i.ytimg.com/vi/"
    }

    /** 120 x 90 */
    internal inline val smallThumbnailUrl: String
        get() = "$IMAGE_BASE_URL$videoId/default.jpg"

    /** 320 x 180 */
    internal inline val mediumThumbnailUrl: String
        get() = "$IMAGE_BASE_URL$videoId/mqdefault.jpg"

    /** 480 x 360 */
    internal inline val bigThumbnailUrl: String
        get() = "$IMAGE_BASE_URL$videoId/hqdefault.jpg"

    /** 640 x 480 */
    internal inline val largeThumbnailUrl: String
        get() = "$IMAGE_BASE_URL$videoId/sddefault.jpg"

    /** Maximum resolution */
    internal inline val maxSizeThumbnailUrl: String
        get() = "$IMAGE_BASE_URL$videoId/maxresdefault.jpg"
}