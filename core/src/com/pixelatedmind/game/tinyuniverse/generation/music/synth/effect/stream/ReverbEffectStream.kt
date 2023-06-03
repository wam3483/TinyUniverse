package com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect.stream

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect.ReverbEffect
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream

class ReverbEffectStream(val effect : ReverbEffect, val inputStream : FloatInputStream) : FloatInputStream {
    override fun read(timeInSeconds: Float): Float {
        val sample = inputStream.read(timeInSeconds)
        val result = effect.processSample(sample.toDouble())
        return result.toFloat()
    }

}