package com.pixelatedmind.game.tinyuniverse.generation.music.waveform

import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream
import kotlin.math.abs

class TriangleWaveForm(frequency : Float, amplitude : Float=1f, phaseShift : Float = 0f): AbstractShapeWaveform(frequency,amplitude, phaseShift) {
    override fun read(timeInSeconds: Float): Float {
        val halfPeriod = period / 2
        return amplitude / halfPeriod * (halfPeriod - abs((timeInSeconds+periodShift) % (2*halfPeriod)-halfPeriod))
    }
}