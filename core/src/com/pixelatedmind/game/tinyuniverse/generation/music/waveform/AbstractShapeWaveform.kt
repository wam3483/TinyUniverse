package com.pixelatedmind.game.tinyuniverse.generation.music.waveform

import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream

abstract class AbstractShapeWaveform(var frequency : Float, val amplitude : Float=1f, var phaseShift : Float=0f) : FloatInputStream {
    protected var period : Float = 0f
    protected var periodShift : Float = 0f
    init{
        setFrequency(frequency)
    }

    @JvmName("setPhaseShift1")
    fun setPhaseShift(phaseShift : Float){
        this.phaseShift = phaseShift
        periodShift = phaseShift * period
    }

    @JvmName("setFrequency1")
    fun setFrequency(frequency : Float){
        this.frequency = frequency
        period = 1 / frequency
        periodShift = phaseShift * period
    }
}