package com.pixelatedmind.game.tinyuniverse.generation.music

class FloatInputStreamReader(val stream : FloatInputStream, samplingFrequency : Int) {
    var time = 0.0f
    val timePassedPerSample = 1f / samplingFrequency
    fun read(buffer : FloatArray, offset:Int, size:Int){
        var i = offset
        while(i<size){
            buffer[i] = stream.read(time)
            time += timePassedPerSample
            i++
        }
    }
}