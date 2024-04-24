package com.example.mobiledev.presentation.algoritms

import android.net.Uri
import android.os.Environment
import android.util.Base64
import com.example.mobiledev.presentation.editorscreen.EditorScreenViewModel
import java.io.File
import java.io.FileOutputStream
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun Scaling(img:ByteArray?, viewModelInstance:EditorScreenViewModel):Unit{

    var newImg:ByteArray = ByteArray(img?.size?:0)

    var ind = 0

    img?.forEach {byte->
        var outputByte = byte
        newImg.set(ind,outputByte)
        ind++
    }

    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

    val file = File.createTempFile("newImage", ".jpg", downloadsDir)

    val ostream = FileOutputStream(file)
    ostream.write(newImg)
    ostream.close()

    viewModelInstance.onStateUpdate(Uri.fromFile(file))
}