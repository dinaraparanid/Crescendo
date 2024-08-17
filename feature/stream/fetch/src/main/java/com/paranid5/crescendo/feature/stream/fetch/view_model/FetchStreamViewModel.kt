package com.paranid5.crescendo.feature.stream.fetch.view_model

import com.paranid5.crescendo.core.common.udf.StateSource
import com.paranid5.crescendo.core.common.udf.UiIntentHandler

interface FetchStreamViewModel :
    StateSource<FetchStreamState>,
    UiIntentHandler<FetchStreamUiIntent>
