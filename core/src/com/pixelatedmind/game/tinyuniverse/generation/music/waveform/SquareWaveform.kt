package com.pixelatedmind.game.tinyuniverse.generation.music.waveform

import com.pixelatedmind.game.tinyuniverse.generation.music.values.ConstantValue
import com.pixelatedmind.game.tinyuniverse.generation.music.values.AnimatedValue

class SquareWaveform(frequency : AnimatedValue, amplitude : Float=1f, phaseShift : Float = 0f, var pulseWidth : AnimatedValue = ConstantValue(.5f)) :
        AbstractShapeWaveform(frequency,amplitude, phaseShift) {

    override fun read(timeInSeconds: Float): Float {
        val freq = frequency.getValue(timeInSeconds)
        val period = 1 / freq
        val periodShift = phaseShift * period

        val pulseWidthVal = pulseWidth.getValue(timeInSeconds)
        val halfPeriod = period * pulseWidthVal
        val phase = (timeInSeconds+periodShift) % period
        if(phase < halfPeriod){
            return amplitude
        }
        return -amplitude
    }
}