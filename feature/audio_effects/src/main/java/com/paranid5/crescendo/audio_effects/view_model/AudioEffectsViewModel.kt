package com.paranid5.crescendo.audio_effects.view_model

import com.paranid5.crescendo.core.common.udf.StateSource
import com.paranid5.crescendo.core.common.udf.UiIntentHandler

interface AudioEffectsViewModel :
    StateSource<AudioEffectsState>,
    UiIntentHandler<AudioEffectsUiIntent>
