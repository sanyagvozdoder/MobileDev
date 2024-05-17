package com.example.mobiledev.presentation.algoritms

import android.graphics.Bitmap
import android.graphics.Color
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

@OptIn(ExperimentalEncodingApi::class)
fun Rotate(img:ByteArray?, viewModelInstance: EditorScreenViewModel, args:List<Int>) {
    GlobalScope.launch {
        val bitmap = toBitmap(img)
        updateScreen(transpose(bitmap, false).await(), viewModelInstance)
    }
}

