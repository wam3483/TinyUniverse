package com.pixelatedmind.game.tinyuniverse.generation.music

interface FloatInputStreamFactory {
    fun newInputStream(frequency : Float) : FloatInputStream
}