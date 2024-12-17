package com.paranid5.crescendo.domain.files.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@JvmInline
value class MimeType(val value: String) : Parcelable
