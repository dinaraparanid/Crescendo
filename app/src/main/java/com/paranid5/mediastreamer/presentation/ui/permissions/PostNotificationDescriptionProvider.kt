package com.paranid5.mediastreamer.presentation.ui.permissions

import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
data class PostNotificationDescriptionProvider(override val description: String) :
    PermissionDescriptionProvider