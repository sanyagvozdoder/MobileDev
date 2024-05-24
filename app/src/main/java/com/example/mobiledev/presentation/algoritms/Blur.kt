package com.example.mobiledev.presentation.algoritms

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import com.example.mobiledev.presentation.algoritms.util.ImageProcessor
import com.example.mobiledev.presentation.algoritms.util.generateUri
import com.example.mobiledev.presentation.algoritms.util.toBitmap
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun Blur(img:ByteArray?, onEnd: (Uri?) -> Unit, args:List<Int>) {
    GlobalScope.launch {
        val radius = args[0]

        val bitmap = toBitmap(img)
        val width = bitmap.width
        val height = bitmap.height

        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val blurred = gaussianBlur(pixels, width, height, radius)

        onEnd(generateUri(Bitmap.createBitmap(blurred, width, height, Bitmap.Config.ARGB_8888)))
    }
}

suspend fun gaussianBlur(pixels: IntArray, width: Int, height: Int, radius: Int): IntArray {
    val newPixels = IntArray(pixels.size)

    val kernel = calculateGaussianKernel1D(radius)
    val kernelRadius = radius / 3

    ImageProcessor(
        pixels,
        width,
        height
    ) { x, y, color ->
        var redSum = 0f
        var greenSum = 0f
        var blueSum = 0f
        var weightSum = 0f

        for (i in -kernelRadius..kernelRadius) {
            val pixelX = (x + i).coerceIn(0, width - 1)
            val index = y * width + pixelX
            val pixelColor = pixels[index]
            val weight = kernel[i + kernelRadius]

            redSum += Color.red(pixelColor) * weight
            greenSum += Color.green(pixelColor) * weight
            blueSum += Color.blue(pixelColor) * weight
            weightSum += weight
        }

        val newRed = (redSum / weightSum).toInt().coerceIn(0, 255)
        val newGreen = (greenSum / weightSum).toInt().coerceIn(0, 255)
        val newBlue = (blueSum / weightSum).toInt().coerceIn(0, 255)

        newPixels[y * width + x] = Color.rgb(newRed, newGreen, newBlue)
    }.process().join()

    return newPixels
}
fun calculateGaussianKernel1D(radius: Int): FloatArray {
    val kernelRadius = radius / 3
    val size = kernelRadius * 2 + 1
    val sigma = radius / 3.0
    val kernel = FloatArray(size)

    val twoSigmaSquare = 2 * sigma * sigma
    val oneOverSqrtTwoPiSigma = 1.0 / (Math.sqrt(2 * Math.PI) * sigma)

    for (i in -kernelRadius..kernelRadius) {
        val x = i.toDouble()
        val gaussianValue = oneOverSqrtTwoPiSigma * Math.exp(-(x * x) / twoSigmaSquare)
        kernel[i + kernelRadius] = gaussianValue.toFloat()
    }

    return kernel
}