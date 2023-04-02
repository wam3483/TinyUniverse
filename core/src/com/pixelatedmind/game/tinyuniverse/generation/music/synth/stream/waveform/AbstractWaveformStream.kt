package com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream

abstract class AbstractWaveformStream(val frequency : FloatInputStream, var sampleRate : Int) : FloatInputStream {
    protected val onePeriod = (Math.PI*2).toFloat()
    private var phase = 0f

    protected abstract fun mapPhaseToAmplitude(elapsedTime : Float, phase : Float) : Float

    override fun read(timeInSeconds: Float): Float {
        val frequency = frequency.read(timeInSeconds)
        val currentPhase = onePeriod * frequency / sampleRate
        phase += currentPhase
        return mapPhaseToAmplitude(timeInSeconds, phase)
    }
}