package com.paranid5.crescendo.feature.meta_editor.view_model

import com.paranid5.crescendo.core.common.udf.StateSource
import com.paranid5.crescendo.core.common.udf.UiEffectProducer
import com.paranid5.crescendo.core.common.udf.UiIntentHandler

interface MetaEditorViewModel :
    StateSource<MetaEditorState>,
    UiIntentHandler<MetaEditorUiIntent>,
    UiEffectProducer<MetaEditorUiEffect>
