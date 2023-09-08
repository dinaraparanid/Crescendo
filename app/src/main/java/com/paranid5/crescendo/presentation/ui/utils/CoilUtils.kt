package com.paranid5.crescendo.presentation.ui.utils

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.palette.graphics.Palette
import coil.executeBlocking
import coil.imageLoader
import coil.request.ImageRequest
import com.paranid5.crescendo.R
import com.paranid5.crescendo.data.VideoMetadata
import com.paranid5.crescendo.presentation.ui.extensions.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.jaudiotagger.audio.AudioFileIO
import java.io.File

class CoilUtils(private val context: Context) {
    private inline val loaderBuilder
        get() = context.imageLoader.newBuilder()

    private inline val Bitmap.withPalette
        get() = Palette.from(this).generate() to this

    private inline val Bitmap.bitmapDrawable
        get() = toDrawable(context.resources)

    private inline val Any.coilModelBuilder
        get() = ImageRequest.Builder(context)
            .data(this)
            .allowHardware(false)
            .allowConversionToBitmap(true)

    internal suspend inline fun getThumbnailBitmap(): Bitmap =
        loaderBuilder
            .build()
            .execute(R.drawable.cover_thumbnail.coilModelBuilder.build())
            .drawable!!
            .toBitmap()

    internal fun getThumbnailBitmapBlocking(): Bitmap =
        loaderBuilder
            .build()
            .executeBlocking(R.drawable.cover_thumbnail.coilModelBuilder.build())
            .drawable!!
            .toBitmap()

    internal suspend inline fun getThumbnailBitmapDrawable() =
        getThumbnailBitmap().bitmapDrawable

    internal fun getThumbnailBitmapDrawableBlocking() =
        getThumbnailBitmapBlocking().bitmapDrawable

    internal suspend inline fun getThumbnailBitmapWithPalette() =
        getThumbnailBitmap().withPalette

    internal fun getThumbnailBitmapWithPaletteBlocking() =
        getThumbnailBitmapBlocking().withPalette

    internal suspend inline fun getThumbnailBitmapDrawableWithPalette() =
        getThumbnailBitmapWithPalette().let { (palette, bitmap) ->
            palette to bitmap.bitmapDrawable
        }

    internal fun getThumbnailBitmapDrawableWithPaletteBlocking() =
        getThumbnailBitmapWithPaletteBlocking().let { (palette, bitmap) ->
            palette to bitmap.bitmapDrawable
        }

    private suspend inline fun getBitmapFromModel(
        model: Any,
        size: Pair<Int, Int>?,
        bitmapSettings: (Bitmap) -> Unit = {}
    ): Bitmap = loaderBuilder
        .build()
        .execute(
            size
                ?.run { model.coilModelBuilder.size(first, second).build() }
                ?: model.coilModelBuilder.build()
        )
        .drawable!!
        .let { size?.run { it.toBitmap(first, second) } ?: it.toBitmap() }
        .also(bitmapSettings)

    private inline fun getBitmapFromModelBlocking(
        model: Any,
        size: Pair<Int, Int>?,
        bitmapSettings: (Bitmap) -> Unit = {}
    ): Bitmap = loaderBuilder
        .build()
        .executeBlocking(
            size
                ?.run { model.coilModelBuilder.size(first, second).build() }
                ?: model.coilModelBuilder.build()
        )
        .drawable!!
        .let { size?.run { it.toBitmap(first, second) } ?: it.toBitmap() }
        .also(bitmapSettings)

    private suspend inline fun getBitmapFromResource(
        @DrawableRes res: Int,
        size: Pair<Int, Int>?,
        bitmapSettings: (Bitmap) -> Unit
    ) = getBitmapFromModel(res, size, bitmapSettings)

    private inline fun getBitmapFromResourceBlocking(
        @DrawableRes res: Int,
        size: Pair<Int, Int>?,
        bitmapSettings: (Bitmap) -> Unit
    ) = getBitmapFromModelBlocking(res, size, bitmapSettings)

    internal suspend inline fun getBitmapFromResourceCatching(
        @DrawableRes res: Int,
        size: Pair<Int, Int>? = null,
        bitmapSettings: (Bitmap) -> Unit = {}
    ) = runCatching { getBitmapFromResource(res, size, bitmapSettings) }

    internal inline fun getBitmapFromResourceBlockingCatching(
        @DrawableRes res: Int,
        size: Pair<Int, Int>? = null,
        bitmapSettings: (Bitmap) -> Unit = {}
    ) = runCatching { getBitmapFromResourceBlocking(res, size, bitmapSettings) }

    private suspend inline fun getBitmapFromUrl(
        url: String,
        size: Pair<Int, Int>?,
        bitmapSettings: (Bitmap) -> Unit
    ) = getBitmapFromModel(url, size, bitmapSettings)

    private inline fun getBitmapFromUrlBlocking(
        url: String,
        size: Pair<Int, Int>?,
        bitmapSettings: (Bitmap) -> Unit
    ) = getBitmapFromModelBlocking(url, size, bitmapSettings)

    private suspend inline fun getBitmapFromUrlWithPalette(
        url: String,
        size: Pair<Int, Int>?,
        bitmapSettings: (Bitmap) -> Unit
    ) = getBitmapFromUrl(url, size, bitmapSettings).withPalette

    private inline fun getBitmapFromUrlWithPaletteBlocking(
        url: String,
        size: Pair<Int, Int>?,
        bitmapSettings: (Bitmap) -> Unit
    ) = getBitmapFromUrlBlocking(url, size, bitmapSettings).withPalette

    internal suspend inline fun getBitmapFromUrlCatching(
        url: String,
        size: Pair<Int, Int>? = null,
        bitmapSettings: (Bitmap) -> Unit = {}
    ) = kotlin.runCatching { getBitmapFromUrl(url, size, bitmapSettings) }

    internal inline fun getBitmapFromUrlBlockingCatching(
        url: String,
        size: Pair<Int, Int>? = null,
        bitmapSettings: (Bitmap) -> Unit = {}
    ) = kotlin.runCatching { getBitmapFromUrlBlocking(url, size, bitmapSettings) }

    internal suspend inline fun getBitmapFromUrlWithPaletteCatching(
        url: String,
        size: Pair<Int, Int>? = null,
        bitmapSettings: (Bitmap) -> Unit = {}
    ) = kotlin.runCatching {
        getBitmapFromUrlWithPalette(url, size, bitmapSettings)
    }

    internal inline fun getBitmapFromUrlWithPaletteBlockingCatching(
        url: String,
        size: Pair<Int, Int>? = null,
        bitmapSettings: (Bitmap) -> Unit = {}
    ) = kotlin.runCatching {
        getBitmapFromUrlWithPaletteBlocking(url, size, bitmapSettings)
    }

    private suspend inline fun getBitmapFromPath(
        path: String?,
        size: Pair<Int, Int>?,
        bitmapSettings: (Bitmap) -> Unit
    ) = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> path?.let {
            AudioFileIO
                .read(File(it))
                .tagOrCreateAndSetDefault
                ?.firstArtwork
                ?.binaryData
        }

        else -> MediaMetadataRetriever()
            .apply { setDataSource(path) }
            .embeddedPicture
    }
        ?.toBitmap()
        ?.let { getBitmapFromModel(it, size, bitmapSettings) }
        ?: getThumbnailBitmap()

    private inline fun getBitmapFromPathBlocking(
        path: String?,
        size: Pair<Int, Int>?,
        bitmapSettings: (Bitmap) -> Unit
    ) = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> path?.let {
            AudioFileIO
                .read(File(it))
                .tagOrCreateAndSetDefault
                ?.firstArtwork
                ?.binaryData
        }

        else -> MediaMetadataRetriever()
            .apply { setDataSource(path) }
            .embeddedPicture
    }
        ?.toBitmap()
        ?.let { getBitmapFromModelBlocking(it, size, bitmapSettings) }
        ?: getThumbnailBitmapBlocking()

    private suspend inline fun getBitmapFromPathWithPalette(
        path: String?,
        size: Pair<Int, Int>?,
        bitmapSettings: (Bitmap) -> Unit
    ) = getBitmapFromPath(path, size, bitmapSettings).withPalette

    private inline fun getBitmapFromPathWithPaletteBlocking(
        path: String?,
        size: Pair<Int, Int>?,
        bitmapSettings: (Bitmap) -> Unit
    ) = getBitmapFromPathBlocking(path, size, bitmapSettings).withPalette

    internal suspend inline fun getBitmapFromPathCatching(
        path: String?,
        size: Pair<Int, Int>?,
        bitmapSettings: (Bitmap) -> Unit = {}
    ) = kotlin.runCatching {
        getBitmapFromPath(path, size, bitmapSettings)
    }

    internal inline fun getBitmapFromPathBlockingCatching(
        path: String?,
        size: Pair<Int, Int>?,
        bitmapSettings: (Bitmap) -> Unit = {}
    ) = kotlin.runCatching {
        getBitmapFromPathBlocking(path, size, bitmapSettings)
    }

    internal suspend inline fun getBitmapFromPathWithPaletteCatching(
        path: String?,
        size: Pair<Int, Int>?,
        bitmapSettings: (Bitmap) -> Unit = {}
    ) = kotlin.runCatching {
        getBitmapFromPathWithPalette(path, size, bitmapSettings)
    }

    internal inline fun getBitmapFromPathWithPaletteBlockingCatching(
        path: String?,
        size: Pair<Int, Int>?,
        bitmapSettings: (Bitmap) -> Unit = {}
    ) = kotlin.runCatching {
        getBitmapFromPathWithPaletteBlocking(path, size, bitmapSettings)
    }

    internal suspend inline fun getVideoCoverBitmapAsync(
        videoMetadata: VideoMetadata,
        size: Pair<Int, Int>? = null,
        crossinline bitmapSettings: (Bitmap) -> Unit = {}
    ) = coroutineScope {
        async(Dispatchers.IO) {
            videoMetadata
                .covers
                .map { getBitmapFromUrlCatching(it, size, bitmapSettings) }
                .firstOrNull { it.isSuccess }
                ?.getOrNull()
                ?: getThumbnailBitmap()
        }
    }

    internal inline fun getVideoCoverBitmapBlocking(
        videoMetadata: VideoMetadata,
        size: Pair<Int, Int>? = null,
        bitmapSettings: (Bitmap) -> Unit = {}
    ) = videoMetadata
        .covers
        .map { getBitmapFromUrlBlockingCatching(it, size, bitmapSettings) }
        .firstOrNull { it.isSuccess }
        ?.getOrNull()
        ?: getThumbnailBitmapBlocking()

    internal suspend inline fun getVideoCoverBitmapWithPaletteAsync(
        videoMetadata: VideoMetadata,
        size: Pair<Int, Int>? = null,
        crossinline bitmapSettings: (Bitmap) -> Unit = {}
    ) = coroutineScope {
        async(Dispatchers.IO) {
            videoMetadata
                .covers
                .map { getBitmapFromUrlWithPaletteCatching(it, size, bitmapSettings) }
                .firstOrNull { it.isSuccess }
                ?.getOrNull()
                ?: getThumbnailBitmapWithPalette()
        }
    }

    internal inline fun getVideoCoverBitmapWithPaletteBlocking(
        videoMetadata: VideoMetadata,
        size: Pair<Int, Int>? = null,
        bitmapSettings: (Bitmap) -> Unit = {}
    ) = videoMetadata
        .covers
        .map { getBitmapFromUrlWithPaletteBlockingCatching(it, size, bitmapSettings) }
        .firstOrNull { it.isSuccess }
        ?.getOrNull()
        ?: getThumbnailBitmapWithPaletteBlocking()

    internal suspend inline fun getVideoCoverAsync(
        videoMetadata: VideoMetadata,
        size: Pair<Int, Int>? = null,
        crossinline bitmapSettings: (Bitmap) -> Unit = {}
    ) = coroutineScope {
        async(Dispatchers.IO) {
            videoMetadata
                .covers
                .map { getBitmapFromUrlCatching(it, size, bitmapSettings) }
                .firstOrNull { it.isSuccess }
                ?.map { it.bitmapDrawable }
                ?.getOrNull()
                ?: getThumbnailBitmapDrawable()
        }
    }

    internal inline fun getVideoCoverBlocking(
        videoMetadata: VideoMetadata,
        size: Pair<Int, Int>? = null,
        bitmapSettings: (Bitmap) -> Unit = {}
    ) = videoMetadata
        .covers
        .map { getBitmapFromUrlBlockingCatching(it, size, bitmapSettings) }
        .firstOrNull { it.isSuccess }
        ?.map { it.bitmapDrawable }
        ?.getOrNull()
        ?: getThumbnailBitmapDrawableBlocking()

    internal suspend inline fun getVideoCoverWithPaletteAsync(
        videoMetadata: VideoMetadata,
        size: Pair<Int, Int>? = null,
        crossinline bitmapSettings: (Bitmap) -> Unit = {}
    ) = coroutineScope {
        async(Dispatchers.IO) {
            videoMetadata
                .covers
                .map { getBitmapFromUrlWithPaletteCatching(it, size, bitmapSettings) }
                .firstOrNull { it.isSuccess }
                ?.map { (palette, bitmap) -> palette to bitmap.bitmapDrawable }
                ?.getOrNull()
                ?: getThumbnailBitmapDrawableWithPalette()
        }
    }

    internal inline fun getVideoCoverWithPaletteBlocking(
        videoMetadata: VideoMetadata,
        size: Pair<Int, Int>? = null,
        bitmapSettings: (Bitmap) -> Unit = {}
    ) = videoMetadata
        .covers
        .map { getBitmapFromUrlWithPaletteBlockingCatching(it, size, bitmapSettings) }
        .firstOrNull { it.isSuccess }
        ?.map { (palette, bitmap) -> palette to bitmap.bitmapDrawable }
        ?.getOrNull()
        ?: getThumbnailBitmapDrawableWithPaletteBlocking()

    internal suspend inline fun getTrackCoverBitmapAsync(
        path: String?,
        size: Pair<Int, Int>? = null,
        crossinline bitmapSettings: (Bitmap) -> Unit = {}
    ) = coroutineScope {
        async(Dispatchers.IO) {
            getBitmapFromPathCatching(path, size, bitmapSettings)
                .getOrNull()
                ?: getThumbnailBitmap()
        }
    }

    internal inline fun getTrackCoverBitmapBlocking(
        path: String?,
        size: Pair<Int, Int>? = null,
        bitmapSettings: (Bitmap) -> Unit = {}
    ) = getBitmapFromPathBlockingCatching(path, size, bitmapSettings)
        .getOrNull()
        ?: getThumbnailBitmapBlocking()

    internal suspend inline fun getTrackCoverBitmapWithPaletteAsync(
        path: String,
        size: Pair<Int, Int>? = null,
        crossinline bitmapSettings: (Bitmap) -> Unit = {}
    ) = coroutineScope {
        async(Dispatchers.IO) {
            getBitmapFromPathWithPaletteCatching(path, size, bitmapSettings)
                .getOrNull()
                ?: getThumbnailBitmapWithPalette()
        }
    }

    internal inline fun getTrackCoverBitmapWithPaletteBlocking(
        path: String,
        size: Pair<Int, Int>? = null,
        bitmapSettings: (Bitmap) -> Unit = {}
    ) = getBitmapFromPathWithPaletteBlockingCatching(path, size, bitmapSettings)
        .getOrNull()
        ?: getThumbnailBitmapWithPaletteBlocking()

    internal suspend inline fun getTrackCoverAsync(
        path: String?,
        size: Pair<Int, Int>?,
        crossinline bitmapSettings: (Bitmap) -> Unit = {}
    ) = coroutineScope {
        async(Dispatchers.IO) {
            getBitmapFromPathCatching(path, size, bitmapSettings)
                .map { it.bitmapDrawable }
                .getOrNull()
                ?: getThumbnailBitmapDrawable()
        }
    }

    internal inline fun getTrackCoverBlocking(
        path: String?,
        size: Pair<Int, Int>?,
        bitmapSettings: (Bitmap) -> Unit = {}
    ) = getBitmapFromPathBlockingCatching(path, size, bitmapSettings)
        .map { it.bitmapDrawable }
        .getOrNull()
        ?: getThumbnailBitmapDrawableBlocking()

    internal suspend inline fun getTrackCoverWithPaletteAsync(
        path: String?,
        size: Pair<Int, Int>?,
        crossinline bitmapSettings: (Bitmap) -> Unit = {}
    ) = coroutineScope {
        async(Dispatchers.IO) {
            getBitmapFromPathWithPaletteCatching(path, size, bitmapSettings)
                .map { (palette, bitmap) -> palette to bitmap.bitmapDrawable }
                .getOrNull()
                ?: getThumbnailBitmapDrawableWithPalette()
        }
    }

    internal inline fun getTrackCoverWithPaletteBlocking(
        path: String?,
        size: Pair<Int, Int>?,
        bitmapSettings: (Bitmap) -> Unit = {}
    ) = getBitmapFromPathWithPaletteBlockingCatching(path, size, bitmapSettings)
        .map { (palette, bitmap) -> palette to bitmap.bitmapDrawable }
        .getOrNull()
        ?: getThumbnailBitmapDrawableWithPaletteBlocking()
}