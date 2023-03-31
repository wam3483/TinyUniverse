package com.pixelatedmind.game.tinyuniverse.generation.music.synth.patch

import com.badlogic.gdx.math.Interpolation
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.EnvelopeFactory
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.generation.music.Notes
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.Envelope
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.EnvelopeImpl
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.inputs.AnimatedValueImpl
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform.SquareWaveform
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect.GainEffect
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect.MultiplexGainEffect
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.ConstantStream

class DramaticSquare(val volume : Float=1f) : EnvelopeFactory {
    val notes = Notes()
    override fun newEnvelope(frequency: Float): Envelope {
        val osc2Freq = notes.increaseFrequencyBySemitones(frequency, 12)

        val pulseWidth = AnimatedValueImpl(.5f, .1f, .9f)
        val pulseWidth2 = AnimatedValueImpl(.2f, .1f, .9f)

        val pitchValue = AnimatedValueImpl(.01f, frequency, osc2Freq, Interpolation.linear,-1, true)

        val osc1 = SquareWaveform(ConstantStream(frequency), 44100, pulseWidth)
        val osc2 = SquareWaveform(ConstantStream(osc2Freq), 44100)//, pulseWidth=pulseWidth2)
        var stream : FloatInputStream = MultiplexGainEffect(listOf(osc1, osc2), listOf(.8f,.2f))
        stream = GainEffect(stream, ConstantStream(volume))
        return EnvelopeImpl.buildEnvelope(stream,.05f,.5f,1f, Interpolation.linear,Interpolation.exp5)
    }
}