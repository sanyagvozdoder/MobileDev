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
import kotlin.math.roundToInt

// https://ru.wikipedia.org/wiki/%D0%9C%D0%B0%D1%81%D1%88%D1%82%D0%B0%D0%B1%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5_%D0%B8%D0%B7%D0%BE%D0%B1%D1%80%D0%B0%D0%B6%D0%B5%D0%BD%D0%B8%D1%8F
@OptIn(ExperimentalEncodingApi::class)
fun Scaling(img:ByteArray?, viewModelInstance:EditorScreenViewModel) {
    GlobalScope.launch {
        val bitmap = toBitmap(img)

        val factor = 1.3f // МНОЖИТЕЛЬ УВЕЛИЧЕНИЯ ГДЕ 1 ЭТО 100% И ТАК ДАЛЕЕ
        val outputWidth = (bitmap.width * factor).toInt()
        val outputHeight = (bitmap.height * factor).toInt()

        val outSums = MutableList<Rgb>(outputWidth * outputHeight) { Rgb(0, 0, 0, 0) }
        val outCounts = MutableList<Int>(outputWidth * outputHeight) { 0 }

        val processPixel = { x: Int, y: Int, color: Int ->
            val outX = (x * factor).toInt()
            val outY = (y * factor).toInt()

            val nextOutX = ((x + 1) * factor).toInt()
            val nextOutY = ((y + 1) * factor).toInt()

            for (dx in outX until nextOutX)
            {
                for (dy in outY until nextOutY)
                {
                    val i = dy * outputWidth + dx
                    outSums[i].red += Color.red(color)
                    outSums[i].green += Color.green(color)
                    outSums[i].blue += Color.blue(color)
                    outSums[i].alpha += Color.alpha(color)

                    outCounts[i] = outCounts[i] + 1
                    if(dy >= outputHeight)
                        break
                }
                if(dx >= outputWidth)
                    break
            }
        }

        val makeNewBitmap = {
            val inPixels = IntArray(bitmap.width * bitmap.height)
            bitmap.getPixels(inPixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
            bitmap.recycle()

            val outputPixels = IntArray(outputWidth * outputHeight)

            repeat(outputWidth) { x ->
                repeat(outputHeight) { y ->
                    val i = y * outputWidth + x

                    if (outCounts[i] > 0) {
                        val pixel = outSums[i]
                        val normalizer = 1f / outCounts[i]

                        pixel.red = (pixel.red * normalizer).toInt()
                        pixel.green = (pixel.green * normalizer).toInt()
                        pixel.blue = (pixel.blue * normalizer).toInt()
                        pixel.alpha = (pixel.alpha * normalizer).toInt()

                        val color = writeRGBA(pixel)

                        outputPixels[i] = color
                    }
                }
            }

            val outputBitmap =
                Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888)
            outputBitmap.setPixels(outputPixels, 0, outputWidth, 0, 0, outputWidth, outputHeight)
            updateScreen(outputBitmap, viewModelInstance)
        }

        val config = ImageProcessorConfig(bitmap, processPixel, 100, "ALGO_SCALING")
        val processor = ImageProcessor(config = config)
        processor.process(makeNewBitmap).join()
    }
}
