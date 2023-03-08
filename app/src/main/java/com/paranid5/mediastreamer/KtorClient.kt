package com.paranid5.mediastreamer

import android.util.Log
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

suspend inline fun HttpClient.downloadFile(fileUrl: String, storeFile: File): HttpStatusCode {
    Log.d("Ktor Client", "Downloading $fileUrl")

    val status = prepareGet(fileUrl).execute { response ->
        val status = response.status

        if (status.isSuccess()) {
            val channel = response.body<ByteReadChannel>()

            while (!channel.isClosedForRead) {
                val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())

                while (packet.isNotEmpty) {
                    val bytes = packet.readBytes()
                    storeFile.appendBytes(bytes)

                    // TODO: Send number of read bytes to channel for notification
                    Log.d(
                        "Ktor Client",
                        "Read ${bytes.size} bytes from ${response.contentLength()}"
                    )
                }
            }
        }

        status
    }

    Log.d("Ktor Client", "Downloaded")

    return status
}