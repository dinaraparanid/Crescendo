package com.paranid5.crescendo.services.stream_service.managers

import com.paranid5.crescendo.STREAM_SERVICE_CONNECTION
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

interface ConnectionManager {
    var startId: Int
    var isConnected: Boolean
}

class ConnectionManagerImpl : ConnectionManager, KoinComponent {
    override var startId = 0

    private val isConnectedState by inject<MutableStateFlow<Boolean>>(
        named(STREAM_SERVICE_CONNECTION)
    )

    override var isConnected
        get() = isConnectedState.value
        set(value) = isConnectedState.update { value }
}

fun ConnectionManager.connect(startId: Int) {
    this.startId = startId
    isConnected = true
}

fun ConnectionManager.disconnect() {
    isConnected = false
}