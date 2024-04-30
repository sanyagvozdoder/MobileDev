package com.example.mobiledev.presentation.algoritms

import android.graphics.Bitmap
import android.graphics.Color
import com.example.mobiledev.presentation.algoritms.util.ImageProcessor
import com.example.mobiledev.presentation.algoritms.util.ImageProcessorConfig
import com.example.mobiledev.presentation.algoritms.util.Rgb
import com.example.mobiledev.presentation.algoritms.util.toBitmap
import com.example.mobiledev.presentation.algoritms.util.updateScreen
import com.example.mobiledev.presentation.algoritms.util.writeRGBA
import com.example.mobiledev.presentation.editorscreen.EditorScreenViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun Rotate(img:ByteArray?, viewModelInstance: EditorScreenViewModel) {
    GlobalScope.launch {
        val bitmap = toBitmap(img)

        val outputWidth = bitmap.height
        val outputHeight = bitmap.width

        val outputPixels = IntArray(outputWidth * outputHeight)

        val processPixel = { x: Int, y: Int, color: Int ->
            val outX = y
            val outY = outputHeight - x - 1

            val i = outY * outputWidth + outX
            outputPixels[i] = color
        }

        val makeNewBitmap = {
            bitmap.recycle()

            val outputBitmap =
                Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888)
            outputBitmap.setPixels(outputPixels, 0, outputWidth, 0, 0, outputWidth, outputHeight)
            updateScreen(outputBitmap, viewModelInstance)
        }

        val config = ImageProcessorConfig(bitmap, processPixel, 100, "ALGO_ROTATE")
        val processor = ImageProcessor(config = config)
        processor.process(makeNewBitmap).join()
    }
}