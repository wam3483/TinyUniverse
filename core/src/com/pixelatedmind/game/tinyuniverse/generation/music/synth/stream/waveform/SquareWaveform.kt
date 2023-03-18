package com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.ConstantStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream

class SquareWaveform(var frequency : FloatInputStream, var pulseWidth : FloatInputStream = ConstantStream(.5f)) :
        FloatInputStream {

    override fun read(timeInSeconds: Float): Float {
        val freq = frequency.read(timeInSeconds)
        val period = 1 / freq

        val pulseWidthVal = pulseWidth.read(timeInSeconds)
        val halfPeriod = period * pulseWidthVal
        val phase = (timeInSeconds) % period
        if(phase < halfPeriod){
            return 1f
        }
        return -1f
    }
}