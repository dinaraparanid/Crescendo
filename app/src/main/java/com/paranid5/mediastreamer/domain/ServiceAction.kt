package com.paranid5.mediastreamer.domain

import android.app.Service

context(Service)
interface ServiceAction {
    val requestCode: Int
    val playbackAction: String
}