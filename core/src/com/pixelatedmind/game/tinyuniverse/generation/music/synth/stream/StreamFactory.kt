package com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream

interface StreamFactory {
    fun getWaveformIds() : List<String>
    fun new(streamId : String, frequencyStream : FloatInputStream) : FloatInputStream
}