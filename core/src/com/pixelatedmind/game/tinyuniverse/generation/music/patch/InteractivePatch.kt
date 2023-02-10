package com.pixelatedmind.game.tinyuniverse.generation.music.patch

import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStreamFactory
import com.pixelatedmind.game.tinyuniverse.generation.music.waveform.*
import com.pixelatedmind.game.tinyuniverse.screen.synthui.Oscillator

class InteractivePatch : FloatInputStreamFactory {
    var oscillator1 : Oscillator
    var oscillator2 : Oscillator
    var oscillatorMix : Float

    init{
        oscillator1 = Oscillator.Sine
        oscillator2 = Oscillator.Sine
        oscillatorMix = 0f
    }

    private fun createOscillatorFor(oscillator: Oscillator, frequency : Float) : FloatInputStream{
        return when(oscillator){
            Oscillator.Sine -> SineWaveform(frequency)
            Oscillator.Saw->SawtoothWaveform(frequency)
            Oscillator.Pulse->SquareWaveform(frequency)
            Oscillator.Triangle->TriangleWaveForm(frequency)
        }
    }

    private fun createPatch(freq:Float) : FloatInputStream{
        val o1 = createOscillatorFor(oscillator1, freq)
        val o2 = createOscillatorFor(oscillator2, freq)
        return AdditiveWaveform(listOf(o1,o2), listOf(1-oscillatorMix, oscillatorMix))
    }

    override fun newInputStream(frequency: Float): FloatInputStream {
        return createPatch(frequency)
    }
}