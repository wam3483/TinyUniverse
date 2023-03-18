package com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.ConstantStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform.SineWaveform

class PhaseModulationEffect(val inputStream : FloatInputStream, val frequency : Float, val phaseOffset: FloatInputStream, val signalRatio : Float = .5f) : FloatInputStream {
    private val mixSignal = SineWaveform(ConstantStream(frequency))
    override fun read(timeInSeconds: Float): Float {
        val periodInSecs = 1 / frequency
        val offsetInSecs = periodInSecs * phaseOffset.read(timeInSeconds)
        val inputValue = inputStream.read(timeInSeconds)
        val wetSignal = mixSignal.read(timeInSeconds+Math.sin(offsetInSecs.toDouble()).toFloat())
        val result = wetSignal*signalRatio + inputValue*(1-signalRatio)

        return Math.min(1f, Math.max(-1f,result))
    }
}