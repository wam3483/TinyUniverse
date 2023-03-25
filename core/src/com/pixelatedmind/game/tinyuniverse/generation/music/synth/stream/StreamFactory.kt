package com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream

interface StreamFactory {
    fun new(streamId : String) : FloatInputStream
}