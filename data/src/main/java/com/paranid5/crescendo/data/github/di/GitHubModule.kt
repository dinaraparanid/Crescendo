package com.paranid5.crescendo.data.github.di

import com.paranid5.crescendo.data.github.GitHubApiImpl
import com.paranid5.crescendo.data.github.GitHubApiUrlBuilder
import com.paranid5.crescendo.domain.github.GitHubApi
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val gitHubModule = module {
    singleOf(::GitHubApiImpl) bind GitHubApi::class
    singleOf(::GitHubApiUrlBuilder)
}