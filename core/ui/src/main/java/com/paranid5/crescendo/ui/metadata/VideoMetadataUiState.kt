package com.paranid5.crescendo.ui.metadata

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.paranid5.crescendo.core.common.metadata.VideoMetadata
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class VideoMetadataUiState(
    val title: String,
    val author: String,
    val coversPaths: ImmutableList<String>,
    val durationMillis: Long,
    val isLiveStream: Boolean,
) : Parcelable {
    companion object {
        fun fromDTO(metadata: VideoMetadata) = VideoMetadataUiState(
            title = metadata.title.orEmpty(),
            author = metadata.author.orEmpty(),
            coversPaths = metadata.covers.toImmutableList(),
            durationMillis = metadata.durationMillis,
            isLiveStream = metadata.isLiveStream,
        )
    }
}
