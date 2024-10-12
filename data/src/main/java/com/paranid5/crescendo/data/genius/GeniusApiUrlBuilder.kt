package com.paranid5.crescendo.data.genius

internal class GeniusApiUrlBuilder {
    companion object {
        private const val BaseUrl = "https://api.genius.com"
    }

    fun buildSearchUrl() = "$BaseUrl/search"
    fun buildSongsUrl(songId: Long) = "$BaseUrl/songs/$songId"
}
