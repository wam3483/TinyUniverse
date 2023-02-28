package com.pixelatedmind.game.tinyuniverse.generation.music.waveform

import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.generation.music.values.NormalizedValue
import kotlin.math.abs

class TriangleWaveForm(frequency : NormalizedValue, amplitude : Float=1f, phaseShift : Float = 0f): AbstractShapeWaveform(frequency,amplitude, phaseShift) {
    override fun read(timeInSeconds: Float): Float {
        val period = 1f / frequency.getValue(timeInSeconds)
        val halfPeriod = period / 2
        val triangleFrom0To1 = amplitude / halfPeriod * (halfPeriod - abs(timeInSeconds % period-halfPeriod))
        return (triangleFrom0To1 * 2) - 1
    }
}