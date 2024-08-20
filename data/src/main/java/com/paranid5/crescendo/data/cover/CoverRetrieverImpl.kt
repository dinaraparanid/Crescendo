package com.paranid5.crescendo.data.cover

import android.content.Context
import android.graphics.Bitmap
import com.paranid5.crescendo.core.media.images.getVideoCoverBitmapAsync
import com.paranid5.crescendo.domain.cover.CoverRetriever

internal class CoverRetrieverImpl(private val context: Context) : CoverRetriever {
    override suspend fun getVideoCoverBitmap(videoCovers: List<String>): Bitmap? =
        getVideoCoverBitmapAsync(context = context, videoCovers = videoCovers).await()
}
