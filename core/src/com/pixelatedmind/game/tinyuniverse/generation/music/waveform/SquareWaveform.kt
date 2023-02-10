package com.pixelatedmind.game.tinyuniverse.generation.music.waveform

import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream

class SquareWaveform(frequency : Float,amplitude : Float=1f, phaseShift : Float = 0f, var pulseWidth : Float = .5f) :
        AbstractShapeWaveform(frequency,amplitude, phaseShift) {

    override fun read(timeInSeconds: Float): Float {
        val halfPeriod = period * pulseWidth
        val phase = (timeInSeconds+periodShift) % period
        if(phase < halfPeriod){
            return amplitude
        }
        return -amplitude
    }

}