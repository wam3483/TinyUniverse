package com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.ui.PiecewiseModel
import kotlin.math.sin

class SineWaveform(frequency:FloatInputStream, var samplingRate : Int) : AbstractWaveformStream(frequency, samplingRate) {
    override fun mapPhaseToAmplitude(phase: Float): Float {
        return sin(phase)
    }
}