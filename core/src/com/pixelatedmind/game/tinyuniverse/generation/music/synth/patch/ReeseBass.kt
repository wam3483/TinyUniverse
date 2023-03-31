package com.pixelatedmind.game.tinyuniverse.generation.music.synth.patch

import com.badlogic.gdx.math.Interpolation
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.EnvelopeFactory
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.AmpEnvelopeStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.Envelope
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.EnvelopeImpl
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect.UnisonEffect
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.ConstantStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform.SawtoothWaveform

class ReeseBass : EnvelopeFactory {
    fun baseStream(frequency : Float) : FloatInputStream {
        val osc1 = SawtoothWaveform(ConstantStream(frequency),44100)
        return osc1
    }
    override fun newEnvelope(frequency: Float): Envelope {
        val unison = UnisonEffect(frequency, this::baseStream, 9, .1f)
        val envelope = EnvelopeImpl.buildEnvelope(unison,.01f,.01f,.8f, Interpolation.linear)
        return AmpEnvelopeStream(envelope, unison)
    }
}