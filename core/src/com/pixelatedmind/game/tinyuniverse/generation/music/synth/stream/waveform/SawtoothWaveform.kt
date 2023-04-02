package com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream

class SawtoothWaveform(frequency : FloatInputStream, sampleRate : Int) : AbstractWaveformStream(frequency,sampleRate) {
    override fun mapPhaseToAmplitude(elapsedSecs : Float, phase: Float): Float {
        val normalizedPhase = phase % onePeriod
        return normalizedPhase * 2 - 1
//        val freq = frequency.read(timeInSeconds)
//        val period = 1 / freq
//        val result = (timeInSeconds % period) / period * 2 - 1
//        return result
    }

}