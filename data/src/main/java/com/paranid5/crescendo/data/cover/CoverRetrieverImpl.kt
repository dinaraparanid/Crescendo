package com.paranid5.crescendo.data.cover

import android.content.Context
import android.graphics.Bitmap
import com.paranid5.crescendo.core.media.images.downloadCoverBitmapAsync
import com.paranid5.crescendo.core.media.images.getTrackCoverBitmapAsync
import com.paranid5.crescendo.domain.cover.CoverRetriever

internal class CoverRetrieverImpl(private val context: Context) : CoverRetriever {
    override suspend fun downloadCoverBitmap(vararg urls: String): Bitmap? =
        downloadCoverBitmapAsync(context = context, urls = urls).await()

    override suspend fun retrieveCoverBitmap(path: String): Bitmap? =
        getTrackCoverBitmapAsync(context = context, path = path).await()
}
