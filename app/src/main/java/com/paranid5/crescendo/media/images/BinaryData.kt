package com.paranid5.crescendo.media.images

import android.content.Context
import arrow.core.Either
import com.paranid5.crescendo.presentation.ui.extensions.byteData

fun getImageBinaryDataFromUrl(context: Context, url: String) =
    getBitmapFromUrlBlockingCatching(context, url)
        .getOrNull()!!
        .byteData

fun getImageBinaryDataFromUrlCatching(context: Context, url: String) =
    Either.catch { getImageBinaryDataFromUrl(context, url) }

fun getImageBinaryDataFromPath(context: Context, path: String) =
    getBitmapFromPathBlockingCatching(context, path)
        .getOrNull()!!
        .byteData

fun getImageBinaryDataFromPathCatching(context: Context, path: String) =
    Either.catch { getImageBinaryDataFromPath(context, path) }