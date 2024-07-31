package com.paranid5.crescendo.data.web

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.paranid5.crescendo.domain.web.OpenBrowserUseCase

internal class OpenBrowserUseCaseImpl(private val context: Context) : OpenBrowserUseCase {
    override fun openBrowser(url: String): Boolean =
        runCatching {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(url)
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
        }.fold(onSuccess = { true }, onFailure = { false })
}
