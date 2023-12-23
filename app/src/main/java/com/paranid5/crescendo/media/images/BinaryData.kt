package com.paranid5.crescendo.media.images

import android.content.Context
import com.paranid5.crescendo.presentation.ui.extensions.byteData

fun getImageBinaryDataFromUrl(context: Context, url: String) =
    getBitmapFromUrlBlockingCatching(context, url)
        .getOrNull()!!
        .byteData

fun getImageBinaryDataFromUrlCatching(context: Context, url: String) =
    runCatching { getImageBinaryDataFromUrl(context, url) }

fun getImageBinaryDataFromPath(context: Context, path: String) =
    getBitmapFromPathBlockingCatching(context, path)
        .getOrNull()!!
        .byteData

fun getImageBinaryDataFromPathCatching(context: Context, path: String) =
    runCatching { getImageBinaryDataFromPath(context, path) }