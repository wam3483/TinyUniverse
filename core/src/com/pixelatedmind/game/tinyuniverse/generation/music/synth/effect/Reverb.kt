package com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream

class Reverb(private val inputStream : FloatInputStream,
             delaySecs : Double,
             sampleRate : Double,
             private val decayStream : FloatInputStream) : FloatInputStream {
    val reverbEffect : ReverbEffect
    init{
        reverbEffect = ReverbEffect(delaySecs, sampleRate, .1)
    }
    override fun read(timeInSeconds: Float): Float {
        val decayValue = decayStream.read(timeInSeconds)
        reverbEffect.setDecay(decayValue.toDouble())

        val inputValue = inputStream.read(timeInSeconds)
        val result = reverbEffect.processSample(inputValue.toDouble())
        return result.toFloat()
    }
}