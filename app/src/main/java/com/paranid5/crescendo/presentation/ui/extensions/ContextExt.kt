package com.paranid5.crescendo.presentation.ui.extensions

import android.content.Context
import android.content.Intent
import com.paranid5.crescendo.presentation.main.MainActivity

fun Context.openMainActivity() =
    startActivity(Intent(this, MainActivity::class.java))