package com.paranid5.crescendo.core.common.udf

interface UiIntentHandler<UiIntent> {
    fun onUiIntent(intent: UiIntent)
}
