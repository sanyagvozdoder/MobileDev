package com.example.mobiledev.presentation.algoritms

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import com.example.mobiledev.presentation.algoritms.util.Rgb
import com.example.mobiledev.presentation.editorscreen.EditorScreenViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun Scaling(img:ByteArray?, viewModelInstance:EditorScreenViewModel):Unit{

    var bitmap = BitmapFactory.decodeByteArray(img,0, img?.size?:0)
    var mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888,true)

    var arrRgb = List<List<Rgb>>(bitmap.height){List<Rgb>(bitmap.width){Rgb(0,0,0,0)}}

    for (i in 0..<bitmap.height){
        for (j in 0..<bitmap.width){
            val pixel = bitmap.getPixel(j,i)
            arrRgb[i][j].red = Color.red(pixel)
            arrRgb[i][j].green = Color.green(pixel)
            arrRgb[i][j].blue = Color.blue(pixel)
            arrRgb[i][j].alpha = Color.alpha(pixel)

            arrRgb[i][j].red = 0
            arrRgb[i][j].green = 0
            arrRgb[i][j].blue = 0
            arrRgb[i][j].alpha = 255

            mutableBitmap.setPixel(
                j,
                i,
                Color.argb(arrRgb[i][j].alpha,arrRgb[i][j].red,arrRgb[i][j].green,arrRgb[i][j].blue)
            )
        }
    }

    val stream = ByteArrayOutputStream()
    mutableBitmap.compress(Bitmap.CompressFormat.JPEG,90,stream)
    val newImg = stream.toByteArray()

    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

    val file = File.createTempFile("newImage", ".jpg", downloadsDir)

    val ostream = FileOutputStream(file)
    ostream.write(newImg)
    ostream.close()

    viewModelInstance.onStateUpdate(Uri.fromFile(file))
}