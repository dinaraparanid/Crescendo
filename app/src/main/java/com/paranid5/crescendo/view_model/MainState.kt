package com.paranid5.crescendo.view_model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.paranid5.crescendo.presentation.entity.ReleaseUiState
import com.paranid5.crescendo.ui.foundation.UiState
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
internal data class MainState(
    val isUpdateDialogShown: Boolean = false,
    val releaseState: UiState<ReleaseUiState> = UiState.Initial,
) : Parcelable
