package com.paranid5.crescendo.domain.ktor_client.youtube

import android.content.Context
import com.evgenii.jsevaluator.JsEvaluator
import com.evgenii.jsevaluator.interfaces.JsCallback
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.URLDecoder
import java.util.regex.Pattern

private const val CACHE_FILE_NAME = "decipher_js_function"

private val patYouTubePageLink = Regex(
    "(http|https)://(www\\.|m.|)youtube\\.com/watch\\?v=(.+?)( |\\z|&)"
)

private val patYouTubeShortLink = Regex(
    "(http|https)://(www\\.|)youtu.be/(.+?)( |\\z|&)"
)

private val patPlayerResponse = Regex(
    "var ytInitialPlayerResponse\\s*=\\s*(\\{.+?\\})\\s*;"
)

private val patSigEncUrl = Regex("url=(.+?)(\\u0026|$)")

private val patSignature = Regex("s=(.+?)(\\u0026|$)")

private val patVariableFunction = Regex(
    "([{; =])([a-zA-Z$][a-zA-Z0-9$]{0,2})\\.([a-zA-Z$][a-zA-Z0-9$]{0,2})\\("
)

private val patFunction = Regex(
    "([{; =])([a-zA-Z$][a-zA-Z0-9$]{0,2})\\("
)

private val patDecryptionJsFile = Regex(
    "\\\\/s\\\\/player\\\\/([^\"]+?)\\.js"
)

private val patDecryptionJsFileWithoutSlash = Regex("/s/player/([^\"]+?).js")

private val patSignatureDecFunction = Regex(
    "(?:\\b|[^a-zA-Z0-9$])([a-zA-Z0-9$]{1,4})\\s*=\\s*function\\(\\s*a\\s*\\)\\s*\\{\\s*a\\s*=\\s*a\\.split\\(\\s*\"\"\\s*\\)"
)

private val formatMapState = MutableStateFlow(hashMapOf<Int, Format>())

internal suspend inline fun HttpClient.extractYtFilesWithMeta(
    context: Context,
    vararg params: String?
): Result<Pair<HashMap<Int, YtFile>, VideoMeta>> {
    val ytUrl = params[0] ?: return Result.failure(RuntimeException("URL is not passed"))
    val videoId = findVideoId(ytUrl)

    return when (videoId) {
        null -> YtFailure(WrongYtUrlFormatException())
        else -> getStreamUrls(context, ytUrl)
    }
}

private fun findVideoId(ytUrl: String): String? {
    val pageMatch = patYouTubePageLink.find(ytUrl)
    if (pageMatch != null) return pageMatch.groupValues[3]

    val shortMatch = patYouTubeShortLink.find(ytUrl)
    if (shortMatch != null) return shortMatch.groupValues[3]

    return if (ytUrl.matches("\\p{Graph}+?".toRegex())) ytUrl else null
}

private suspend inline fun HttpClient.getYouTubePageHtml(videoId: String) =
    get("https://youtube.com/watch?v=$videoId").bodyAsText()

private suspend inline fun HttpClient.getStreamUrls(
    context: Context,
    videoId: String,
): Result<Pair<HashMap<Int, YtFile>, VideoMeta>> {
    val pageHtml = getYouTubePageHtml(videoId)
    val encSignatures = hashMapOf<Int, String>()
    val ytFiles = hashMapOf<Int, YtFile>()

    val videoMeta = parseVideoPage(pageHtml, ytFiles, encSignatures).let {
        when (val meta = it.getOrNull()) {
            null -> return Result.failure(it.exceptionOrNull()!!)
            else -> meta
        }
    }

    if (encSignatures.isNotEmpty()) {
        val decipherFunDataRes = readDecipherFunctionFromCache(
            cacheDirPath = context.cacheDir.absolutePath
        )

        var (decipherJsFileName, decipherFunctions, decipherFunctionName) =
            when (val res = decipherFunDataRes.getOrNull()) {
                null -> return Result.failure(decipherFunDataRes.exceptionOrNull()!!)
                else -> res
            }

        val decryptJsFileMatch = patDecryptionJsFile.find(pageHtml)
            ?: patDecryptionJsFileWithoutSlash.find(pageHtml)

        if (decryptJsFileMatch != null) {
            val curJsFileName = decryptJsFileMatch.groupValues[0].replace("\\/", "/")

            if (decipherJsFileName == null || decipherJsFileName != curJsFileName) {
                decipherFunctions = null
                decipherFunctionName = null
            }

            decipherJsFileName = curJsFileName
        }

        val decipherFunctionDataState = MutableStateFlow(
            DecipherFunctionData(
                decipherJsFileName = decipherJsFileName,
                decipherFunctions = decipherFunctions,
                decipherFunctionName = decipherFunctionName
            )
        )

        var signatureRes = Result.failure<String>(Exception())
        val decipheredSignatureChannel = Channel<Result<String>>()

        if (decipherSignature(context, decipherFunctionDataState, encSignatures))
            signatureRes = decipheredSignatureChannel.receive() // TODO: 7 sec timeout

        if (signatureRes.isFailure)
            return Result.failure(signatureRes.exceptionOrNull()!!)

        val signature = signatureRes.getOrNull()!!

        val sigs = signature
            .split("\n".toRegex())
            .dropLastWhile { it.isEmpty() }

        var i = 0

        while (i < encSignatures.size && i < sigs.size) {
            val key = encSignatures[i]!!.toInt()
            val url = "${ytFiles[key]!!.url!!}&sig=${sigs[i]}"
            val newFile = YtFile(formatMapState.value[key], url)
            ytFiles[key] = newFile
            i++
        }
    }

    if (ytFiles.size == 0)
        return YtFailure(YtFilesNotFoundException())

    return Result.success(ytFiles to videoMeta)
}

private fun parseVideoPage(
    pageHtml: String,
    ytFiles: HashMap<Int, YtFile>,
    encSignatures: HashMap<Int, String>
): Result<VideoMeta> {
    val playerResponseJson = patPlayerResponse.find(pageHtml)
        ?: return YtFailure(YtPlayerResponseNotFoundException())

    val ytPlayerResponse = playerResponseJson.groupValues
        .getOrNull(1)
        ?.let { JSONObject(it) }
        ?: return YtFailure(YtPlayerResponseStructureChangedException())

    val streamingData = when {
        ytPlayerResponse.has("streamingData") ->
            ytPlayerResponse.getJSONObject("streamingData")

        else -> return YtFailure(YtPlayerResponseStructureChangedException())
    }

    if (streamingData.has("formats"))
        parseFormats(
            formats = streamingData.getJSONArray("formats"),
            ytFiles = ytFiles,
            encSignatures = encSignatures
        )

    if (streamingData.has("adaptiveFormats"))
        parseFormats(
            formats = streamingData.getJSONArray("adaptiveFormats"),
            ytFiles = ytFiles,
            encSignatures = encSignatures
        )

    val videoDetails = when {
        ytPlayerResponse.has("videoDetails") ->
            ytPlayerResponse.getJSONObject("videoDetails")

        else -> return YtFailure(YtPlayerResponseStructureChangedException())
    }

    return runCatching {
        VideoMeta(
            videoDetails.getString("videoId"),
            videoDetails.getString("title"),
            videoDetails.getString("author"),
            videoDetails.getString("channelId"),
            videoDetails.getString("lengthSeconds").toLong(),
            videoDetails.getString("viewCount").toLong(),
            videoDetails.getBoolean("isLiveContent"),
            videoDetails.getString("shortDescription")
        )
    }
}

private inline val JSONArray.formatObjectsAndItags
    get() = (0 until length())
        .asSequence()
        .map(this::getJSONObject)
        .filter { it.optString("type") != "FORMAT_STREAM_TYPE_OTF" }
        .map { it to it.getInt("itag") }
        .filter { (_, itag) -> formatMapState.value[itag] != null }

private fun parseFormats(
    formats: JSONArray,
    ytFiles: HashMap<Int, YtFile>,
    encSignatures: HashMap<Int, String>
) = formats.formatObjectsAndItags.forEach { (format, itag) ->
    when {
        format.has("url") -> {
            val url = format.getString("url").replace("\\u0026", "&")
            ytFiles[itag] = YtFile(formatMapState.value[itag], url)
        }

        format.has("signatureCipher") -> {
            val encUrlMatch = patSigEncUrl.find(format.getString("signatureCipher"))
            val sigMatch = patSignature.find(format.getString("signatureCipher"))

            if (encUrlMatch != null && sigMatch != null) {
                val url = URLDecoder.decode(encUrlMatch.groupValues[1], "UTF-8")
                val signature = URLDecoder.decode(sigMatch.groupValues[1], "UTF-8")
                ytFiles[itag] = YtFile(formatMapState.value[itag], url)
                encSignatures[itag] = signature
            }
        }
    }
}

private suspend fun HttpClient.decipherSignature(
    context: Context,
    decipherFunctionDataState: MutableStateFlow<DecipherFunctionData>,
    encSignatures: HashMap<Int, String>
) = coroutineScope {
    fun decipherFunctionData() = decipherFunctionDataState.value
    fun decipherFunctionName() = decipherFunctionData().decipherFunctionName
    fun decipherJsFileName() = decipherFunctionData().decipherJsFileName
    fun decipherFunctions() = decipherFunctionData().decipherFunctions

    val decipheredSignatureChannel = Channel<Result<String>>()

    if (decipherFunctionName() == null || decipherFunctions() == null) {
        decipherViaWebView(
            context,
            encSignatures,
            decipherFunctionData(),
            decipheredSignatureChannel
        )

        return@coroutineScope true
    }

    val jsFileUrl = "https://youtube.com${decipherJsFileName()}"
    val jsFile = get(jsFileUrl).bodyAsText()

    val funcNameMatch = patSignatureDecFunction.find(jsFile)
        ?: return@coroutineScope false

    decipherFunctionDataState.update {
        it.copy(decipherFunctionName = funcNameMatch.groupValues[1])
    }

    val mainDecipherFunction = parseMainDecipherFunction(
        jsFile = jsFile,
        decipherFunctionName = decipherFunctionName()!!
    ).getOrNull() ?: return@coroutineScope false

    decipherFunctionDataState.update {
        it.copy(decipherFunctions = mainDecipherFunction)
    }

    parseMainFunctionExtra(
        mainDecipherFunction = mainDecipherFunction,
        jsFile = jsFile,
        decipherFunctionDataState = decipherFunctionDataState
    )

    decipherViaWebView(
        context = context,
        encSignatures = encSignatures,
        decipherFunctionData = decipherFunctionData(),
        decipheredSignatureChannel = decipheredSignatureChannel
    )

    writeDecipherFunctionToCache(
        cacheDirPath = context.cacheDir.absolutePath,
        decipherFunctionData = decipherFunctionData(),
    )

    true
}

private fun parseMainDecipherFunction(
    jsFile: String,
    decipherFunctionName: String
): Result<String> {
    val patMainVar = Pattern.compile(
        "(var |\\s|,|;)${decipherFunctionName.replace("$", "\\$")}(=function\\((.{1,3})\\)\\{)"
    )

    var matcher = patMainVar.matcher(jsFile)

    var mainDecipherFunction = when {
        matcher.find() -> "var ${decipherFunctionName}${matcher.group(2)}"

        else -> {
            val patMainFunction = Pattern.compile(
                "function ${decipherFunctionName.replace("$", "\\$")}(\\((.{1,3})\\)\\{)"
            )

            matcher = patMainFunction.matcher(jsFile)

            when {
                matcher.find() -> "function ${decipherFunctionName}${matcher.group(2)}"
                else -> return Result.failure(Exception())
            }
        }
    }

    val startIndex = matcher.end()
    var braces = 1
    var i = startIndex

    while (i < jsFile.length) {
        if (braces == 0 && startIndex + 5 < i) {
            mainDecipherFunction += "${jsFile.substring(startIndex, i)};"
            break
        }

        if (jsFile[i] == '{') braces++ else if (jsFile[i] == '}') braces--
        i++
    }

    return Result.success(mainDecipherFunction)
}

/**
 * Parse the main function for extra functions and variables
 * needed for deciphering
 */

private fun parseMainFunctionExtra(
    mainDecipherFunction: String,
    jsFile: String,
    decipherFunctionDataState: MutableStateFlow<DecipherFunctionData>,
) {
    // Search for variables
    parseMainFunctionExtraDef(
        mainDecipherFunction = mainDecipherFunction,
        jsFile = jsFile,
        decipherFunctionDataState = decipherFunctionDataState,
        pattern = patVariableFunction,
        definition = { "var $it={" }
    )

    // Search for functions
    parseMainFunctionExtraDef(
        mainDecipherFunction = mainDecipherFunction,
        jsFile = jsFile,
        decipherFunctionDataState = decipherFunctionDataState,
        pattern = patFunction,
        definition = { "function $it(" }
    )
}

private inline fun parseMainFunctionExtraDef(
    mainDecipherFunction: String,
    jsFile: String,
    decipherFunctionDataState: MutableStateFlow<DecipherFunctionData>,
    pattern: Regex,
    definition: (matched: String) -> String
) {
    while (true) {
        val varMatch = pattern.find(mainDecipherFunction) ?: return
        val variableDef = definition(varMatch.groupValues[2])

        if (decipherFunctionDataState.value.decipherFunctions?.contains(variableDef) == true)
            continue

        val startIndex = jsFile.indexOf(variableDef) + variableDef.length
        var braces = 1
        var i = startIndex

        while (i < jsFile.length) {
            if (braces == 0) {
                decipherFunctionDataState.update {
                    it.copy(
                        decipherFunctions =
                        it.decipherFunctions + "$variableDef${jsFile.substring(startIndex, i)};"
                    )
                }

                break
            }

            if (jsFile[i] == '{') braces++ else if (jsFile[i] == '}') braces--
            i++
        }
    }
}

private fun readDecipherFunctionFromCache(cacheDirPath: String): Result<DecipherFunctionData?> {
    val cacheFile = File("$cacheDirPath/$CACHE_FILE_NAME")

    return when {
        cacheFile.exists() && cacheFile.hasValidCache -> runCatching {
            cacheFile.bufferedReader().use {
                DecipherFunctionData(
                    decipherJsFileName = it.readLine(),
                    decipherFunctionName = it.readLine(),
                    decipherFunctions = it.readLine()
                )
            }
        }

        else -> Result.success(null)
    }
}

/** The cached functions are valid for 2 weeks */

private inline val File.hasValidCache
    get() = System.currentTimeMillis() - lastModified() < 1209600000

private fun writeDecipherFunctionToCache(
    cacheDirPath: String,
    decipherFunctionData: DecipherFunctionData
) {
    val cacheFile = File("$cacheDirPath/$CACHE_FILE_NAME").also {
        if (!it.exists()) it.createNewFile()
    }

    cacheFile.printWriter().use {
        it.println(decipherFunctionData.decipherJsFileName)
        it.println(decipherFunctionData.decipherFunctionName)
        it.println(decipherFunctionData.decipherFunctions)
    }
}

context(CoroutineScope)
private suspend inline fun decipherViaWebView(
    context: Context,
    encSignatures: HashMap<Int, String>,
    decipherFunctionData: DecipherFunctionData,
    decipheredSignatureChannel: Channel<Result<String>>
) = coroutineScope {
    val stb = StringBuilder("${decipherFunctionData.decipherFunctions} function decipher(){return ")

    encSignatures.forEach { (key, sig) ->
        when {
            key < encSignatures.size - 1 ->
                "${decipherFunctionData.decipherFunctionName}($sig)+\"\\n\"+"

            else -> "${decipherFunctionData.decipherFunctionName}($sig)"
        }
    }

    stb.append("};decipher();")

    JsEvaluator(context).evaluate(stb.toString(), object : JsCallback {
        override fun onResult(result: String) {
            launch { decipheredSignatureChannel.send(Result.success(result)) }
        }

        override fun onError(errorMessage: String?) {
            launch { decipheredSignatureChannel.send(Result.failure(YtException(errorMessage))) }
        }
    })
}