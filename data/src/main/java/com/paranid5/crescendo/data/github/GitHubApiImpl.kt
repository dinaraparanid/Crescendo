package com.paranid5.crescendo.data.github

import arrow.core.Either
import com.paranid5.crescendo.domain.github.GitHubApi
import com.paranid5.crescendo.domain.github.dto.Release
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class GitHubApiImpl(
    private val ktorClient: HttpClient,
    private val urlBuilder: GitHubApiUrlBuilder,
) : GitHubApi {
    companion object {
        private const val CurrentVersion = "V0.4.0.0"
    }

    override suspend fun checkForUpdates() =
        getLatestRelease().getOrNull()?.takeIf { it.tagName > CurrentVersion }

    private suspend inline fun getLatestRelease() = Either.catch {
        withContext(Dispatchers.IO) {
            ktorClient.get(urlBuilder.buildAppLatestReleaseUrl())
                .body<List<Release>>()
                .first()
        }
    }
}
