package com.paranid5.crescendo.tracks.view_model

import com.paranid5.crescendo.core.common.udf.StateSource
import com.paranid5.crescendo.core.common.udf.UiIntentHandler

interface TracksViewModel : StateSource<TracksState>, UiIntentHandler<TracksUiIntent>
