package com.paranid5.crescendo.feature.stream.view_model

import com.paranid5.crescendo.core.common.udf.StateSource
import com.paranid5.crescendo.core.common.udf.UiIntentHandler

interface StreamViewModel : StateSource<StreamState>, UiIntentHandler<StreamUiIntent>
