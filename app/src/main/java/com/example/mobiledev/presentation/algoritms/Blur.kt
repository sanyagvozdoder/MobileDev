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
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.math.*
import kotlin.math.roundToInt


// https://ru.wikipedia.org/wiki/%D0%9C%D0%B0%D1%81%D1%88%D1%82%D0%B0%D0%B1%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5_%D0%B8%D0%B7%D0%BE%D0%B1%D1%80%D0%B0%D0%B6%D0%B5%D0%BD%D0%B8%D1%8F
@OptIn(ExperimentalEncodingApi::class)
fun Blur(img:ByteArray?, viewModelInstance: EditorScreenViewModel, args:List<Int>) {
    GlobalScope.launch {

        val bitmap = toBitmap(img)

        val sigma = 0.8f
        val coreSize = 10

        val core = createCore(coreSize, sigma)

        val outputWidth = bitmap.width
        val outputHeight = bitmap.height

        val outputPixels = IntArray(outputWidth * outputHeight)

        for(x in 0 until outputHeight)
        {
            for (y in 0 until outputWidth)
            {
                var sumR : Float = 0f
                var sumG : Float = 0f
                var sumB : Float = 0f
                for (i in -coreSize / 2..coreSize / 2) {
                    for (j in -coreSize / 2..coreSize / 2) {
                        val imageX = (x + j + outputWidth) % outputWidth
                        val imageY = (y + i + outputHeight) % outputHeight

                        val pixel = readRGBA(bitmap.getPixel(imageX, imageY))

                        sumR += pixel.red * core[i + coreSize / 2][j + coreSize / 2]
                        sumG += pixel.green * core[i + coreSize / 2][j + coreSize / 2]
                        sumB += pixel.blue * core[i + coreSize / 2][j + coreSize / 2]
                    }
                }
                val i = y * outputWidth + x
                val pixel = readRGBA(bitmap.getPixel(x, y))
                pixel.red = sumR.toInt()
                pixel.green = sumG.toInt()
                pixel.blue = sumB.toInt()

                outputPixels[i] = writeRGBA(pixel)
            }
        }


        updateScreen(bitmap, viewModelInstance)
    }
}

fun createCore(coreSize: Int, sigma: Float): Array<FloatArray> {
    val core = Array(coreSize) { FloatArray(coreSize) }
    val sigma2 = sigma * sigma
    var sum = 0.0


    for (y in -coreSize / 2..coreSize / 2) {
        for (x in -coreSize / 2..coreSize / 2) {
            val g = (1 / (2 * Math.PI * sigma2)) * Math.exp(-(x * x + y * y) / (2 * sigma2).toDouble())
            core[y + coreSize / 2][x + coreSize / 2] = g.toFloat()
            sum += g
        }
    }

    for (y in 0 until coreSize) {
        for (x in 0 until coreSize) {
            core[y][x] = (core[y][x] / sum).toFloat()
        }
    }

    return core
}