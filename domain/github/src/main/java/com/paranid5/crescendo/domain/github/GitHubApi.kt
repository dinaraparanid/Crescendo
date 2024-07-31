package com.paranid5.crescendo.domain.github

import com.paranid5.crescendo.domain.github.dto.Release

interface GitHubApi {
    suspend fun checkForUpdates(): Release?
}
