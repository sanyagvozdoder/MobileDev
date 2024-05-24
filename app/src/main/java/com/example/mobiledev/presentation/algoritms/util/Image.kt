package com.example.mobiledev.presentation.algoritms.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import com.example.mobiledev.presentation.retouchscreen.RetouchScreenViewModel
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import okhttp3.internal.publicsuffix.PublicSuffixDatabase
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

fun createAppDirectoryIfNotExists() : File{
    val folder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/HitsPhotoApp")
    folder.mkdirs()

    return folder
}
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

fun generateUri(image:Bitmap): Uri {
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val file = saveToFile(image, "newImage", ".jpg", downloadsDir)
    file.deleteOnExit()
    return Uri.fromFile(file)
}

fun saveToFile(image: Bitmap, prefix: String, suffix:String, directory: File) : File {
    val file = File.createTempFile(prefix, suffix, directory)
    val ostream = FileOutputStream(file)
    ostream.write(toByteArray(image))
    ostream.close()
    image.recycle()
    return file
}