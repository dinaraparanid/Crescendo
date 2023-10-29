package com.paranid5.crescendo.domain.ktor_client.youtube

import android.content.Context
import com.evgenii.jsevaluator.JsEvaluator
import com.evgenii.jsevaluator.interfaces.JsCallback
import com.paranid5.crescendo.domain.utils.extensions.receiveTimeout
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLDecoder
import java.util.SortedMap
import java.util.regex.Pattern
import kotlin.time.Duration.Companion.seconds

private val youtubeUrlRegex = Regex(
    "https://((www\\.youtube\\.com/((watch\\?v=)|(live/)))|(youtu\\.be/))(\\S{11}).*"
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

@Suppress("LongLine")
private val patSignatureDecFunction = Regex(
    "(?:\\b|[^a-zA-Z0-9$])([a-zA-Z0-9$]{1,4})\\s*=\\s*function\\(\\s*a\\s*\\)\\s*\\{\\s*a\\s*=\\s*a\\.split\\(\\s*\"\"\\s*\\)"
)

@Suppress("IncorrectFormatting", "LongLine")
private val formatMapState = MutableStateFlow(
    hashMapOf(
        // Video and Audio
        17 to Format(itag = 17, ext = "3gp", height = 144, videoCodec = Format.VCodec.MPEG4, audioCodec = Format.ACodec.AAC, audioBitrate = 24, isDashContainer = false),
        36 to Format(itag = 36, ext = "3gp", height = 240, videoCodec = Format.VCodec.MPEG4, audioCodec = Format.ACodec.AAC, audioBitrate = 32, isDashContainer = false),
        5 to Format(itag = 5, ext = "flv", height = 240, videoCodec = Format.VCodec.H263, audioCodec = Format.ACodec.MP3, audioBitrate = 64, isDashContainer = false),
        43 to Format(itag = 43, ext = "webm", height = 360, videoCodec = Format.VCodec.VP8, audioCodec = Format.ACodec.VORBIS, audioBitrate = 128, isDashContainer = false),
        18 to Format(itag = 18, ext = "mp4", height = 360, videoCodec = Format.VCodec.H264, audioCodec = Format.ACodec.AAC, audioBitrate = 96, isDashContainer = false),
        22 to Format(itag = 22, ext = "mp4", height = 720, videoCodec = Format.VCodec.H264, audioCodec = Format.ACodec.AAC, audioBitrate = 192, isDashContainer = false),

        // Dash Video
        160 to Format(itag = 160, ext = "mp4", height = 144, videoCodec = Format.VCodec.H264, audioCodec = Format.ACodec.NONE, isDashContainer = true),
        133 to Format(itag = 133, ext = "mp4", height = 240, videoCodec = Format.VCodec.H264, audioCodec = Format.ACodec.NONE, isDashContainer = true),
        134 to Format(itag = 134, ext = "mp4", height = 360, videoCodec = Format.VCodec.H264, audioCodec = Format.ACodec.NONE, isDashContainer = true),
        135 to Format(itag = 135, ext = "mp4", height = 480, videoCodec = Format.VCodec.H264, audioCodec = Format.ACodec.NONE, isDashContainer = true),
        136 to Format(itag = 136, ext = "mp4", height = 720, videoCodec = Format.VCodec.H264, audioCodec = Format.ACodec.NONE, isDashContainer = true),
        137 to Format(itag = 137, ext = "mp4", height = 1080, videoCodec = Format.VCodec.H264, audioCodec = Format.ACodec.NONE, isDashContainer = true),
        264 to Format(itag = 264, ext = "mp4", height = 1440, videoCodec = Format.VCodec.H264, audioCodec = Format.ACodec.NONE, isDashContainer = true),
        266 to Format(itag = 266, ext = "mp4", height = 2160, videoCodec = Format.VCodec.H264, audioCodec = Format.ACodec.NONE, isDashContainer = true),

        298 to Format(itag = 298, ext = "mp4", height = 720, videoCodec = Format.VCodec.H264, fps = 60, audioCodec = Format.ACodec.NONE, isDashContainer = true),
        299 to Format(itag = 299, ext = "mp4", height = 1080, videoCodec = Format.VCodec.H264, fps = 60, audioCodec = Format.ACodec.NONE, isDashContainer = true),

        // Dash Audio
        140 to Format(itag = 140, ext = "m4a", videoCodec = Format.VCodec.NONE, audioCodec = Format.ACodec.AAC, audioBitrate = 128, isDashContainer = true),
        141 to Format(itag = 141, ext = "m4a", videoCodec = Format.VCodec.NONE, audioCodec = Format.ACodec.AAC, audioBitrate = 256, isDashContainer = true),
        256 to Format(itag = 256, ext = "m4a", videoCodec = Format.VCodec.NONE, audioCodec = Format.ACodec.AAC, audioBitrate = 192, isDashContainer = true),
        258 to Format(itag = 258, ext = "m4a", videoCodec = Format.VCodec.NONE, audioCodec = Format.ACodec.AAC, audioBitrate = 384, isDashContainer = true),

        // WEBM Dash Video
        278 to Format(itag = 278, ext = "webm", height = 144, videoCodec = Format.VCodec.VP9, audioCodec = Format.ACodec.NONE, isDashContainer = true),
        242 to Format(itag = 242, ext = "webm", height = 240, videoCodec = Format.VCodec.VP9, audioCodec = Format.ACodec.NONE, isDashContainer = true),
        243 to Format(itag = 243, ext = "webm", height = 360, videoCodec = Format.VCodec.VP9, audioCodec = Format.ACodec.NONE, isDashContainer = true),
        244 to Format(itag = 244, ext = "webm", height = 480, videoCodec = Format.VCodec.VP9, audioCodec = Format.ACodec.NONE, isDashContainer = true),
        247 to Format(itag = 247, ext = "webm", height = 720, videoCodec = Format.VCodec.VP9, audioCodec = Format.ACodec.NONE, isDashContainer = true),
        248 to Format(itag = 248, ext = "webm", height = 1080, videoCodec = Format.VCodec.VP9, audioCodec = Format.ACodec.NONE, isDashContainer = true),
        271 to Format(itag = 271, ext = "webm", height = 1440, videoCodec = Format.VCodec.VP9, audioCodec = Format.ACodec.NONE, isDashContainer = true),
        313 to Format(itag = 313, ext = "webm", height = 2160, videoCodec = Format.VCodec.VP9, audioCodec = Format.ACodec.NONE, isDashContainer = true),

        302 to Format(itag = 302, ext = "webm", height = 720, videoCodec = Format.VCodec.VP9, fps = 60, audioCodec = Format.ACodec.NONE, isDashContainer = true),
        308 to Format(itag = 308, ext = "webm", height = 1440, videoCodec = Format.VCodec.VP9, fps = 60, audioCodec = Format.ACodec.NONE, isDashContainer = true),
        303 to Format(itag = 303, ext = "webm", height = 1080, videoCodec = Format.VCodec.VP9, fps = 60, audioCodec = Format.ACodec.NONE, isDashContainer = true),
        315 to Format(itag = 315, ext = "webm", height = 2160, videoCodec = Format.VCodec.VP9, fps = 60, audioCodec = Format.ACodec.NONE, isDashContainer = true),

        // WEBM Dash Audio
        171 to Format(itag = 171, ext = "webm", videoCodec = Format.VCodec.NONE, audioCodec = Format.ACodec.VORBIS, audioBitrate = 128, isDashContainer = true),
        249 to Format(itag = 249, ext = "webm", videoCodec = Format.VCodec.NONE, audioCodec = Format.ACodec.OPUS, audioBitrate = 48, isDashContainer = true),
        250 to Format(itag = 250, ext = "webm", videoCodec = Format.VCodec.NONE, audioCodec = Format.ACodec.OPUS, audioBitrate = 64, isDashContainer = true),
        251 to Format(itag = 251, ext = "webm", videoCodec = Format.VCodec.NONE, audioCodec = Format.ACodec.OPUS, audioBitrate = 160, isDashContainer = true),

        // HLS Live Stream
        91 to Format(itag = 91, ext = "mp4", height = 144, videoCodec = Format.VCodec.H264, audioCodec = Format.ACodec.AAC, audioBitrate = 48, isDashContainer = false, isHlsContent = true),
        92 to Format(itag = 92, ext = "mp4", height = 240, videoCodec = Format.VCodec.H264, audioCodec = Format.ACodec.AAC, audioBitrate = 48, isDashContainer = false, isHlsContent = true),
        93 to Format(itag = 93, ext = "mp4", height = 360, videoCodec = Format.VCodec.H264, audioCodec = Format.ACodec.AAC, audioBitrate = 128, isDashContainer = false, isHlsContent = true),
        94 to Format(itag = 94, ext = "mp4", height = 480, videoCodec = Format.VCodec.H264, audioCodec = Format.ACodec.AAC, audioBitrate = 128, isDashContainer = false, isHlsContent = true),
        95 to Format(itag = 95, ext = "mp4", height = 720, videoCodec = Format.VCodec.H264, audioCodec = Format.ACodec.AAC, audioBitrate = 256, isDashContainer = false, isHlsContent = true),
        96 to Format(itag = 96, ext = "mp4", height = 1080, videoCodec = Format.VCodec.H264, audioCodec = Format.ACodec.AAC, audioBitrate = 256, isDashContainer = false, isHlsContent = true),
    )
)

suspend fun HttpClient.extractYtFilesWithMeta(
    context: Context,
    ytUrl: String
) = when (val videoId = findVideoId(ytUrl)) {
    null -> YtFailure(WrongYtUrlFormatException())
    else -> Result.success(getStreamData(context, videoId))
}

private fun findVideoId(ytUrl: String): String? {
    val pageMatch = youtubeUrlRegex.find(ytUrl)
    if (pageMatch != null) return pageMatch.groupValues[7]
    return if (ytUrl.matches("\\p{Graph}+?".toRegex())) ytUrl else null
}

private suspend inline fun HttpClient.getYouTubePageHtml(videoId: String) =
    get("https://youtube.com/watch?v=$videoId").bodyAsText()

private suspend inline fun HttpClient.getStreamData(
    context: Context,
    videoId: String,
): StreamData {
    val encSignatures = sortedMapOf<Int, String>()
    val ytFiles = sortedMapOf<Int, YtFile>()

    val pageHtml = getYouTubePageHtml(videoId)
    val (livestreams, videoMeta) = parseVideoPage(pageHtml, ytFiles, encSignatures)

    if (encSignatures.isNotEmpty())
        decodeYtFileUrls(context, pageHtml, ytFiles, encSignatures)

    return StreamData(ytFiles, livestreams, videoMeta)
}

private fun parseVideoPage(
    pageHtml: String,
    ytFiles: SortedMap<Int, YtFile>,
    encSignatures: SortedMap<Int, String>
): Pair<Result<LiveStreamManifests>, Result<VideoMeta>> {
    val playerResponseJson = patPlayerResponse.find(pageHtml) ?: return run {
        val e = YtPlayerResponseNotFoundException()
        Pair(YtFailure(e), YtFailure(e))
    }

    val ytPlayerResponse = playerResponseJson.groupValues
        .getOrNull(1)
        ?.let { JSONObject(it) }
        ?: return run {
            val e = YtPlayerResponseStructureChangedException()
            Pair(YtFailure(e), YtFailure(e))
        }

    val streamingData = when {
        ytPlayerResponse.has("streamingData") ->
            ytPlayerResponse.getJSONObject("streamingData")

        else -> {
            val e = YtPlayerResponseStructureChangedException()
            return Pair(YtFailure(e), YtFailure(e))
        }
    }

    if (streamingData.has("formats"))
        streamingData
            .getJSONArray("formats")
            .storeFormats(
                ytFiles = ytFiles,
                encSignatures = encSignatures
            )

    if (streamingData.has("adaptiveFormats"))
        streamingData
            .getJSONArray("adaptiveFormats")
            .storeFormats(
                ytFiles = ytFiles,
                encSignatures = encSignatures
            )

    val videoMeta = when {
        ytPlayerResponse.has("videoDetails") ->
            ytPlayerResponse.getVideoMeta()

        else -> YtFailure(YtPlayerResponseStructureChangedException())
    }

    return Result.success(streamingData.getLiveStreamManifests()) to videoMeta
}

private fun JSONObject.getVideoMeta(name: String = "videoDetails") =
    getJSONObject(name).let { videoDetails ->
        runCatching {
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

private fun JSONObject.getLiveStreamManifests(
    dashName: String = "dashManifestUrl",
    hlsName: String = "hlsManifestUrl"
) = LiveStreamManifests(
    dashManifestUrl = when {
        has(dashName) -> getString(dashName)
        else -> null
    },
    hlsManifestUrl = when {
        has(hlsName) -> getString(hlsName)
        else -> null
    }
)

private suspend inline fun HttpClient.decodeYtFileUrls(
    context: Context,
    pageHtml: String,
    ytFiles: SortedMap<Int, YtFile>,
    encSignatures: SortedMap<Int, String>
): Result<Unit> {
    val decipherFunDataRes = Result.success<DecipherFunctionData?>(null)

    var (decipherJsFileName, decipherFunctionName, decipherFunctions) =
        when (val res = decipherFunDataRes.exceptionOrNull()) {
            null -> decipherFunDataRes.getOrNull() ?: DecipherFunctionData()
            else -> return Result.failure(res)
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
            decipherFunctionName = decipherFunctionName,
            decipherFunctions = decipherFunctions,
        )
    )

    var signatureRes = Result.failure<String>(Exception())
    val decipheredSignatureChannel = Channel<Result<String>>(Channel.CONFLATED)

    if (
        decipherSignature(
            context,
            decipherFunctionDataState,
            encSignatures,
            decipheredSignatureChannel
        )
    )
        signatureRes = decipheredSignatureChannel
            .receiveTimeout(7.seconds)
            .getOrDefault(Result.failure(Exception()))

    if (signatureRes.isFailure)
        return Result.failure(signatureRes.exceptionOrNull()!!)

    val sigs = signatureRes
        .getOrNull()!!
        .split("\n")
        .asSequence()
        .filter(String::isNotEmpty)

    encSignatures.keys
        .asSequence()
        .zip(sigs)
        .map { (itag, sig) -> itag to "${ytFiles[itag]!!.url!!}&sig=${sig}" }
        .map { (itag, url) -> itag to YtFile(formatMapState.value[itag], url) }
        .forEach { (itag, file) -> ytFiles[itag] = file }

    return Result.success(Unit)
}

private inline val JSONArray.formatObjectsAndItags
    get() = (0 until length())
        .asSequence()
        .map(this::getJSONObject)
        .filter {
            val type = it.optString("type")
            type.isEmpty() || type != "FORMAT_STREAM_TYPE_OTF"
        }
        .map { it to it.getInt("itag") }
        .filter { (_, itag) -> formatMapState.value[itag] != null }

private fun JSONArray.storeFormats(
    ytFiles: SortedMap<Int, YtFile>,
    encSignatures: SortedMap<Int, String>
) = formatObjectsAndItags.forEach { (format, itag) ->
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
    encSignatures: SortedMap<Int, String>,
    decipheredSignatureChannel: Channel<Result<String>>
) = coroutineScope {
    fun decipherFunctionData() = decipherFunctionDataState.value
    fun decipherFunctionName() = decipherFunctionData().decipherFunctionName
    fun decipherJsFileName() = decipherFunctionData().decipherJsFileName
    fun decipherFunctions() = decipherFunctionData().decipherFunctions

    if (decipherFunctionName() != null && decipherFunctions() != null) {
        decipherViaWebViewAsync(
            context,
            encSignatures,
            decipherFunctionData(),
            decipheredSignatureChannel
        ).join()

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

    decipherViaWebViewAsync(
        context = context,
        encSignatures = encSignatures,
        decipherFunctionData = decipherFunctionData(),
        decipheredSignatureChannel = decipheredSignatureChannel
    ).join()

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

    (startIndex until jsFile.length).forEach { i ->
        if (braces == 0 && startIndex + 5 < i) {
            mainDecipherFunction += "${jsFile.substring(startIndex, i)};"
            return Result.success(mainDecipherFunction)
        }

        when (jsFile[i]) {
            '{' -> ++braces
            '}' -> --braces
        }
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
        initialBraces = 1,
        definition = { "var $it={" },
        breakCondition = { braces, _, _ -> braces == 0 }
    )

    // Search for functions
    parseMainFunctionExtraDef(
        mainDecipherFunction = mainDecipherFunction,
        jsFile = jsFile,
        decipherFunctionDataState = decipherFunctionDataState,
        pattern = patFunction,
        initialBraces = 0,
        definition = { "function $it(" },
        breakCondition = { braces, startIndex, i -> braces == 0 && startIndex + 5 < i }
    )
}

private inline fun parseMainFunctionExtraDef(
    mainDecipherFunction: String,
    jsFile: String,
    decipherFunctionDataState: MutableStateFlow<DecipherFunctionData>,
    pattern: Regex,
    initialBraces: Int,
    definition: (matched: String) -> String,
    breakCondition: (braces: Int, startIndex: Int, i: Int) -> Boolean
) {
    var match = pattern.find(mainDecipherFunction) ?: return

    while (true) {
        match = match.next() ?: return
        val def = definition(match.groupValues[2])

        if (decipherFunctionDataState.value.decipherFunctions?.contains(def) == true)
            continue

        val startIndex = jsFile.indexOf(def) + def.length
        var braces = initialBraces

        for (i in startIndex until jsFile.length) {
            if (breakCondition(braces, startIndex, i)) {
                decipherFunctionDataState.update {
                    it.copy(
                        decipherFunctions =
                        "${it.decipherFunctions}$def${jsFile.substring(startIndex, i)};"
                    )
                }

                break
            }

            when (jsFile[i]) {
                '{' -> ++braces
                '}' -> --braces
            }
        }
    }
}

context(CoroutineScope)
private fun decipherViaWebViewAsync(
    context: Context,
    encSignatures: SortedMap<Int, String>,
    decipherFunctionData: DecipherFunctionData,
    decipheredSignatureChannel: Channel<Result<String>>
): Job {
    val stb = StringBuilder("${decipherFunctionData.decipherFunctions} function decipher(){return ")

    encSignatures.keys.toList().run {
        dropLast(1).forEach { key ->
            val sig = encSignatures[key]
            stb.append("${decipherFunctionData.decipherFunctionName}('$sig')+\"\\n\"+")
        }

        stb.append("${decipherFunctionData.decipherFunctionName}('${encSignatures[last()]}')")
    }

    stb.append("};decipher();")

    println(stb.toString())

    return launch(Dispatchers.Main) {
        JsEvaluator(context).evaluate(stb.toString(), object : JsCallback {
            override fun onResult(result: String) {
                decipheredSignatureChannel.trySend(Result.success(result))
            }

            override fun onError(errorMessage: String?) {
                decipheredSignatureChannel.trySend(Result.failure(YtException(errorMessage)))
            }
        })
    }
}