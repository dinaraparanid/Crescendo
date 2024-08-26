package com.paranid5.crescendo.trimmer.view_model

import com.paranid5.crescendo.core.common.udf.StateSource
import com.paranid5.crescendo.core.common.udf.UiIntentHandler

interface TrimmerViewModel : StateSource<TrimmerState>, UiIntentHandler<TrimmerUiIntent>
