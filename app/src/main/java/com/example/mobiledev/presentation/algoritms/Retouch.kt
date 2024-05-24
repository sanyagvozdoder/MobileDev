package com.example.mobiledev.presentation.algoritms

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.util.Log
import androidx.compose.material3.surfaceColorAtElevation
import androidx.core.graphics.set
import com.example.mobiledev.presentation.algoritms.util.ImageProcessor
import com.example.mobiledev.presentation.algoritms.util.ImageProcessorConfig
import com.example.mobiledev.presentation.algoritms.util.Rgb
import com.example.mobiledev.presentation.algoritms.util.generateUri
import com.example.mobiledev.presentation.algoritms.util.readRGBA
import com.example.mobiledev.presentation.algoritms.util.toBitmap
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



@OptIn(ExperimentalEncodingApi::class)

fun Retouch(img:ByteArray?, onEnd: (Uri?) -> Unit, args:List<Int>) {

    val image = toBitmap(img);
    val strength : Float = args[0].toFloat() / 100
    val width = image.width
    val height = image.height
    val size = width * height

    val originalPixels = IntArray(size)
    image.getPixels(originalPixels, 0, width, 0, 0, width, height)

    val retouchedPixels = IntArray(size)
    for (y in 0 until height) {
        for (x in 0 until width) {
            val centerIndex = y * width + x

            val neighbors = mutableListOf<Int>()
            for (dy in -1..1) {
                for (dx in -1..1) {
                    if (dx == 0 && dy == 0) {
                        continue
                    }
                    val neighborX = x + dx
                    val neighborY = y + dy
                    if (neighborX in 0 until width && neighborY in 0 until height) {
                        neighbors.add(originalPixels[neighborY * width + neighborX])
                    }
                }
            }

            val retouchedColor = calculateRetouchedColor(originalPixels[centerIndex], neighbors, strength)
            retouchedPixels[centerIndex] = retouchedColor
        }
    }

    onEnd(generateUri(Bitmap.createBitmap(retouchedPixels, width, height, Bitmap.Config.ARGB_8888)))
}


fun calculateRetouchedColor(centerColor: Int, neighbors: List<Int>, strength: Float): Int {
    val red = Color.red(centerColor)
    val green = Color.green(centerColor)
    val blue = Color.blue(centerColor)

    var redSum = red * 8
    var greenSum = green * 8
    var blueSum = blue * 8
    var weightSum = 8

    for (neighbor in neighbors) {
        redSum += Color.red(neighbor)
        greenSum += Color.green(neighbor)
        blueSum += Color.blue(neighbor)
        weightSum += 1
    }

    val avgRed = redSum.toFloat() / weightSum
    val avgGreen = greenSum.toFloat() / weightSum
    val avgBlue = blueSum.toFloat() / weightSum

    val newRed = ((1 - strength) * red + strength * avgRed).toInt()
    val newGreen = ((1 - strength) * green + strength * avgGreen).toInt()
    val newBlue = ((1 - strength) * blue + strength * avgBlue).toInt()

    return Color.rgb(newRed, newGreen, newBlue)
}