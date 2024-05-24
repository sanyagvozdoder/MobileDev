package com.example.mobiledev.presentation.algoritms.util

import android.graphics.Bitmap
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch


class ImageProcessor(private val input: Bitmap,
                     private val func: (x:Int, y:Int, color: Int) -> Unit) {
    private val inPixels = IntArray(input.width * input.height)

    private fun processLine(x0: Int, length:Int, width:Int, height:Int) = GlobalScope.launch(Dispatchers.Default,
        start = CoroutineStart.LAZY){

        for (x in x0 until x0 + length)
        {
            for(y in 0 until height){
                val i = y * width + x
                func.invoke(x, y, inPixels[i])
            }
        }
    }

    suspend fun process(onEnd:() -> Unit) = GlobalScope.launch(start = CoroutineStart.LAZY) {
        input.getPixels(inPixels, 0, input.width, 0, 0,
            input.width, input.height)
        val jobs = mutableListOf<Job>()

        val processorsNum = Runtime.getRuntime().availableProcessors()
        val lineWidth = input.width / processorsNum

        for(start in 0 until input.width step lineWidth)
        {
            val length = minOf(lineWidth, input.width - start)
            jobs.add(processLine(start, length, input.width, input.height))
        }
        jobs.joinAll()
        onEnd.invoke()
    }
}