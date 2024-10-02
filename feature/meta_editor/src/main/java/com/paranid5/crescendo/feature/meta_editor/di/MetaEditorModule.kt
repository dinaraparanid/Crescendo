package com.paranid5.crescendo.feature.meta_editor.di

import com.paranid5.crescendo.feature.meta_editor.view_model.MetaEditorViewModelImpl
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val metaEditorModule = module {
    viewModelOf(::MetaEditorViewModelImpl)
}