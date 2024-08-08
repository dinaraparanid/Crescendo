package com.paranid5.crescendo.feature.playing.view_model

import com.paranid5.crescendo.core.common.udf.StateSource
import com.paranid5.crescendo.core.common.udf.UiIntentHandler

interface PlayingViewModel : StateSource<PlayingState>, UiIntentHandler<PlayingUiIntent>
