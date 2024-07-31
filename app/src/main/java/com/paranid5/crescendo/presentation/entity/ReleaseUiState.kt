package com.paranid5.crescendo.presentation.entity

import android.os.Parcelable
import com.paranid5.crescendo.domain.github.dto.Release
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReleaseUiState(
    val htmlUrl: String,
    val tagName: String,
    val name: String,
    val body: String,
) : Parcelable {
    companion object {
        fun fromResponse(release: Release) = ReleaseUiState(
            htmlUrl = release.htmlUrl,
            tagName = release.tagName,
            name = release.name,
            body = release.body,
        )
    }
}
