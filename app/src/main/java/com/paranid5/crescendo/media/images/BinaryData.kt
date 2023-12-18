package com.paranid5.crescendo.media.images

import android.content.Context
import com.paranid5.crescendo.presentation.ui.extensions.byteData

fun getImageBinaryData(context: Context, url: String) =
    getBitmapFromUrlBlockingCatching(context, url)
        .getOrNull()!!
        .byteData

fun getImageBinaryDataCatching(context: Context, url: String) =
    runCatching { getImageBinaryData(context, url) }