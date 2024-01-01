package com.paranid5.crescendo.services

interface ConnectionManager {
    var startId: Int
    var isConnected: Boolean
}

fun ConnectionManager.connect(startId: Int) {
    this.startId = startId
    isConnected = true
}

fun ConnectionManager.disconnect() {
    isConnected = false
}