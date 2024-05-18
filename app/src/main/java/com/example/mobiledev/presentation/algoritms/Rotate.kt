package com.example.mobiledev.presentation.algoritms

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Size
import com.example.mobiledev.presentation.algoritms.util.ImageProcessor
import com.example.mobiledev.presentation.algoritms.util.ImageProcessorConfig
import com.example.mobiledev.presentation.algoritms.util.Rgb
import com.example.mobiledev.presentation.algoritms.util.toBitmap
import com.example.mobiledev.presentation.algoritms.util.transpose
import com.example.mobiledev.presentation.algoritms.util.updateScreen
import com.example.mobiledev.presentation.algoritms.util.writeRGBA
import com.example.mobiledev.presentation.editorscreen.EditorScreenViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@OptIn(ExperimentalEncodingApi::class)
fun Rotate(img:ByteArray?, viewModelInstance: EditorScreenViewModel, args:List<Int>) {
    GlobalScope.launch {
        val bitmap = toBitmap(img)
        var pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        val angleI = args[0]

        if(angleI == 0)
            return@launch

        val angle = angleI.toDouble()

        val dim = getNewDimensions(bitmap.width, bitmap.height, angle)
        val newWidth = dim.width
        val newHeight = dim.height

        pixels = rotateImage(pixels, bitmap.width, bitmap.height, angle)

        val outputBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
        outputBitmap.setPixels(pixels, 0, newWidth, 0, 0, newWidth, newHeight)
        updateScreen(outputBitmap, viewModelInstance)
    }
}

// https://ip76.ru/theory-and-practice/rotate-rect/
fun getNewDimensions(width: Int, height: Int, angleInDegrees: Double): Size {
    val radianAngle = Math.toRadians(angleInDegrees)

    val sin = abs(sin(radianAngle))
    val cos = abs(cos(radianAngle))

    val newHeight = (width * sin + height * cos).roundToInt()
    val newWidth = (width * cos + height * sin).roundToInt()

    return Size(newWidth, newHeight)
}

fun rotateImage(pixels: IntArray, width: Int, height: Int, angleInDegrees: Double): IntArray {
    val radianAngle = Math.toRadians(angleInDegrees)

    val sin = sin(radianAngle)
    val cos = cos(radianAngle)

    val dim = getNewDimensions(width, height, angleInDegrees)
    val newWidth = dim.width
    val newHeight = dim.height

    val rotatedImage = IntArray(newWidth * newHeight)

    val centerX = width / 2
    val centerY = height / 2
    val newCenterX = newWidth / 2
    val newCenterY = newHeight / 2

    repeat(newHeight) { y ->
        repeat(newWidth) { x ->
            val dx = x - newCenterX
            val dy = y - newCenterY

            val origX = (cos * dx + sin * dy + centerX).roundToInt()
            val origY = (-sin * dx + cos * dy + centerY).roundToInt()

            val origI = origX + origY * width
            val i = y * newWidth + x

            if (origX in 0 until width && origY in 0 until height)
            {
                rotatedImage[i] = pixels[origI]
            }
            else
            {
                rotatedImage[i] = Color.WHITE
            }
        }
    }

    return rotatedImage
}
