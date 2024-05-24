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
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


@OptIn(ExperimentalEncodingApi::class)
fun UnsharpMask(img:ByteArray?, onEnd: (Uri?) -> Unit, args:List<Int>)  {
    GlobalScope.launch {
        val threshold = args[0];
        val radius = args[1];
        val amount = args[2].toFloat() / 10f;

        val bitmap = toBitmap(img)
        val width = bitmap.width
        val height = bitmap.height

        val originalPixels = IntArray(width * height)
        bitmap.getPixels(originalPixels, 0, width, 0, 0, width, height)
        val blurredPixels = gaussianBlur(originalPixels, width, height, radius)

        val resultPixels = IntArray(width * height)

        ImageProcessor(
            originalPixels,
            width,
            height
        ) { x, y, color ->
            val i = y * width + x
            val blurredColor = blurredPixels[i]

            val redDiff = Color.red(color) - Color.red(blurredColor)
            val greenDiff = Color.green(color) - Color.green(blurredColor)
            val blueDiff = Color.blue(color) - Color.blue(blurredColor)

            if ((abs(redDiff) > threshold) || (abs(greenDiff) > threshold) || (abs(blueDiff) > threshold)) {
                val newRed = min(255, max(0, Color.red(color) + (redDiff * amount).toInt()))
                val newGreen = min(255, max(0, Color.green(color) + (greenDiff * amount).toInt()))
                val newBlue = min(255, max(0, Color.blue(color) + (blueDiff * amount).toInt()))

                resultPixels[i] = Color.rgb(newRed, newGreen, newBlue)
            } else {
                resultPixels[i] = color
            }
        }.process().join()

        onEnd(generateUri(Bitmap.createBitmap(resultPixels, width, height, Bitmap.Config.ARGB_8888)))
    }
}
