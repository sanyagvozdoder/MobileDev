package com.example.mobiledev.presentation.algoritms

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.example.mobiledev.presentation.algoritms.util.generateUri
import com.example.mobiledev.presentation.algoritms.util.toBitmap


fun ApplyAffineTransform(img:ByteArray?, dotsStart: List<Offset>, dotsEnd: List<Offset>,
                         workSpaceSizeStart: IntSize,
                         workSpaceSizeEnd: IntSize,
                         onEnd: (Uri) -> Unit)
{

    val image = toBitmap(img)

    val coeffXS = image.width / workSpaceSizeStart.width.toFloat()
    val coeffYS = image.height / workSpaceSizeStart.height.toFloat()
    val coeffXE = image.width / workSpaceSizeEnd.width.toFloat()
    val coeffYE = image.height / workSpaceSizeEnd.height.toFloat()

    var sourcePoints = mutableListOf<Offset>()

    for (offset in dotsStart) {
        sourcePoints.add(Offset(
            offset.x * coeffXS,
            offset.y * coeffYS
        ))
    }

    val targetPoints = mutableListOf<Offset>()

    for (offset in dotsEnd) {
        targetPoints.add(Offset(
            offset.x * coeffXE,
            offset.y * coeffYE
        ))
    }

    val height = image.height
    val width = image.width
    val matrix = calculateAffineMatrix(sourcePoints, targetPoints)
    val originalPixels = IntArray(width * height)

    image.getPixels(originalPixels, 0, width, 0, 0, width, height)



    val targetPixels = IntArray(width * height)


    for (y in 0 until height) {
        for (x in 0 until width) {

            val transformedX = matrix[0] * x + matrix[1] * y + matrix[2]
            val transformedY = matrix[3] * x + matrix[4] * y + matrix[5]

            if (transformedX >= 0 && transformedX < width && transformedY >= 0 && transformedY < height) {



                val interpolatedColor = bilinearInterpolation(originalPixels,width, transformedX.toInt(), transformedY.toInt())

                targetPixels[y * width + x] = interpolatedColor
            } else {

                targetPixels[y * width + x] = Color.TRANSPARENT
            }
        }
    }

    val outputBitmap =
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    outputBitmap.setPixels(targetPixels, 0,width, 0, 0, width, height)
    onEnd(generateUri(outputBitmap))
}

fun calculateAffineMatrix(sourcePoints: List<Offset>, targetPoints: List<Offset>): FloatArray {


    val matrix = FloatArray(9)


    val a = targetPoints[0].x * (sourcePoints[1].y - sourcePoints[2].y) +
            targetPoints[1].x * (sourcePoints[2].y - sourcePoints[0].y) +
            targetPoints[2].x * (sourcePoints[0].y - sourcePoints[1].y)

    val b = targetPoints[0].y * (sourcePoints[1].y - sourcePoints[2].y) +
            targetPoints[1].y * (sourcePoints[2].y - sourcePoints[0].y) +
            targetPoints[2].y * (sourcePoints[0].y - sourcePoints[1].y)

    val c = sourcePoints[0].x * (targetPoints[1].y - targetPoints[2].y) +
            sourcePoints[1].x * (targetPoints[2].y - targetPoints[0].y) +
            sourcePoints[2].x * (targetPoints[0].y - targetPoints[1].y)

    val d = sourcePoints[0].y * (targetPoints[1].y - targetPoints[2].y) +
            sourcePoints[1].y * (targetPoints[2].y - targetPoints[0].y) +
            sourcePoints[2].y * (targetPoints[0].y - targetPoints[1].y)

    val e = sourcePoints[0].x * (targetPoints[1].x - targetPoints[2].x) +
            sourcePoints[1].x * (targetPoints[2].x - targetPoints[0].x) +
            sourcePoints[2].x * (targetPoints[0].x - targetPoints[1].x)

    val f = sourcePoints[0].y * (targetPoints[1].x - targetPoints[2].x) +
            sourcePoints[1].y * (targetPoints[2].x - targetPoints[0].x) +
            sourcePoints[2].y * (targetPoints[0].x - targetPoints[1].x)

    val det = (sourcePoints[1].x - sourcePoints[0].x) * (sourcePoints[2].y - sourcePoints[0].y) -
            (sourcePoints[2].x - sourcePoints[0].x) * (sourcePoints[1].y - sourcePoints[0].y)

    matrix[0] = (a / det)
    matrix[1] = (b / det)
    matrix[2] = (c / det)
    matrix[3] = (d / det)
    matrix[4] = (e / det)
    matrix[5] = (f / det)
    matrix[6] = 0f
    matrix[7] = 0f
    matrix[8] = 1f

    return matrix
}


fun bilinearInterpolation(pixels: IntArray, width: Int, x: Int, y: Int): Int {

    if (x < 0 || x >= width || y < 0 || y >= pixels.size / width) {
        return 0
    }

    val x0 = x
    val y0 = y
    val x1 = if (x0 < width - 1) x0 + 1 else x0
    val y1 = if (y0 < pixels.size / width - 1) y0 + 1 else y0


    val wx = x.toFloat() - x0.toFloat()
    val wy = y.toFloat() - y0.toFloat()


    val p00 = y0 * width + x0
    val p01 = y0 * width + x1
    val p10 = y1 * width + x0
    val p11 = y1 * width + x1

    val r = (1 - wx) * (1 - wy) * Color.red(pixels[p00]) +
            wx * (1 - wy) * Color.red(pixels[p01]) +
            (1 - wx) * wy * Color.red(pixels[p10]) +
            wx * wy * Color.red(pixels[p11])

    val g = (1 - wx) * (1 - wy) * Color.green(pixels[p00]) +
            wx * (1 - wy) * Color.green(pixels[p01]) +
            (1 - wx) * wy * Color.green(pixels[p10]) +
            wx * wy * Color.green(pixels[p11])

    val b = (1 - wx) * (1 - wy) * Color.blue(pixels[p00]) +
            wx * (1 - wy) * Color.blue(pixels[p01]) +
            (1 - wx) * wy * Color.blue(pixels[p10]) +
            wx * wy * Color.blue(pixels[p11])

    return Color.rgb(r.toInt(), g.toInt(), b.toInt())
}