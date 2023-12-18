package com.paranid5.crescendo.media.images

import android.content.Context
import androidx.annotation.Px
import coil.imageLoader
import coil.request.ImageRequest

data class ImageSize(@Px val width: Int, @Px val height: Int)

fun ImageLoaderBuilder(context: Context) =
    context.imageLoader.newBuilder()

fun Any.getCoilModelBuilder(context: Context) =
    ImageRequest.Builder(context)
        .data(this)
        .allowHardware(false)
        .allowConversionToBitmap(true)