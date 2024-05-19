package com.example.mobiledev.presentation.algoritms

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.compose.material3.surfaceColorAtElevation
import androidx.core.graphics.set
import com.example.mobiledev.presentation.algoritms.util.ImageProcessor
import com.example.mobiledev.presentation.algoritms.util.ImageProcessorConfig
import com.example.mobiledev.presentation.algoritms.util.Rgb
import com.example.mobiledev.presentation.algoritms.util.readRGBA
import com.example.mobiledev.presentation.algoritms.util.toBitmap
import com.example.mobiledev.presentation.algoritms.util.updateScreen
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



@OptIn(ExperimentalEncodingApi::class)


fun UnsharpMask(img:ByteArray?, viewModelInstance:EditorScreenViewModel, args:List<Int>)  {

    val threshold = args[0];
    val radius = args[1];
    val amount : Float= args[2].toFloat() / 10;

    val bitmap = toBitmap(img)
    val width = bitmap.width
    val height = bitmap.height
    val size = width * height
    val originalPixels = IntArray(size)
    bitmap.getPixels(originalPixels, 0, width, 0, 0, width, height)
    val blurredPixels = gaussianBlur(originalPixels, width, height, radius)

    val resultPixels = IntArray(size)
    for (i in 0 until size) {
        val originalColor = originalPixels[i]
        val blurredColor = blurredPixels[i]

        val redDiff = Color.red(originalColor) - Color.red(blurredColor)
        val greenDiff = Color.green(originalColor) - Color.green(blurredColor)
        val blueDiff = Color.blue(originalColor) - Color.blue(blurredColor)

        if ((abs(redDiff) > threshold) || (abs(greenDiff) > threshold) || (abs(blueDiff) > threshold)) {
            val newRed = min(255, max(0, Color.red(originalColor) + (redDiff * amount).toInt()))
            val newGreen = min(255, max(0, Color.green(originalColor) + (greenDiff * amount).toInt()))
            val newBlue = min(255, max(0, Color.blue(originalColor) + (blueDiff * amount).toInt()))

            resultPixels[i] = Color.rgb(newRed, newGreen, newBlue)
        } else {
            resultPixels[i] = originalColor
        }
    }


    updateScreen(Bitmap.createBitmap(resultPixels, width, height, Bitmap.Config.ARGB_8888), viewModelInstance)

}

fun gaussianBlur(pixels: IntArray, width: Int, height: Int, radius: Int): IntArray {
    val newPixels = IntArray(pixels.size)

    val kernel = calculateGaussianKernel1D(radius)
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