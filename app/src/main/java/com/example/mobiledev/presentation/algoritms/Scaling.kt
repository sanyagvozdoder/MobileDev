package com.example.mobiledev.presentation.algoritms

import android.graphics.Bitmap
import android.util.Log
import com.example.mobiledev.presentation.algoritms.util.Rgb
import com.example.mobiledev.presentation.algoritms.util.readRGBA
import com.example.mobiledev.presentation.algoritms.util.toBitmap
import com.example.mobiledev.presentation.algoritms.util.updateScreen
import com.example.mobiledev.presentation.algoritms.util.writeRGBA
import com.example.mobiledev.presentation.editorscreen.EditorScreenViewModel
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.math.roundToInt

// https://ru.wikipedia.org/wiki/%D0%9C%D0%B0%D1%81%D1%88%D1%82%D0%B0%D0%B1%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5_%D0%B8%D0%B7%D0%BE%D0%B1%D1%80%D0%B0%D0%B6%D0%B5%D0%BD%D0%B8%D1%8F
@OptIn(ExperimentalEncodingApi::class)
fun Scaling(img:ByteArray?, viewModelInstance:EditorScreenViewModel):Unit{
    val bitmap = toBitmap(img)

    val factor = 0.5f
    val outWidth = (bitmap.width * factor).roundToInt()
    val outHeight = (bitmap.height * factor).roundToInt()

    val sums = MutableList<MutableList<Rgb>>(outWidth){MutableList<Rgb>(outHeight){ Rgb(0, 0, 0, 0) } }
    val quantities = MutableList<MutableList<Float>>(outWidth){MutableList<Float>(outHeight){0f}}

    val startTime = System.currentTimeMillis()

    repeat(bitmap.width){ x ->
        val outX = (x * factor).toInt()

        repeat(bitmap.height){ y ->
            val outY = (y * factor).toInt()

            val pixel = readRGBA(bitmap.getPixel(x, y))

            sums[outX][outY].red += pixel.red
            sums[outX][outY].green += pixel.green
            sums[outX][outY].blue += pixel.blue
            sums[outX][outY].alpha += pixel.alpha

            quantities[outX][outY]++
        }
    }

    val outputBitmap = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888)

    repeat(outWidth){ x ->
        repeat(outHeight){ y ->
            val pixel = sums[x][y]
            val normalizer = 1f / quantities[x][y]

            pixel.red = (pixel.red * normalizer).toInt()
            pixel.green = (pixel.green * normalizer).toInt()
            pixel.blue = (pixel.blue * normalizer).toInt()
            pixel.alpha = (pixel.alpha * normalizer).toInt()

            outputBitmap.setPixel(x, y, writeRGBA(pixel))
        }
    }

    val processingTime = System.currentTimeMillis() - startTime

    updateScreen(outputBitmap, viewModelInstance)

    Log.d("ALGO_SCALING", "processing finished at " + Thread.currentThread().toString() +
            "in: " + processingTime.toString() + "ms. input colorSpace: " + bitmap.colorSpace.toString() +
            ", output colorSpace: " + outputBitmap.colorSpace.toString())
}
