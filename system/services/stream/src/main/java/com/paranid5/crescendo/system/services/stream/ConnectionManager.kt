package com.paranid5.crescendo.system.services.stream

import com.paranid5.crescendo.core.impl.di.STREAM_SERVICE_CONNECTION
import com.paranid5.system.services.common.ConnectionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class ConnectionManagerImpl : ConnectionManager, KoinComponent {
    override var startId = 0

    private val isConnectedState by inject<MutableStateFlow<Boolean>>(
        named(STREAM_SERVICE_CONNECTION)
    )

    override var isConnected
        get() = isConnectedState.value
        set(value) = isConnectedState.update { value }
}