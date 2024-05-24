package com.example.mobiledev.presentation.algoritms

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import com.example.mobiledev.presentation.algoritms.util.ImageProcessor
import com.example.mobiledev.presentation.algoritms.util.Rgb
import com.example.mobiledev.presentation.algoritms.util.generateUri
import com.example.mobiledev.presentation.algoritms.util.toBitmap
import com.example.mobiledev.presentation.algoritms.util.writeRGBA
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.io.encoding.ExperimentalEncodingApi

// https://ru.wikipedia.org/wiki/%D0%9C%D0%B0%D1%81%D1%88%D1%82%D0%B0%D0%B1%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5_%D0%B8%D0%B7%D0%BE%D0%B1%D1%80%D0%B0%D0%B6%D0%B5%D0%BD%D0%B8%D1%8F
@OptIn(ExperimentalEncodingApi::class)
fun Scaling(img:ByteArray?, onEnd: (Uri?) -> Unit, args:List<Int>) {
    GlobalScope.launch {
        val bitmap = toBitmap(img)

        val factor = (args[0].toFloat()/100)
        val outputWidth = (bitmap.width * factor).toInt()
        val outputHeight = (bitmap.height * factor).toInt()

        val outSums = MutableList<Rgb>(outputWidth * outputHeight) { Rgb(0, 0, 0, 0) }
        val outCounts = MutableList<Int>(outputWidth * outputHeight) { 0 }

        ImageProcessor(bitmap){ x, y, color ->
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
        }.process().join()

        val inPixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(inPixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        bitmap.recycle()

        val outputPixels = IntArray(outputWidth * outputHeight)

        ImageProcessor(
            outputPixels,
            outputWidth,
            outputHeight
        ){ x, y, color ->
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
        }.process().join()

        val outputBitmap =
            Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888)
        outputBitmap.setPixels(outputPixels, 0, outputWidth, 0, 0, outputWidth, outputHeight)

        onEnd(generateUri(outputBitmap))
    }
}
