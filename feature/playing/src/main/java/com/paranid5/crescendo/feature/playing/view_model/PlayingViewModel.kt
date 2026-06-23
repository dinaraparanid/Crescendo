package com.paranid5.crescendo.feature.playing.view_model

import com.paranid5.crescendo.core.common.udf.StateSource
import com.paranid5.crescendo.core.common.udf.UiEffectProducer
import com.paranid5.crescendo.core.common.udf.UiIntentHandler
import com.paranid5.crescendo.domain.image.model.BitmapDrawableWithPalette
import com.paranid5.crescendo.domain.image.model.ImagePath
import com.paranid5.crescendo.domain.image.model.ImageUrl

interface PlayingViewModel :
    StateSource<PlayingState>,
    UiIntentHandler<PlayingUiIntent>,
    UiEffectProducer<PlayingScreenEffect> {

    suspend fun retrieveBitmapDrawableFromMediaWithPalette(
        path: ImagePath,
    ): BitmapDrawableWithPalette?

    suspend fun downloadBitmapDrawableWithPalette(
        url: ImageUrl,
    ): BitmapDrawableWithPalette?
}
