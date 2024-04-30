package com.example.mobiledev.presentation.algoritms.util

import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


data class ImageProcessorConfig (
    var input: Bitmap,
    var func: (x:Int, y:Int, color: Int) -> Unit,
    var chunkSize: Int = 100,
    var logTag: String = "PROCESSOR",
)

class ImageProcessor(private val config: ImageProcessorConfig) {
    private val inPixels = IntArray(config.input.width * config.input.height)
    private var totalChunks = 0
    private var processedChunks = 0

    private fun processChunk(ox:Int, oy:Int) = GlobalScope.launch(Dispatchers.Default,
        start = CoroutineStart.LAZY) {
        Log.d(config.logTag, "$ox x $oy working on " + Thread.currentThread().getId())

        for (lx in 0 until config.chunkSize){
            val x = ox + lx
            if(x >= config.input.width)
                break

            for (ly in 0 until config.chunkSize){
                val y = oy + ly
                if(y >= config.input.height)
                    break

                val i = y * config.input.width + x
                config.func.invoke(x, y, inPixels[i])
            }
        }
        processedChunks++
    }

    suspend fun process(onEnd:() -> Unit) = GlobalScope.launch(start = CoroutineStart.LAZY) {
        config.input.getPixels(inPixels, 0, config.input.width, 0, 0,
            config.input.width, config.input.height)
        val jobs = mutableListOf<Job>()

        for(chunkX in 0
                until config.input.width + config.chunkSize
                step config.chunkSize){
            for(chunkY in 0
                    until config.input.height + config.chunkSize
                    step config.chunkSize){
                jobs.add(processChunk(chunkX, chunkY))
                totalChunks++
            }
        }

        Log.d(config.logTag, "waiting for process")

        jobs.joinAll()

        Log.d(config.logTag, "total processed $processedChunks/$totalChunks")

        onEnd.invoke()
    }
}