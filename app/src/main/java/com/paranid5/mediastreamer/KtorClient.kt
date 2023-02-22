package com.paranid5.mediastreamer

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import java.io.File

fun KtorClient() = HttpClient(Android) {
    install(Logging) {
        logger = Logger.ANDROID
        level = LogLevel.ALL
    }
}

suspend inline fun HttpClient.downloadFile(
    url: String,
    storeFile: File
): HttpStatusCode {
    val response = get(url)
    val status = response.status

    if (status.isSuccess()) {
        val channel = response.body<ByteReadChannel>()

        while (!channel.isClosedForRead) {
            val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())

            while (packet.isNotEmpty)
                storeFile.appendBytes(packet.readBytes())
        }
    }

    return status
}