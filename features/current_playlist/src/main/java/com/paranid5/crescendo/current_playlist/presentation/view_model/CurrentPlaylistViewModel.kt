package com.paranid5.crescendo.current_playlist.presentation.view_model

import com.paranid5.crescendo.core.common.udf.StateSource
import com.paranid5.crescendo.core.common.udf.UiIntentHandler

interface CurrentPlaylistViewModel :
    StateSource<CurrentPlaylistState>,
    UiIntentHandler<CurrentPlaylistUiIntent>
