package com.paranid5.crescendo.media

import android.content.Context
import com.paranid5.crescendo.presentation.ui.extensions.byteData

private fun getImageBinaryData(context: Context, url: String) =
    CoilUtils(context)
        .getBitmapFromUrlBlockingCatching(url)
        .getOrNull()!!
        .byteData

fun getImageBinaryDataCatching(context: Context, url: String) =
    runCatching { getImageBinaryData(context, url) }