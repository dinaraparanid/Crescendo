package com.paranid5.crescendo.core.media.images

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Build
import androidx.annotation.DrawableRes
import arrow.core.Either
import coil.executeBlocking
import com.paranid5.crescendo.utils.extensions.toBitmap
import org.jaudiotagger.audio.AudioFileIO
import java.io.File

internal fun Any.getCoilModel(context: Context, size: ImageSize? = null) =
    when (size) {
        null -> getCoilModelBuilder(context).build()

        else -> getCoilModelBuilder(context)
            .size(size.width, size.height)
            .build()
    }

internal suspend fun getBitmapFromModel(
    context: Context,
    model: Any,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = ImageLoaderBuilder(context)
    .build()
    .execute(model.getCoilModel(context, size))
    .drawable!!
    .toResizedBitmap(size)
    .also(bitmapSettings)

internal fun getBitmapFromModelBlocking(
    context: Context,
    model: Any,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = ImageLoaderBuilder(context)
    .build()
    .executeBlocking(model.getCoilModel(context, size))
    .drawable!!
    .toResizedBitmap(size)
    .also(bitmapSettings)

internal suspend fun getBitmapFromResource(
    context: Context,
    @DrawableRes res: Int,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = getBitmapFromModel(context, res, size, bitmapSettings)

internal fun getBitmapFromResourceBlocking(
    context: Context,
    @DrawableRes res: Int,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = getBitmapFromModelBlocking(context, res, size, bitmapSettings)

internal suspend fun getBitmapFromResourceCatching(
    context: Context,
    @DrawableRes res: Int,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = Either.catch {
    getBitmapFromResource(context, res, size, bitmapSettings)
}

internal fun getBitmapFromResourceBlockingCatching(
    context: Context,
    @DrawableRes res: Int,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = Either.catch {
    getBitmapFromResourceBlocking(context, res, size, bitmapSettings)
}

internal suspend fun getBitmapFromUrl(
    context: Context,
    url: String,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = getBitmapFromModel(context, url, size, bitmapSettings)

internal fun getBitmapFromUrlBlocking(
    context: Context,
    url: String,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = getBitmapFromModelBlocking(context, url, size, bitmapSettings)

internal suspend fun getBitmapFromUrlWithPalette(
    context: Context,
    url: String,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = getBitmapFromUrl(context, url, size, bitmapSettings).withPalette

internal fun getBitmapFromUrlWithPaletteBlocking(
    context: Context,
    url: String,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = getBitmapFromUrlBlocking(context, url, size, bitmapSettings).withPalette

internal suspend fun getBitmapFromUrlCatching(
    context: Context,
    url: String,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = Either.catch {
    getBitmapFromUrl(context, url, size, bitmapSettings)
}

internal fun getBitmapFromUrlBlockingCatching(
    context: Context,
    url: String,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = Either.catch {
    getBitmapFromUrlBlocking(context, url, size, bitmapSettings)
}

internal suspend fun getBitmapFromUrlWithPaletteCatching(
    context: Context,
    url: String,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = Either.catch {
    getBitmapFromUrlWithPalette(context, url, size, bitmapSettings)
}

internal fun getBitmapFromUrlWithPaletteBlockingCatching(
    context: Context,
    url: String,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = Either.catch {
    getBitmapFromUrlWithPaletteBlocking(context, url, size, bitmapSettings)
}

internal fun getCoverDataByPath(path: String?): ByteArray? {
    fun legacyCoverDataByPath(path: String) =
        AudioFileIO
            .read(File(path))
            .tagOrCreateAndSetDefault
            ?.firstArtwork
            ?.binaryData

    fun modernCoverDataByPath() =
        MediaMetadataRetriever()
            .apply { setDataSource(path) }
            .embeddedPicture

    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->
            path?.let(::legacyCoverDataByPath)

        else -> modernCoverDataByPath()
    }
}

internal suspend fun getBitmapFromPath(
    context: Context,
    path: String?,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = getCoverDataByPath(path)
    ?.toBitmap()
    ?.let { getBitmapFromModel(context, it, size, bitmapSettings) }
    ?: getThumbnailBitmap(context)

internal fun getBitmapFromPathBlocking(
    context: Context,
    path: String?,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = getCoverDataByPath(path)
    ?.toBitmap()
    ?.let { getBitmapFromModelBlocking(context, it, size, bitmapSettings) }
    ?: getThumbnailBitmapBlocking(context)

internal suspend fun getBitmapFromPathWithPalette(
    context: Context,
    path: String?,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = getBitmapFromPath(context, path, size, bitmapSettings).withPalette

internal fun getBitmapFromPathWithPaletteBlocking(
    context: Context,
    path: String?,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = getBitmapFromPathBlocking(context, path, size, bitmapSettings).withPalette

internal suspend fun getBitmapFromPathCatching(
    context: Context,
    path: String?,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = Either.catch {
    getBitmapFromPath(context, path, size, bitmapSettings)
}

internal fun getBitmapFromPathBlockingCatching(
    context: Context,
    path: String?,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = Either.catch {
    getBitmapFromPathBlocking(context, path, size, bitmapSettings)
}

internal suspend fun getBitmapFromPathWithPaletteCatching(
    context: Context,
    path: String?,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = Either.catch {
    getBitmapFromPathWithPalette(context, path, size, bitmapSettings)
}

internal fun getBitmapFromPathWithPaletteBlockingCatching(
    context: Context,
    path: String?,
    size: ImageSize? = null,
    bitmapSettings: (Bitmap) -> Unit = {}
) = Either.catch {
    getBitmapFromPathWithPaletteBlocking(context, path, size, bitmapSettings)
}