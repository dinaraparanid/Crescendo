package com.paranid5.crescendo.domain.image.model

import android.os.Parcelable
import androidx.annotation.Px
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageSize(@Px val width: Int, @Px val height: Int) : Parcelable