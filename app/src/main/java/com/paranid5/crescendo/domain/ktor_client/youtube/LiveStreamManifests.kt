package com.paranid5.crescendo.domain.ktor_client.youtube

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LiveStreamManifests(
    @JvmField val dashManifestUrl: String? = null,
    @JvmField val hlsManifestUrl: String? = null
) : Parcelable
