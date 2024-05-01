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
fun Negative(img:ByteArray?, viewModelInstance: EditorScreenViewModel) {
    GlobalScope.launch {
        val bitmap = toBitmap(img)

        val outputWidth = bitmap.width
        val outputHeight = bitmap.height

        val outputPixels = IntArray(outputWidth * outputHeight)

        val processPixel = { x: Int, y: Int, color: Int ->

            val pixel = readRGBA(color)


            pixel.red = 255 - pixel.red
            pixel.blue = 255 - pixel.blue
            pixel.green = 255 - pixel.green

            val i = y * outputWidth + x
            outputPixels[i] = writeRGBA(pixel)
        }

        val makeNewBitmap = {
            bitmap.recycle()

            val outputBitmap =
                Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888)
            outputBitmap.setPixels(outputPixels, 0, outputWidth, 0, 0, outputWidth, outputHeight)
            updateScreen(outputBitmap, viewModelInstance)
        }

        val config = ImageProcessorConfig(bitmap, processPixel, 100, "ALGO_NEGATIVE")
        val processor = ImageProcessor(config = config)
        processor.process(makeNewBitmap).join()
    }
}