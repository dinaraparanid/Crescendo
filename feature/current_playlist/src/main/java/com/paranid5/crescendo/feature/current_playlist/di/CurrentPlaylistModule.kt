package com.paranid5.crescendo.feature.current_playlist.di

import com.paranid5.crescendo.feature.current_playlist.view_model.CurrentPlaylistViewModelImpl
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val currentPlaylistModule = module {
    viewModelOf(::CurrentPlaylistViewModelImpl)
}