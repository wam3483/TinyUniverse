package com.pixelatedmind.game.tinyuniverse.generation.music.waveform

import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream

class SquareWaveform(frequency : Float,amplitude : Float=1f, phaseShift : Float = 0f) : AbstractShapeWaveform(frequency,amplitude, phaseShift) {

    override fun read(timeInSeconds: Float): Float {
        val halfPeriod = period / 2
        val phase = (timeInSeconds+periodShift) % period
        if(phase < halfPeriod){
            return amplitude
        }
        return -amplitude
    }

}