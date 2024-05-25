package com.example.mobiledev.presentation.algoritms.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

fun createAppDirectoryIfNotExists(): File {
    val folder = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString() + "/HitsPhotoApp"
    )
    folder.mkdirs()

    return folder
}

fun getTmpDirectory(): File {
    val folder = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString() + "/HitsPhotoAppTEMP"
    )
    folder.mkdirs()

    return folder
}

fun cleanTmpDirectory() {
    getTmpDirectory().deleteRecursively()
}

fun toBitmap(data: ByteArray?): Bitmap {
    var bitmap = BitmapFactory.decodeByteArray(data, 0, data?.size ?: 0)
    return bitmap.copy(Bitmap.Config.ARGB_8888, true)
}

fun toByteArray(data: Bitmap?): ByteArray {
    val stream = ByteArrayOutputStream()
    data?.compress(Bitmap.CompressFormat.JPEG, 90, stream)
    return stream.toByteArray()
}

fun readARGB(pixel: Int): Rgba {
    return Rgba(Color.red(pixel), Color.green(pixel), Color.blue(pixel), Color.alpha(pixel))
}

fun writeARGB(pixel: Rgba): Int {
    return Color.argb(pixel.alpha, pixel.red, pixel.green, pixel.blue)
}

fun generateUri(image: Bitmap): Uri {
    val file = saveToFile(image, "newImage", ".jpg", getTmpDirectory())
    file.deleteOnExit()
    return Uri.fromFile(file)
}

fun saveToFile(image: Bitmap, prefix: String, suffix: String, directory: File): File {
    val file = File.createTempFile(prefix, suffix, directory)
    val ostream = FileOutputStream(file)
    ostream.write(toByteArray(image))
    ostream.close()
    image.recycle()
    return file
}