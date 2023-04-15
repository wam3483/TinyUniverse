package com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream

class SawtoothWaveform(frequency : FloatInputStream, sampleRate : Int, startPhase : Float = 0f) : AbstractWaveformStream(frequency,sampleRate) {
    init{
        phase = startPhase
    }
    override fun mapPhaseToAmplitude(elapsedTime : Float, phase: Float): Float {
        val normalizedPhase = (phase % onePeriod) / onePeriod
        return normalizedPhase * 2 - 1
    }

}