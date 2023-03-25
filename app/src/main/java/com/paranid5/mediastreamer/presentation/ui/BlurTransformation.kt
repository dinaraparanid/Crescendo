package com.paranid5.mediastreamer.presentation.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import coil.size.Size
import coil.transform.Transformation

class BlurTransformation @JvmOverloads constructor(
    private val context: Context,
    private val radius: Float = DEFAULT_RADIUS,
    private val sampling: Float = DEFAULT_SAMPLING
) : Transformation {
    private companion object {
        private const val DEFAULT_RADIUS = 15F
        private const val DEFAULT_SAMPLING = 5F

        private inline val Bitmap.safeConfig
            get() = config ?: Bitmap.Config.ARGB_8888
    }

    @Suppress("NullableToStringCall")
    override val cacheKey = "${BlurTransformation::class.java.name}-$radius-$sampling"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        val scaledWidth = (input.width / sampling).toInt()
        val scaledHeight = (input.height / sampling).toInt()
        val output = createBitmap(scaledWidth, scaledHeight, input.safeConfig)

        output.applyCanvas {
            scale(1 / sampling, 1 / sampling)
            drawBitmap(input, 0f, 0f, paint)
        }

        var script: RenderScript? = null
        var tmpInt: Allocation? = null
        var tmpOut: Allocation? = null
        var blur: ScriptIntrinsicBlur? = null

        try {
            script = RenderScript.create(context)
            tmpInt = Allocation.createFromBitmap(script, output, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT)
            tmpOut = Allocation.createTyped(script, tmpInt.type)
            blur = ScriptIntrinsicBlur.create(script, Element.U8_4(script))
            blur.setRadius(radius)
            blur.setInput(tmpInt)
            blur.forEach(tmpOut)
            tmpOut.copyTo(output)
        } finally {
            script?.destroy()
            tmpInt?.destroy()
            tmpOut?.destroy()
            blur?.destroy()
        }

        return output
    }
}