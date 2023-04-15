package com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream

interface EffectFactory {
    fun new(inputStream : FloatInputStream) : FloatInputStream
}