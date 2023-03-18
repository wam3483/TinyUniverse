package com.pixelatedmind.game.tinyuniverse.generation.music.synth.patch

import com.pixelatedmind.game.tinyuniverse.generation.music.*
import com.pixelatedmind.game.tinyuniverse.generation.music.filter.*
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect.GainEffect
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect.MultiplexGainEffect
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect.PhaseModulationEffect
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect.StartTimeEffect
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.Envelope
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.EnvelopeFactory
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.EnvelopeImpl
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.filter.HighLowPassFilterInputStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.ConstantStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform.SawtoothWaveform
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.inputs.AnimatedValueImpl

class ChurchOrgan(val volume : Float) : EnvelopeFactory {
    val notes : Notes = Notes()

    override fun newEnvelope(frequency: Float): Envelope {
        val osc2Freq = notes.increaseFrequencyBySemitones(frequency, 12)

        val saw1 = SawtoothWaveform(frequency = ConstantStream(frequency))
        val saw2 = SawtoothWaveform(frequency = ConstantStream(osc2Freq))

        val cutoff = .8f
        val bandwidthMultiplierNorm = .01f
        val oscMixStream = MultiplexGainEffect(listOf(saw1, saw2), listOf(.5f,.5f))
        val maxBandwidth = 10f
        val bandwidth = maxBandwidth * bandwidthMultiplierNorm
        val centralFrequency = (cutoff-bandwidth/2)
        val resonance = centralFrequency / bandwidth

        val lowPassStream = GainEffect(
                HighLowPassFilterInputStream(frequency*cutoff,resonance, 44100/2, PassType.Low, oscMixStream),
                1f)
        val volumeAdjustedStream = GainEffect(
                GainEffect(StartTimeEffect(lowPassStream), -.5f), volume)
        val phaseModulation = PhaseModulationEffect(volumeAdjustedStream, frequency, AnimatedValueImpl(1f))
        return EnvelopeImpl.buildEnvelope(phaseModulation, .05f, .2f, 1f)
    }
}