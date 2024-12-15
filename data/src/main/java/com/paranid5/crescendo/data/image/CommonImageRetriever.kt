package com.paranid5.crescendo.data.image

import android.content.Context
import android.graphics.Bitmap
import coil.executeBlocking
import coil.imageLoader
import coil.request.ImageRequest
import com.paranid5.crescendo.domain.image.model.ImageSize
import com.paranid5.crescendo.domain.image.utils.toResizedBitmap

internal object CommonImageRetriever {
    fun ImageLoaderBuilder(context: Context) = context.imageLoader.newBuilder()
    
    fun Any.getCoilModelBuilder(context: Context) =
        ImageRequest.Builder(context)
            .data(this)
            .allowHardware(enable = false)
            .allowConversionToBitmap(enable = true)

    fun Any.getCoilModel(context: Context, size: ImageSize? = null) =
        when (size) {
            null -> getCoilModelBuilder(context).build()

            else -> getCoilModelBuilder(context)
                .size(width = size.width, height = size.height)
                .build()
        }

    suspend fun getBitmapFromModel(
        context: Context,
        model: Any,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ) = ImageLoaderBuilder(context)
        .build()
        .execute(model.getCoilModel(context, size))
        .drawable!!
        .toResizedBitmap(size)
        .also { bitmapMutation?.invoke(it) }

    fun getBitmapFromModelBlocking(
        context: Context,
        model: Any,
        size: ImageSize? = null,
        bitmapMutation: ((Bitmap) -> Unit)? = null,
    ) = ImageLoaderBuilder(context)
        .build()
        .executeBlocking(model.getCoilModel(context, size))
        .drawable!!
        .toResizedBitmap(size)
        .also { bitmapMutation?.invoke(it) }
}
