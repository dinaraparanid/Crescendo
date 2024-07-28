package com.paranid5.crescendo.data.current_playlist.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.paranid5.crescendo.data.CurrentPlaylist
import com.paranid5.crescendo.data.current_playlist.CurrentPlaylistRepositoryImpl
import com.paranid5.crescendo.domain.repositories.CurrentPlaylistRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

private const val DATABASE_NAME = "current_playlist.db"

internal val currentPlaylistModule = module {
    singleOf(::CurrentPlaylistRepositoryImpl) bind CurrentPlaylistRepository::class

    single<SqlDriver> {
        AndroidSqliteDriver(
            schema = CurrentPlaylist.Schema,
            context = androidContext(),
            name = DATABASE_NAME,
        )
    }
}