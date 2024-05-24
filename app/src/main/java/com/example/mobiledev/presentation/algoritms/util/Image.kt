package com.example.mobiledev.presentation.algoritms.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import com.example.mobiledev.presentation.editorscreen.EditorScreenViewModel
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

fun toBitmap(data:ByteArray?):Bitmap {
    var bitmap = BitmapFactory.decodeByteArray(data,0, data?.size?:0)
    return bitmap.copy(Bitmap.Config.ARGB_8888,true)
}

fun toByteArray(data:Bitmap?):ByteArray {
    val stream = ByteArrayOutputStream()
    data?.compress(Bitmap.CompressFormat.JPEG,90,stream)
    return stream.toByteArray()
}

fun readRGBA(pixel:Int):Rgb {
    return Rgb(Color.red(pixel), Color.green(pixel), Color.blue(pixel), Color.alpha(pixel))
}

fun writeRGBA(pixel:Rgb):Int {
    return Color.argb(pixel.alpha, pixel.red, pixel.green, pixel.blue)
}

/*fun updateScreen(image:Bitmap, viewModelInstance: EditorScreenViewModel){
    viewModelInstance.onStateUpdate(generateUri(image))
}*/

fun generateUri(image:Bitmap): Uri {
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val file = File.createTempFile("newImage", ".jpg", downloadsDir)
    val ostream = FileOutputStream(file)
    ostream.write(toByteArray(image))
    ostream.close()
    image.recycle()
    return Uri.fromFile(file)
}

fun transpose(bitmap: Bitmap, right: Boolean = false) =
    GlobalScope.async<Bitmap>(Dispatchers.Default, start = CoroutineStart.LAZY) {
        val outputWidth = bitmap.height
        val outputHeight = bitmap.width
        val outputBitmap =
                Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888)

        val outputPixels = IntArray(outputWidth * outputHeight)

        val processPixel = { x: Int, y: Int, color: Int ->
            var outX = y
            var outY = outputHeight - x - 1

            if(right)
            {
                outX = outputWidth - y - 1
                outY = x
            }

            val i = outY * outputWidth + outX
            outputPixels[i] = color
        }

        val makeNewBitmap: () -> Unit = {
            bitmap.recycle()

            outputBitmap.setPixels(outputPixels, 0, outputWidth, 0, 0, outputWidth, outputHeight)

        }

        val config = ImageProcessorConfig(bitmap, processPixel, 200, "ALGO_ROTATE")
        val processor = ImageProcessor(config = config)
        processor.process(makeNewBitmap).join()
        outputBitmap
}