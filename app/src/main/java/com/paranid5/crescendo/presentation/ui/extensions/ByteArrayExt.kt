package com.paranid5.crescendo.presentation.ui.extensions

import android.graphics.BitmapFactory

fun ByteArray.toBitmap() = BitmapFactory.decodeByteArray(this, 0, size)