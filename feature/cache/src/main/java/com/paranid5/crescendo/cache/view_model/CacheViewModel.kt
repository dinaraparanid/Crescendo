package com.paranid5.crescendo.cache.view_model

import com.paranid5.crescendo.core.common.udf.StateSource
import com.paranid5.crescendo.core.common.udf.UiIntentHandler

interface CacheViewModel : StateSource<CacheState>, UiIntentHandler<CacheUiIntent>
