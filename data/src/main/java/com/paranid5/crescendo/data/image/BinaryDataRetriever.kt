package com.paranid5.crescendo.data.image

import android.content.Context
import arrow.core.Either
import com.paranid5.crescendo.data.image.PathRetriever.getBitmapFromPathCatching
import com.paranid5.crescendo.data.image.ResourceRetriever.getBitmapFromResourceCatching
import com.paranid5.crescendo.data.image.UrlRetriever.getBitmapFromUrlCatching
import com.paranid5.crescendo.domain.image.model.ImagePath
import com.paranid5.crescendo.domain.image.model.ImageResource
import com.paranid5.crescendo.domain.image.model.ImageUrl
import com.paranid5.crescendo.domain.image.utils.byteData

internal object BinaryDataRetriever {
    suspend fun getImageBinaryDataFromUrl(context: Context, url: ImageUrl): ByteArray =
        getBitmapFromUrlCatching(context, url).getOrNull()!!.byteData

    suspend fun getImageBinaryDataFromUrlCatching(context: Context, url: ImageUrl): Either<Throwable, ByteArray> =
        Either.catch { getImageBinaryDataFromUrl(context, url) }

    suspend fun getImageBinaryDataFromPath(context: Context, path: ImagePath): ByteArray =
        getBitmapFromPathCatching(context, path).getOrNull()!!.byteData

    suspend fun getImageBinaryDataFromPathCatching(context: Context, path: ImagePath): Either<Throwable, ByteArray> =
        Either.catch { getImageBinaryDataFromPath(context, path) }

    suspend fun getImageBinaryDataFromResource(context: Context, resource: ImageResource): ByteArray =
        getBitmapFromResourceCatching(context, resource).getOrNull()!!.byteData

    suspend fun getImageBinaryDataFromResourceCatching(context: Context, resource: ImageResource): Either<Throwable, ByteArray> =
        Either.catch { getImageBinaryDataFromResource(context, resource) }
}
