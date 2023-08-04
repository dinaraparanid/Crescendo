package com.paranid5.crescendo.domain.utils.media

import android.content.Context
import com.bumptech.glide.Glide
import com.paranid5.crescendo.presentation.ui.extensions.byteData

private fun getImageBinaryData(context: Context, url: String) =
    Glide.with(context)
        .asBitmap()
        .load(url)
        .submit()
        .get()
        .byteData

fun getImageBinaryDataCatching(context: Context, url: String) =
    runCatching { getImageBinaryData(context, url) }