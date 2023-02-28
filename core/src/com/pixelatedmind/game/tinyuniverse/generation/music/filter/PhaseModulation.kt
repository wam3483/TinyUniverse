package com.pixelatedmind.game.tinyuniverse.generation.music.filter

import com.badlogic.gdx.math.Interpolation
import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.generation.music.values.ConstantValue
import com.pixelatedmind.game.tinyuniverse.generation.music.values.NormalizedValue
import com.pixelatedmind.game.tinyuniverse.generation.music.waveform.SineWaveform

class PhaseModulation(val inputStream : FloatInputStream, val frequency : Float, val phaseOffset: NormalizedValue, val signalRatio : Float = .5f) : FloatInputStream {
    private val mixSignal = SineWaveform(ConstantValue(frequency),.5f)
    override fun read(timeInSeconds: Float): Float {
        val periodInSecs = 1 / frequency
        val offsetInSecs = periodInSecs * phaseOffset.getValue(timeInSeconds)
        val inputValue = inputStream.read(timeInSeconds)
        val wetSignal = mixSignal.read(timeInSeconds+Math.sin(offsetInSecs.toDouble()).toFloat())
                //inputStream.read(timeInSeconds+Math.sin(offsetInSecs.toDouble()).toFloat())
        val result = wetSignal*signalRatio + inputValue*(1-signalRatio)

        return Math.min(1f, Math.max(-1f,result))
    }
}