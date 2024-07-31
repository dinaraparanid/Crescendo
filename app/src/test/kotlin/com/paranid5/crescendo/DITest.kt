package com.paranid5.crescendo

import androidx.lifecycle.SavedStateHandle
import com.paranid5.crescendo.di.appModule
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import org.koin.test.verify.verify
import kotlin.test.Test

class DITest {
    @Test
    fun koinGraphTest() {
        appModule.verify(
            extraTypes = listOf(
                Unit::class,
                Set::class,
                Boolean::class,
                kotlin.Function0::class,
                android.content.Context::class,
                android.app.Application::class,
                kotlinx.serialization.StringFormat::class,
                HttpClientEngine::class,
                HttpClientConfig::class,
                SavedStateHandle::class,
            )
        )
    }
}