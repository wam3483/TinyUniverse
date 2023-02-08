package com.pixelatedmind.game.tinyuniverse.generation.music.waveform

import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream
import kotlin.math.sin

class SineWaveform(frequency:Float, amplitude:Float=1f, phaseShift : Float=0f) : AbstractShapeWaveform(frequency,amplitude,  phaseShift) {
    private val frequencyMult = Math.PI*2
    override fun read(time:Float) : Float {
        return sin((time)*frequencyMult*frequency+(periodShift*frequencyMult)).toFloat()
    }
}