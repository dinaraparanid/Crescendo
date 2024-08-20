package com.paranid5.crescendo.feature.stream.fetch.view_model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.paranid5.crescendo.ui.covers.ImageContainer
import com.paranid5.crescendo.ui.foundation.UiState
import com.paranid5.crescendo.ui.foundation.getOrNull
import com.paranid5.crescendo.ui.foundation.isOk
import com.paranid5.crescendo.ui.metadata.VideoMetadataUiState
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class FetchStreamState(
    val url: String = "",
    val videoMetadataUiState: UiState<VideoMetadataUiState> = UiState.Initial,
    val coverUiState: UiState<ImageContainer> = UiState.Initial,
) : Parcelable {
    @IgnoredOnParcel
    val isCancelInputVisible = url.isNotEmpty()

    @IgnoredOnParcel
    val isError = videoMetadataUiState is UiState.Error

    @IgnoredOnParcel
    val isUrlEditorVisible = videoMetadataUiState is UiState.Initial

    @IgnoredOnParcel
    val isUrlMangerVisible = videoMetadataUiState.isOk

    @IgnoredOnParcel
    val isContinueButtonEnabled = url.isNotBlank() && isUrlEditorVisible

    @IgnoredOnParcel
    val isDownloadButtonVisible = videoMetadataUiState.getOrNull()?.isLiveStream == false
}
