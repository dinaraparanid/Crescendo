package com.paranid5.crescendo.core.common.media

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Deprecated("Will be removed")
@Parcelize
@JvmInline
value class MimeType(val value: String) : Parcelable