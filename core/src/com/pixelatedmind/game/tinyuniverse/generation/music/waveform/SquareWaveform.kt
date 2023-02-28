package com.pixelatedmind.game.tinyuniverse.generation.music.waveform

import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.generation.music.values.ConstantValue
import com.pixelatedmind.game.tinyuniverse.generation.music.values.NormalizedValue

class SquareWaveform(frequency : NormalizedValue,amplitude : Float=1f, phaseShift : Float = 0f, var pulseWidth : NormalizedValue = ConstantValue(.5f)) :
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