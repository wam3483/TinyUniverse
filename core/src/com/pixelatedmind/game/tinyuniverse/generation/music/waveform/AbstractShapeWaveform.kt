package com.pixelatedmind.game.tinyuniverse.generation.music.waveform

import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.generation.music.values.NormalizedValue

abstract class AbstractShapeWaveform(var frequency : NormalizedValue, val amplitude : Float=1f, var phaseShift : Float=0f) : FloatInputStream {
    init{
        setFrequency(frequency)
    }

    @JvmName("setFrequency1")
    fun setFrequency(frequency : NormalizedValue){
        this.frequency = frequency
    }
}