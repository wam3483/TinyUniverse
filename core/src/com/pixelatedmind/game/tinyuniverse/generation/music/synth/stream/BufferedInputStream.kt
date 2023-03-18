package com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream

import com.pixelatedmind.game.tinyuniverse.datastructure.CircularArray
import java.util.*

open class BufferedInputStream(private val inputStream : FloatInputStream, val sampleRate : Float = 441000f, private val numBuffers : Int = 10, private val bufferSize :Int = 10000) : FloatInputStream{
    //TODO i'll likely need to do buffering on a separate thread.

    val bufferCache = LinkedList<FloatArray>()
    var currentBufferIndex = 0
    var buffer =FloatArray(0)

    protected open fun nextBuffer(epoch : Float) : FloatArray {
        var i = 0
        var elapsedTime = epoch
        val timestep = 1 / sampleRate
        val buffer =
                if(bufferCache.isNotEmpty()) bufferCache.remove()
                else FloatArray(bufferSize)
        while(i<bufferSize){
            val sample = inputStream.read(elapsedTime)
            buffer[i] = sample
            elapsedTime += timestep
            i++
        }
        return buffer
    }

    override fun read(timeInSeconds: Float): Float {
        if(currentBufferIndex>=buffer.size){
            buffer = nextBuffer(timeInSeconds)
            currentBufferIndex = 0
        }
        val result = buffer[currentBufferIndex]
        currentBufferIndex++
        return result
    }
}