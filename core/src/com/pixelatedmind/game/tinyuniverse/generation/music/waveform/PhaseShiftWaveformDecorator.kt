package com.pixelatedmind.game.tinyuniverse.generation.music.waveform

import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream

class PhaseShiftWaveformDecorator(val stream : AbstractShapeWaveform, var phaseShift : Float) : FloatInputStream{
    override fun read(timeInSeconds: Float): Float {
        val timeOffset = (1/stream.frequency) * phaseShift
        return stream.read(timeInSeconds+timeOffset)
    }
}