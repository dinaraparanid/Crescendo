package com.paranid5.mediastreamer.domain

import android.content.Context
import android.content.Intent

interface Receiver {
    fun registerReceivers()
    fun unregisterReceivers()
}

context(Receiver)
fun <R : Context> R.sendBroadcast(action: String) = sendBroadcast(Intent(action))