package com.paranid5.crescendo.feature.stream.fetch.view_model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.paranid5.crescendo.ui.foundation.UiState
import com.paranid5.crescendo.ui.foundation.isOk
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class FetchStreamState(
    val url: String = "",
    val uiState: UiState<Unit> = UiState.Initial,
) : Parcelable {
    @IgnoredOnParcel
    val isCancelInputVisible = url.isNotEmpty()

    @IgnoredOnParcel
    val isContinueButtonEnabled = url.isNotBlank()

    @IgnoredOnParcel
    val isError = uiState is UiState.Error

    @IgnoredOnParcel
    val isUrlMangerVisible = uiState.isOk
}
