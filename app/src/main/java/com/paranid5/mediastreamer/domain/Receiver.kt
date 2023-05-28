package com.paranid5.mediastreamer.domain

interface Receiver {
    fun registerReceivers()
    fun unregisterReceivers()
}