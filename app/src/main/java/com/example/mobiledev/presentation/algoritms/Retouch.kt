package com.example.mobiledev.presentation.algoritms

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.util.Log
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.core.graphics.set
import com.example.mobiledev.presentation.algoritms.util.ImageProcessor
import com.example.mobiledev.presentation.algoritms.util.Rgb
import com.example.mobiledev.presentation.algoritms.util.generateUri
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



fun applyRetouch(img:ByteArray?, strengthPercentage: Float, onEnd: (Uri) -> Unit,
                 points:List<Offset>, radius:Int, workSpaceSize: IntSize
) {
    GlobalScope.launch {
        val image = toBitmap(img);
        val strength = strengthPercentage / 100f // 0..1
        val width = image.width
        val height = image.height
        val size = width * height

        val coefficientX = workSpaceSize.width.toFloat() / image.width
        val coefficientY = workSpaceSize.height.toFloat() / image.height

        val originalPixels = IntArray(size)
        val retouchedPixels = IntArray(size)

        image.getPixels(originalPixels, 0, width, 0, 0, width, height)
        image.getPixels(retouchedPixels, 0, width, 0, 0, width, height)

        for (y in 0 until height) {
            for (x in 0 until width) {

                val wsX = x * coefficientX
                val wsY = y * coefficientY

                val factor = getMaskFactor(points, radius, Offset(wsX, wsY))

                if (factor <= 0.01f) continue
                val centerIndex = y * width + x

                val neighbors = mutableListOf<Int>()
                for (dy in -2..2) {
                    for (dx in -2..2) {
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

                val retouchedColor = calculateRetouchedColor(
                    originalPixels[centerIndex],
                    neighbors,
                    strength * factor
                )
                retouchedPixels[centerIndex] = retouchedColor
            }
        }

        onEnd(
            generateUri(Bitmap.createBitmap(retouchedPixels, width, height, Bitmap.Config.ARGB_8888))
        )
    }
}

fun getMaskFactor(spline: List<Offset>, radius: Int, p0: Offset) : Float {
    var minDist = radius + 100f

    // https://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line
    spline.forEachIndexed { i, p1 ->
        val dx = p0.x - p1.x
        val dy = p0.y - p1.y
        val distance = sqrt(dx * dx + dy * dy)
        minDist = minOf(minDist, distance)
    }

    return exp(-(minDist * minDist) / (2 * radius * radius)).toFloat();
}

fun calculateRetouchedColor(centerColor: Int, neighbors: List<Int>, strength: Float): Int {
    val red = Color.red(centerColor)
    val green = Color.green(centerColor)
    val blue = Color.blue(centerColor)

    var redSum = red
    var greenSum = green
    var blueSum = blue
    var weightSum = 1

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