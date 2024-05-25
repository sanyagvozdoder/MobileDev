package com.example.mobiledev.presentation.algoritms

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import com.example.mobiledev.presentation.algoritms.util.ImageProcessor
import com.example.mobiledev.presentation.algoritms.util.generateUri
import com.example.mobiledev.presentation.algoritms.util.toBitmap
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.io.encoding.ExperimentalEncodingApi

// https://ru.wikipedia.org/wiki/%D0%9C%D0%B0%D1%81%D1%88%D1%82%D0%B0%D0%B1%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5_%D0%B8%D0%B7%D0%BE%D0%B1%D1%80%D0%B0%D0%B6%D0%B5%D0%BD%D0%B8%D1%8F
@OptIn(ExperimentalEncodingApi::class)
fun Scaling(img: ByteArray?, onEnd: (Uri?) -> Unit, args: List<Int>) {
    GlobalScope.launch {
        val bitmap = toBitmap(img)

        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val factor = (args[0].toFloat() / 100)

        var outBitmap: Bitmap

        if (factor > 1)
            outBitmap = upscale(pixels, width, height, factor)
        else
            outBitmap = downscale(pixels, width, height, factor)

        onEnd(generateUri(outBitmap))
    }
}

// https://gamedev.ru/terms/TrilinearFiltering
// https://ru.wikipedia.org/wiki/%D0%A2%D1%80%D0%B8%D0%BB%D0%B8%D0%BD%D0%B5%D0%B9%D0%BD%D0%B0%D1%8F_%D1%84%D0%B8%D0%BB%D1%8C%D1%82%D1%80%D0%B0%D1%86%D0%B8%D1%8F
suspend fun downscale(pixels: IntArray, width: Int, height: Int, factor: Float): Bitmap {
    val outputWidth = (width * factor).toInt()
    val outputHeight = (height * factor).toInt()
    val outPixels = IntArray(outputWidth * outputHeight)

    ImageProcessor(
        outPixels,
        outputWidth,
        outputHeight
    ) { x, y, _ ->
        val srcX = x / factor
        val srcY = y / factor
        val srcI = srcY.toInt() * width + srcX.toInt()
        val i = y * outputWidth + x

        val mip0color = pixels[srcI]
        val mip1color = interpolatedPixel(pixels, width, height, srcX, srcY)
        val t = maxOf(srcY - srcY.toInt(), srcX - srcX.toInt())

        outPixels[i] = lerpColor(mip0color, mip1color, t)
    }.process().join()

    return Bitmap.createBitmap(outPixels, outputWidth, outputHeight, Bitmap.Config.ARGB_8888)
}

suspend fun upscale(pixels: IntArray, width: Int, height: Int, factor: Float): Bitmap {
    val outputWidth = (width * factor).toInt()
    val outputHeight = (height * factor).toInt()
    val outPixels = IntArray(outputWidth * outputHeight)

    ImageProcessor(
        outPixels,
        outputWidth,
        outputHeight
    ) { x, y, _ ->
        val srcX = x / factor
        val srcY = y / factor
        val i = y * outputWidth + x

        outPixels[i] = interpolatedPixel(
            pixels,
            width, height, srcX, srcY
        )
    }.process().join()

    return Bitmap.createBitmap(outPixels, outputWidth, outputHeight, Bitmap.Config.ARGB_8888)
}

// https://gamedev.ru/code/terms/BilinearFiltering
fun interpolatedPixel(
    pixels: IntArray,
    width: Int,
    height: Int,
    u: Float,
    v: Float
): Int {
    val ui = u.toInt().coerceIn(0, width - 2)
    val vi = v.toInt().coerceIn(0, height - 2)
    val du = u - ui
    val dv = v - vi

    val col1 = pixels[vi * width + ui]
    val col2 = pixels[vi * width + (ui + 1)]
    val col3 = pixels[(vi + 1) * width + ui + 1]
    val col4 = pixels[(vi + 1) * width + ui]

    val r = interpolateChannel(
        du, dv,
        Color.red(col1),
        Color.red(col2),
        Color.red(col3),
        Color.red(col4)
    )

    val g = interpolateChannel(
        du, dv,
        Color.green(col1),
        Color.green(col2),
        Color.green(col3),
        Color.green(col4)
    )

    val b = interpolateChannel(
        du, dv,
        Color.blue(col1),
        Color.blue(col2),
        Color.blue(col3),
        Color.blue(col4)
    )

    return Color.rgb(r, g, b)
}

fun interpolateChannel(
    du: Float,
    dv: Float,
    c1: Int,
    c2: Int,
    c3: Int,
    c4: Int,
): Int {
    return ((1f - du) * (1f - dv) * c1 +
            du * (1f - dv) * c2 +
            du * dv * c3 +
            (1f - du) * dv * c4).toInt()
}

fun lerp(a: Float, b: Float, t: Float): Float {
    return a * (1 - t) + b * t
}

fun lerpColor(a: Int, b: Int, t: Float): Int {
    val red = lerp(Color.red(a).toFloat(), Color.red(b).toFloat(), t).toInt()
    val green = lerp(Color.green(a).toFloat(), Color.green(b).toFloat(), t).toInt()
    val blue = lerp(Color.blue(a).toFloat(), Color.blue(b).toFloat(), t).toInt()
    return Color.rgb(red, green, blue)
}
