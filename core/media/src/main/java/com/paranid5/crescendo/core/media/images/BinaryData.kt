package com.paranid5.crescendo.core.media.images

import android.content.Context
import arrow.core.Either
import com.paranid5.crescendo.utils.extensions.byteData

@Deprecated("Will be removed")
fun getImageBinaryDataFromUrl(context: Context, url: String) =
    getBitmapFromUrlBlockingCatching(context, url)
        .getOrNull()!!
        .byteData

@Deprecated("Will be removed")
fun getImageBinaryDataFromUrlCatching(context: Context, url: String) =
    Either.catch { getImageBinaryDataFromUrl(context, url) }

@Deprecated("Will be removed")
fun getImageBinaryDataFromPath(context: Context, path: String) =
    getBitmapFromPathBlockingCatching(context, path)
        .getOrNull()!!
        .byteData

@Deprecated("Will be removed")
fun getImageBinaryDataFromPathCatching(context: Context, path: String) =
    Either.catch { getImageBinaryDataFromPath(context, path) }