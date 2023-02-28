package com.pixelatedmind.game.tinyuniverse.generation.music

class FloatInputStreamReader(val stream : FloatInputStream, samplingFrequency : Int) {
    //Read cursor for stream in time.
    var streamTimeSecs = 0.0f
    val timePassedPerSample = 1f / samplingFrequency
    fun read(buffer : FloatArray, offset:Int, size:Int){
        var i = offset
        val end = offset + size
        while(i<end){
            buffer[i] = stream.read(streamTimeSecs)
            streamTimeSecs += timePassedPerSample
            i++
        }
    }
}