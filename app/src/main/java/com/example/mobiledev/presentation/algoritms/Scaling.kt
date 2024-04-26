package com.example.mobiledev.presentation.algoritms

import android.graphics.Bitmap
import android.util.Log
import com.example.mobiledev.presentation.algoritms.util.Rgb
import com.example.mobiledev.presentation.algoritms.util.readRGBA
import com.example.mobiledev.presentation.algoritms.util.toBitmap
import com.example.mobiledev.presentation.algoritms.util.updateScreen
import com.example.mobiledev.presentation.algoritms.util.writeRGBA
import com.example.mobiledev.presentation.editorscreen.EditorScreenViewModel
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.math.roundToInt

data class ParallelScalerConfig(
    var input: Bitmap,
    var factor: Float,
    var chunkSize: Int = 100,
)

class ParallelScaler(private val config: ParallelScalerConfig) {
    private val outWidth = (config.input.width * config.factor).roundToInt()
    private val outHeight = (config.input.height * config.factor).roundToInt()
    private val sums = MutableList<MutableList<Rgb>>(outWidth){MutableList<Rgb>(outHeight){ Rgb(0, 0, 0, 0) } }
    private val quantities = MutableList<MutableList<Float>>(outWidth){MutableList<Float>(outHeight){0f}}
    private var totalChunks = 0
    private var processedChunks = 0

    private suspend fun processChunk(ox:Int, oy:Int) = GlobalScope.async<Boolean>(Dispatchers.Default, start = CoroutineStart.LAZY) {
        Log.d("ALGO_SCALING", "$ox x $oy working on " + Thread.currentThread().getId())
        for (lx in 0..config.chunkSize){
            val x = ox + lx
            if(x >= config.input.width)
                break
            val outX = (x * config.factor).toInt()

            for (ly in 0..config.chunkSize){
                val y = oy + ly
                if(y >= config.input.height)
                    break
                val outY = (y * config.factor).toInt()

                val pixel = readRGBA(config.input.getPixel(x, y))

                sums[outX][outY].red += pixel.red
                sums[outX][outY].green += pixel.green
                sums[outX][outY].blue += pixel.blue
                sums[outX][outY].alpha += pixel.alpha

                quantities[outX][outY]++
            }
        }
        processedChunks++

        true
    }

    suspend fun process() = GlobalScope.async<Bitmap>(start = CoroutineStart.LAZY) {


        val startTime = System.currentTimeMillis()
        val deferreds = mutableListOf<Deferred<Boolean>>()

        for(chunkX in 0
                until config.input.width + config.chunkSize
                step config.chunkSize){
            for(chunkY in 0
                    until config.input.height + config.chunkSize
                    step config.chunkSize){
                deferreds.add(processChunk(chunkX, chunkY))
                totalChunks++
            }
        }

        Log.d("ALGO_SCALING", "waiting for process")

        deferreds.awaitAll()

        Log.d("ALGO_SCALING", "total processed $processedChunks/$totalChunks")

        val outputBitmap = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888)

        val processingTime = System.currentTimeMillis() - startTime

        Log.d("ALGO_SCALING", "processing finished at " + Thread.currentThread().toString() +
                "in: " + processingTime.toString() + "ms. input colorSpace: " + config.input.colorSpace.toString() +
                ", output colorSpace: " + outputBitmap.colorSpace.toString())

        repeat(outWidth){ x ->
            repeat(outHeight){ y ->
                val pixel = sums[x][y]
                val normalizer = 1f / quantities[x][y]

                pixel.red = (pixel.red * normalizer).toInt()
                pixel.green = (pixel.green * normalizer).toInt()
                pixel.blue = (pixel.blue * normalizer).toInt()
                pixel.alpha = (pixel.alpha * normalizer).toInt()

                outputBitmap.setPixel(x, y, writeRGBA(pixel))
            }
        }

        outputBitmap
    }
}

// https://ru.wikipedia.org/wiki/%D0%9C%D0%B0%D1%81%D1%88%D1%82%D0%B0%D0%B1%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5_%D0%B8%D0%B7%D0%BE%D0%B1%D1%80%D0%B0%D0%B6%D0%B5%D0%BD%D0%B8%D1%8F
@OptIn(ExperimentalEncodingApi::class)
fun Scaling(img:ByteArray?, viewModelInstance:EditorScreenViewModel):Unit {
    GlobalScope.launch {
        val bitmap = toBitmap(img)

        val factor = 0.5f

        val config = ParallelScalerConfig(bitmap, factor, 100)
        val scaler = ParallelScaler(config = config)
        val outputBitmap = scaler.process().await()

        updateScreen(outputBitmap, viewModelInstance)
    }
}
