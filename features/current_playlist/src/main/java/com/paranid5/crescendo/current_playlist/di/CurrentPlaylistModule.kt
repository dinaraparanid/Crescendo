package com.paranid5.crescendo.current_playlist.di

import com.paranid5.crescendo.current_playlist.presentation.view_model.CurrentPlaylistViewModelImpl
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val currentPlaylistModule = module {
    viewModelOf(::CurrentPlaylistViewModelImpl)
}