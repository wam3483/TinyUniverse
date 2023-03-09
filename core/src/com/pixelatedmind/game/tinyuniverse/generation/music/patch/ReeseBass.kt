package com.pixelatedmind.game.tinyuniverse.generation.music.patch

import com.badlogic.gdx.math.Interpolation
import com.pixelatedmind.game.tinyuniverse.generation.music.EnvelopeFactory
import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.generation.music.filter.AmpEnvelopeStream
import com.pixelatedmind.game.tinyuniverse.generation.music.filter.Envelope
import com.pixelatedmind.game.tinyuniverse.generation.music.filter.EnvelopeImpl
import com.pixelatedmind.game.tinyuniverse.generation.music.filter.UnisonEffect
import com.pixelatedmind.game.tinyuniverse.generation.music.values.ConstantValue
import com.pixelatedmind.game.tinyuniverse.generation.music.waveform.SawtoothWaveform

class ReeseBass : EnvelopeFactory {
    fun baseStream(frequency : Float) : FloatInputStream{
        val osc1 = SawtoothWaveform(ConstantValue(frequency))
        return osc1
    }
    override fun newEnvelope(frequency: Float): Envelope {
        val unison = UnisonEffect(frequency, this::baseStream, 9, .1f)
        val envelope = EnvelopeImpl.Companion.buildEnvelope(unison,.01f,.01f,.8f, Interpolation.linear)
        return AmpEnvelopeStream(envelope, unison)
    }
}