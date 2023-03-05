package com.pixelatedmind.game.tinyuniverse.generation.music.waveform

import com.pixelatedmind.game.tinyuniverse.generation.music.values.AnimatedValue
import kotlin.math.sin

class SineWaveform(frequency:AnimatedValue, amplitude:Float=1f, phaseShift : Float=0f) : AbstractShapeWaveform(frequency,amplitude,  phaseShift) {
    private val frequencyMult = Math.PI*2
    override fun read(timeInSeconds:Float) : Float {
        val freq = frequency.getValue(timeInSeconds)
        val period = 1 / freq
        val periodShift = phaseShift * period
        val result = sin((timeInSeconds)*frequencyMult*freq+(periodShift*frequencyMult)).toFloat()
        return result
    }
}