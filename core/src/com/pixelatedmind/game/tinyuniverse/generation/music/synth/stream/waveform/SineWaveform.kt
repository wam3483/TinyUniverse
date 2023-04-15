package com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.ui.PiecewiseModel
import kotlin.math.sin

class SineWaveform(frequency:FloatInputStream, var samplingRate : Int, startPhase : Float = 0f) : AbstractWaveformStream(frequency, samplingRate) {
    init{
        phase = startPhase
    }
    override fun mapPhaseToAmplitude(elapsedSecs : Float, phase: Float): Float {
        return sin(phase)
    }
}