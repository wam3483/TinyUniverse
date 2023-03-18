package com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream
import kotlin.math.abs

class TriangleWaveForm(var frequency : FloatInputStream): FloatInputStream {
    override fun read(timeInSeconds: Float): Float {
        val period = 1f / frequency.read(timeInSeconds)
        val halfPeriod = period / 2
        val triangleFrom0To1 = 1f / halfPeriod * (halfPeriod - abs(timeInSeconds % period-halfPeriod))
        return (triangleFrom0To1 * 2) - 1
    }
}