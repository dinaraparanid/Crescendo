package com.paranid5.crescendo.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.paranid5.crescendo.data.current_playlist.CurrentPlaylistRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sqlDelightModule = module {
    single<SqlDriver> {
        AndroidSqliteDriver(
            CurrentPlaylist.Schema,
            androidContext(),
            "current_playlist.db"
        )
    }

    singleOf(::CurrentPlaylistRepository)
}