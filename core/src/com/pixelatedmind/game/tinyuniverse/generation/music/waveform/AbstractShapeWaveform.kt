package com.pixelatedmind.game.tinyuniverse.generation.music.waveform

import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.generation.music.values.AnimatedValue

abstract class AbstractShapeWaveform(var frequency : AnimatedValue, val amplitude : Float=1f, var phaseShift : Float=0f) : FloatInputStream {
    init{
        setFrequency(frequency)
    }

    @JvmName("setFrequency1")
    fun setFrequency(frequency : AnimatedValue){
        this.frequency = frequency
    }
}