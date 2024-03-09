package com.paranid5.crescendo.data.ktor_client.github

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

private const val CUR_VERSION = "V0.3.0.2"

private suspend inline fun HttpClient.getLatestRelease() =
    get("https://api.github.com/repos/dinaraparanid/MediaStreamer/releases")
        .body<List<Release>>()
        .first()

private suspend inline fun HttpClient.getLatestReleaseCatching() =
    kotlin.runCatching { getLatestRelease() }

private suspend inline fun HttpClient.getLatestReleaseAsync() = coroutineScope {
    async(Dispatchers.IO) { getLatestReleaseCatching() }
}

suspend fun HttpClient.checkForUpdates() =
    getLatestReleaseAsync().await().getOrNull()?.takeIf { it.tagName > CUR_VERSION }