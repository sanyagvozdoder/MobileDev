package com.example.mobiledev.presentation.algoritms

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.example.mobiledev.presentation.algoritms.util.toBitmap
import com.example.mobiledev.presentation.algoritms.util.updateScreen
import com.example.mobiledev.presentation.retouchscreen.RetouchScreenViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.sqrt


fun applyRetouch(img:ByteArray?, viewModelInstance: RetouchScreenViewModel,
                 points:List<Offset>, radius:Int, workSpaceSize: IntSize) {
    GlobalScope.launch {
        val image = toBitmap(img);
        val strength = 1f // 0..1
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

                val retouchedColor = calculateRetouchedColor(
                    originalPixels[centerIndex],
                    neighbors,
                    strength * minOf(factor + 0.5f, 1f)
                )
                retouchedPixels[centerIndex] = retouchedColor
            }
        }

        updateScreen(
            Bitmap.createBitmap(retouchedPixels, width, height, Bitmap.Config.ARGB_8888),
            viewModelInstance
        )
    }
}

fun getMaskFactor(spline: List<Offset>, radius: Int, p0: Offset) : Float {
    var minDist = radius + 100f

    // https://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line
    spline.forEachIndexed { i, p1 ->
        /*if(i > 0)
        {
            val p1 = spline[i - 1]
            val p2 = spline[i]

            if(p0.x in p1.x - radius..p2.x + radius
                || p0.x in p2.x - radius..p1.x + radius
                && p0.y in p1.y - radius..p2.y + radius
                || p0.y in p2.y - radius..p1.y + radius)
            {
                val dx = p2.x - p1.x
                val dy = p2.y - p1.y
                val len = sqrt(dx * dx + dy * dy)
                val verx = abs((p2.x - p1.x) * (p0.y - p1.y) -
                        (p0.x - p1.x) * (p2.y - p1.y))

                val distance = verx / len

                if(!distance.isNaN())
                    minDist = minOf(minDist, distance)
            }
            //Log.d("ЖОПА СРАКА", "len: " + len.toString() + "verx:" + verx.toString() + " d: " + distance.toString())
        }*/
        val dx = p0.x - p1.x
        val dy = p0.y - p1.y
        val distance = sqrt(dx * dx + dy * dy)
        minDist = minOf(minDist, distance)
    }

    return 1f - minOf(minDist / radius, 1f)
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