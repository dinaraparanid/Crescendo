package com.paranid5.crescendo.data.github

internal class GitHubApiUrlBuilder {
    companion object {
        private const val BaseUrl = "https://api.github.com"
    }

    fun buildAppLatestReleaseUrl() =
        "$BaseUrl/repos/dinaraparanid/Crescendo/releases"
}
