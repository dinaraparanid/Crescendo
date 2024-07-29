package com.paranid5.crescendo.data.current_playlist.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.paranid5.crescendo.data.CurrentPlaylist
import com.paranid5.crescendo.data.current_playlist.CurrentPlaylistPublisherImpl
import com.paranid5.crescendo.data.current_playlist.CurrentPlaylistRepositoryImpl
import com.paranid5.crescendo.data.current_playlist.CurrentPlaylistSubscriberImpl
import com.paranid5.crescendo.domain.current_playlist.CurrentPlaylistPublisher
import com.paranid5.crescendo.domain.current_playlist.CurrentPlaylistRepository
import com.paranid5.crescendo.domain.current_playlist.CurrentPlaylistSubscriber
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

private const val DATABASE_NAME = "current_playlist.db"

internal val currentPlaylistModule = module {
    singleOf(::CurrentPlaylistRepositoryImpl) bind CurrentPlaylistRepository::class
    singleOf(::CurrentPlaylistSubscriberImpl) bind CurrentPlaylistSubscriber::class
    singleOf(::CurrentPlaylistPublisherImpl) bind CurrentPlaylistPublisher::class

    single<SqlDriver> {
        AndroidSqliteDriver(
            schema = CurrentPlaylist.Schema,
            context = androidContext(),
            name = DATABASE_NAME,
        )
    }
}