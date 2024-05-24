package com.example.mobiledev.presentation.algoritms.util

import android.graphics.Bitmap
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch


class ImageProcessor(private val width: Int,
                     private val height: Int,
                     private val func: (x:Int, y:Int, color: Int) -> Unit) {

    private var inPixels = IntArray(width * height)

    constructor(bitmap: Bitmap, func: (x:Int, y:Int, color: Int) -> Unit) :
            this(bitmap.width, bitmap.height, func)
    {
        bitmap.getPixels(inPixels, 0, width, 0, 0, width, height)
    }

    constructor(pixels: IntArray, width: Int, height: Int, func: (x:Int, y:Int, color: Int) -> Unit) :
            this(width, height, func)
    {
        inPixels = pixels
    }

    private fun processLine(x0: Int, length:Int, width:Int, height:Int) =
        GlobalScope.launch(Dispatchers.Default, start = CoroutineStart.LAZY) {

        for (x in x0 until x0 + length)
        {
            for(y in 0 until height){
                val i = y * width + x
                func.invoke(x, y, inPixels[i])
            }
        }
    }

    suspend fun process() =
        GlobalScope.launch(start = CoroutineStart.LAZY) {
        val jobs = mutableListOf<Job>()

        val processorsNum = Runtime.getRuntime().availableProcessors()
        val lineWidth = width / processorsNum

        for(start in 0 until width step lineWidth)
        {
            val length = minOf(lineWidth, width - start)
            jobs.add(processLine(start, length, width, height))
        }
        jobs.joinAll()
    }
}