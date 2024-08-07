package com.paranid5.crescendo.view_model

import com.paranid5.crescendo.core.common.udf.StateSource
import com.paranid5.crescendo.core.common.udf.UiIntentHandler

internal interface MainViewModel : StateSource<MainState>, UiIntentHandler<MainUiIntent>
