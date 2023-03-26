package com.paranid5.mediastreamer.presentation.ui.extensions

import android.graphics.BitmapFactory

fun ByteArray.toBitmap() = BitmapFactory.decodeByteArray(this, 0, size)