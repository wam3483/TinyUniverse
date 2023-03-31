package com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream

import com.pixelatedmind.game.tinyuniverse.generation.music.Notes

class CentOffsetStream(val centStream : FloatInputStream, val baseFrequencyStream : FloatInputStream, val noteUtil : Notes) : FloatInputStream {
    var lastTimestamp = 0f
    override fun read(timeInSeconds: Float): Float {
        val baseFrequency = baseFrequencyStream.read(timeInSeconds)
        val noteBandwidth = noteUtil.getNoteBandwidth(baseFrequency)
        val centOffset = centStream.read(timeInSeconds) * noteBandwidth
        //println("baseFrequency = "+baseFrequency)
        val result = baseFrequency + centOffset
        if(lastTimestamp == 0f || (timeInSeconds-lastTimestamp)>.05f){
            lastTimestamp = timeInSeconds
//            println("baseFrequency="+baseFrequency+" result="+result)
        }
        return result
    }
}