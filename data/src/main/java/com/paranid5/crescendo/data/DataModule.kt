package com.paranid5.crescendo.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.paranid5.crescendo.data.audio_effects.di.audioEffectsModule
import com.paranid5.crescendo.data.current_playlist.CurrentPlaylistRepositoryImpl
import com.paranid5.crescendo.domain.repositories.CurrentPlaylistRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

private const val DATABASE_NAME = "current_playlist.db"

val dataModule = module {
    includes(audioEffectsModule)

    single<SqlDriver> {
        AndroidSqliteDriver(
            CurrentPlaylist.Schema,
            androidContext(),
            DATABASE_NAME,
        )
    }

    singleOf(::CurrentPlaylistRepositoryImpl) bind CurrentPlaylistRepository::class
}