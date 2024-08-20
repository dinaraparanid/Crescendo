package com.paranid5.crescendo.domain.cover

import android.graphics.Bitmap

interface CoverRetriever {
    suspend fun getVideoCoverBitmap(videoCovers: List<String>): Bitmap?
}
