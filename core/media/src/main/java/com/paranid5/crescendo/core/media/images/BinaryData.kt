package com.paranid5.crescendo.core.media.images

import android.content.Context
import arrow.core.Either
import com.paranid5.crescendo.utils.extensions.byteData
import com.paranid5.crescendo.utils.extensions.catchNonCancellation
import com.paranid5.crescendo.utils.extensions.notNull

@Deprecated("Will be removed")
fun getImageBinaryDataFromUrl(context: Context, url: String) =
    getBitmapFromUrlBlockingCatching(context, url)
        .getOrNull()
        .notNull
        .byteData

@Deprecated("Will be removed")
fun getImageBinaryDataFromUrlCatching(context: Context, url: String) =
    Either.catchNonCancellation { getImageBinaryDataFromUrl(context, url) }

@Deprecated("Will be removed")
fun getImageBinaryDataFromPath(context: Context, path: String) =
    getBitmapFromPathBlockingCatching(context, path)
        .getOrNull()
        .notNull
        .byteData

@Deprecated("Will be removed")
fun getImageBinaryDataFromPathCatching(context: Context, path: String) =
    Either.catchNonCancellation { getImageBinaryDataFromPath(context, path) }