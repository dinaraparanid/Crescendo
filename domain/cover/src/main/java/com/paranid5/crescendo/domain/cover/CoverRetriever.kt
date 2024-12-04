package com.paranid5.crescendo.domain.cover

import android.graphics.Bitmap

interface CoverRetriever {
    suspend fun downloadCoverBitmap(vararg urls: String): Bitmap?
    suspend fun retrieveCoverBitmap(path: String): Bitmap?
}
