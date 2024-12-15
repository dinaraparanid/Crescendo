package com.paranid5.crescendo.core.media.images

import android.content.Context
import android.os.Parcelable
import androidx.annotation.Px
import androidx.compose.runtime.Immutable
import coil.imageLoader
import coil.request.ImageRequest
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
@Deprecated("Will be removed")
data class ImageSize(@Px val width: Int, @Px val height: Int) : Parcelable

@Deprecated("Will be removed")
fun ImageLoaderBuilder(context: Context) =
    context.imageLoader.newBuilder()

@Deprecated("Will be removed")
fun Any.getCoilModelBuilder(context: Context) =
    ImageRequest.Builder(context)
        .data(this)
        .allowHardware(false)
        .allowConversionToBitmap(true)