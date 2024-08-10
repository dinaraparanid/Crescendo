package com.paranid5.crescendo.feature.play.main.view_model

import com.paranid5.crescendo.core.common.udf.StateSource
import com.paranid5.crescendo.core.common.udf.UiIntentHandler

interface PlayViewModel : StateSource<PlayState>, UiIntentHandler<PlayUiIntent>
