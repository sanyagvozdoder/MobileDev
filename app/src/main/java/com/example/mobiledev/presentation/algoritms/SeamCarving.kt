package com.example.mobiledev.presentation.algoritms

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import com.example.mobiledev.presentation.algoritms.util.toBitmap
import com.example.mobiledev.presentation.algoritms.util.transpose
import com.example.mobiledev.presentation.algoritms.util.updateScreen
import com.example.mobiledev.presentation.editorscreen.EditorScreenViewModel
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.math.min

// a.k.a. ЖМЫХ
@OptIn(ExperimentalEncodingApi::class)
fun SeamCarving(img:ByteArray?, viewModelInstance: EditorScreenViewModel, args:List<Int>){
    GlobalScope.launch {
        val bitmap = toBitmap(img)
        var pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        bitmap.recycle()

        val targetWidth = bitmap.width - args[0]
        val targetHeight = bitmap.height - args[0]
        var cWidth = bitmap.width
        var cHeight = bitmap.height

        while (cWidth > targetWidth) {
            pixels = removeVerticalSeam(pixels, cWidth, cHeight)
            cWidth--
        }

        while (cHeight > targetHeight) {
            pixels = removeHorizontalSeam(pixels, cWidth, cHeight)
            cHeight--
        }

        val outputBitmap =
            Bitmap.createBitmap(cWidth, cHeight, Bitmap.Config.ARGB_8888)
        outputBitmap.setPixels(pixels, 0, cWidth, 0, 0, cWidth, cHeight)
        updateScreen(outputBitmap, viewModelInstance)
    }
}

fun removeVerticalSeam(pixels: IntArray, width:Int, height:Int): IntArray {
    val outputPixels = IntArray((width - 1) * height)
    val energy = IntArray(width * height)
    val cumulativeEnergy = IntArray(width * height)
    val seamPath = IntArray(height)

    for (y in 0 until height) {
        for (x in 0 until width) {
            val i = y * width + x
            energy[i] = computeEnergy(pixels, width, height, x, y)
        }
    }

    for (x in 0 until width) {
        cumulativeEnergy[x] = energy[x]
    }

    for (y in 1 until height) {
        for (x in 0 until width) {
            val i = y * width + x
            cumulativeEnergy[i] = energy[i] + minEnergy(cumulativeEnergy, width,y - 1, x)
        }
    }

    val offset = width * height - 1
    var minEnergy = cumulativeEnergy[offset]
    repeat(width) { i ->
        val x = offset - i
        minEnergy = min(minEnergy, cumulativeEnergy[x])
    }

    for (y in height - 2 downTo  0) {
        seamPath[y] = findMinIndex(cumulativeEnergy, width, y, seamPath[y + 1])
    }

    for (y in 0 until height) {
        var newX = 0
        for (x in 0 until width) {
            if (x != seamPath[y]) {
                outputPixels[y * (width - 1) + newX] = pixels[y * width + x]
                newX++
            }
        }
    }

    return outputPixels
}

fun removeHorizontalSeam(pixels: IntArray, width: Int, height: Int): IntArray {
    val rotatedBitmap = rotate(pixels, width, height, false)
    val carvedBitmap = removeVerticalSeam(rotatedBitmap, height, width)
    return rotate(carvedBitmap, height - 1, width,  true)
}

fun rotate(pixels: IntArray, width: Int, height: Int, right: Boolean = false): IntArray {
    val outputPixels = IntArray(width * height)
    for (y in 0 until height) {
        for (x in 0 until width) {
            var outX = y
            var outY = width - x - 1

            if(right)
            {
                outX = height - y - 1
                outY = x
            }

            val oi = outY * height + outX
            val i = y * width + x
            outputPixels[oi] = pixels[i]
        }
    }

    return outputPixels
}

fun computeEnergy(pixels: IntArray, width: Int, height: Int, x: Int, y: Int): Int {
    val left = pixels[y * width + maxOf(0, x - 1)]
    val right = pixels[y * width + minOf(width - 1, x + 1)]
    val up = pixels[maxOf(0, y - 1) * width + x]
    val down = pixels[minOf(height - 1, y + 1) * width + x]

    val dx = colorDistance(left, right)
    val dy = colorDistance(up, down)
    return dx + dy
}

fun colorDistance(c1: Int, c2: Int): Int {
    val dr = Color.red(c1) - Color.red(c2)
    val dg = Color.green(c1) - Color.green(c2)
    val db = Color.blue(c1) - Color.blue(c2)
    return dr * dr + dg * dg + db * db
}

fun minEnergy(energy: IntArray, width: Int, y: Int, x: Int): Int {
    val i = y * width + x
    var minEnergy = energy[i]
    if (x > 0) minEnergy = minOf(minEnergy, energy[i - 1])
    if (x < width - 1) minEnergy = minOf(minEnergy, energy[i + 1])
    return minEnergy
}

fun findMinIndex(energy: IntArray, width: Int, y: Int, x: Int): Int {
    val i = y * width + x
    var minIndex = x
    var minEnergy = energy[i]

    if (x > 0 && energy[i - 1] < minEnergy) {
        minIndex = x - 1
        minEnergy = energy[i - 1]
    }

    if (x < width - 1 && energy[i + 1] < minEnergy) {
        minIndex = x + 1
    }

    return minIndex
}