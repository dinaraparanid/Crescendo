package com.paranid5.crescendo.feature.current_playlist.view_model

import com.paranid5.crescendo.core.common.udf.StateSource
import com.paranid5.crescendo.core.common.udf.UiIntentHandler

interface CurrentPlaylistViewModel :
    StateSource<CurrentPlaylistState>,
    UiIntentHandler<CurrentPlaylistUiIntent>
