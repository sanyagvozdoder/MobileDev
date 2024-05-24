package com.example.mobiledev.presentation.algoritms

import android.graphics.Bitmap
import android.net.Uri
import com.example.mobiledev.presentation.algoritms.util.ImageProcessor
import com.example.mobiledev.presentation.algoritms.util.generateUri
import com.example.mobiledev.presentation.algoritms.util.readRGBA
import com.example.mobiledev.presentation.algoritms.util.toBitmap
import com.example.mobiledev.presentation.algoritms.util.writeRGBA
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.io.encoding.ExperimentalEncodingApi

// https://ru.wikipedia.org/wiki/%D0%9C%D0%B0%D1%81%D1%88%D1%82%D0%B0%D0%B1%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5_%D0%B8%D0%B7%D0%BE%D0%B1%D1%80%D0%B0%D0%B6%D0%B5%D0%BD%D0%B8%D1%8F
@OptIn(ExperimentalEncodingApi::class)
fun Grayscale(img:ByteArray?, onEnd: (Uri?) -> Unit, args:List<Int>) {
    GlobalScope.launch {
        val bitmap = toBitmap(img)

        val outputWidth = bitmap.width
        val outputHeight = bitmap.height

        val outputPixels = IntArray(outputWidth * outputHeight)

        val processPixel = { x: Int, y: Int, color: Int ->

            val pixel = readRGBA(color)

            var totalColor = 0

            totalColor += (pixel.red + pixel.blue + pixel.green) / 3

            pixel.red = totalColor
            pixel.green = totalColor
            pixel.blue = totalColor

            val i = y * outputWidth + x
            outputPixels[i] = writeRGBA(pixel)
        }

        val processor = ImageProcessor(bitmap, processPixel)
        processor.process().join()

        bitmap.recycle()

        val outputBitmap =
            Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888)
        outputBitmap.setPixels(outputPixels, 0, outputWidth, 0, 0, outputWidth, outputHeight)
        onEnd(generateUri(outputBitmap))
    }
}