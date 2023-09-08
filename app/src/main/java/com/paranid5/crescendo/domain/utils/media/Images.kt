package com.paranid5.crescendo.domain.utils.media

import android.content.Context
import com.paranid5.crescendo.presentation.ui.extensions.byteData
import com.paranid5.crescendo.presentation.ui.utils.CoilUtils

private fun getImageBinaryData(context: Context, url: String) =
    CoilUtils(context)
        .getBitmapFromUrlBlockingCatching(url)
        .getOrNull()!!
        .byteData

fun getImageBinaryDataCatching(context: Context, url: String) =
    runCatching { getImageBinaryData(context, url) }