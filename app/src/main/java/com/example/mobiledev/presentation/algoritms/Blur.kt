package com.example.mobiledev.presentation.algoritms

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.util.Log
import androidx.compose.material3.surfaceColorAtElevation
import androidx.core.graphics.set
import com.example.mobiledev.presentation.algoritms.util.ImageProcessor
import com.example.mobiledev.presentation.algoritms.util.Rgb
import com.example.mobiledev.presentation.algoritms.util.generateUri
import com.example.mobiledev.presentation.algoritms.util.readRGBA
import com.example.mobiledev.presentation.algoritms.util.toBitmap
import com.example.mobiledev.presentation.algoritms.util.writeRGBA
import com.example.mobiledev.presentation.editorscreen.EditorScreenViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.math.*
import kotlin.math.roundToInt

fun Blur(img:ByteArray?, onEnd: (Uri?) -> Unit, args:List<Int>) {

    val radius = args[0]

    val bitmap = toBitmap(img)
    val width = bitmap.width
    val height = bitmap.height
    val size = width * height
    val pixels = IntArray(size)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    val newPixels = IntArray(pixels.size)

    val kernel = calculateGaussianKernel(radius)
    val kernelRadius = radius / 3

    for (y in 0 until height) {
        for (x in 0 until width) {
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
        }
    }

    for (x in 0 until width) {
        for (y in 0 until height) {
            var redSum = 0f
            var greenSum = 0f
            var blueSum = 0f
            var weightSum = 0f

            for (i in -kernelRadius..kernelRadius) {
                val pixelY = (y + i).coerceIn(0, height - 1)
                val index = pixelY * width + x
                val pixelColor = newPixels[index]
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
        }
    }

    onEnd(generateUri(Bitmap.createBitmap(newPixels, width, height, Bitmap.Config.ARGB_8888)))
}
fun calculateGaussianKernel(radius: Int): FloatArray {
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