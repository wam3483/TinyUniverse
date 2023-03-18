package com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream

interface FloatInputStream {
 fun read(timeInSeconds: Float) : Float
}