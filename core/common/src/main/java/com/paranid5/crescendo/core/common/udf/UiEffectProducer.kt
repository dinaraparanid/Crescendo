package com.paranid5.crescendo.core.common.udf

import kotlinx.coroutines.flow.SharedFlow

interface UiEffectProducer<UiEffect> {
    val effectFlow: SharedFlow<UiEffect>
}
