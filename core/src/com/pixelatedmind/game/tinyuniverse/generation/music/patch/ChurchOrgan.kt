package com.pixelatedmind.game.tinyuniverse.generation.music.patch

import com.pixelatedmind.game.tinyuniverse.generation.music.*
import com.pixelatedmind.game.tinyuniverse.generation.music.filter.*
import com.pixelatedmind.game.tinyuniverse.generation.music.values.ConstantValue
import com.pixelatedmind.game.tinyuniverse.generation.music.values.AnimatedValueImpl
import com.pixelatedmind.game.tinyuniverse.generation.music.waveform.*

class ChurchOrgan(val volume : Float) : EnvelopeFactory {
    val notes : Notes = Notes()

    override fun newEnvelope(frequency: Float): Envelope {
        val osc2Freq = notes.increaseFrequencyBySemitones(frequency, 12)

        val saw1 = SawtoothWaveform(frequency = ConstantValue(frequency))
        val saw2 = SawtoothWaveform(frequency = ConstantValue(osc2Freq))

        val cutoff = .8f
        val bandwidthMultiplierNorm = .01f
        val oscMixStream = WeightedInputStreamDecoratorImpl(listOf(saw1, saw2), listOf(.5f,.5f))
        val maxBandwidth = 10f
        val bandwidth = maxBandwidth * bandwidthMultiplierNorm
        val centralFrequency = (cutoff-bandwidth/2)
        val resonance = centralFrequency / bandwidth

        val lowPassStream = VolumeModulationWaveformDecorator(
                HighLowPassFilterInputStream(frequency*cutoff,resonance, 44100/2, PassType.Low, oscMixStream),
                1f)
        val volumeAdjustedStream = VolumeModulationWaveformDecorator(
                VolumeModulationWaveformDecorator(TimedFloatInputStreamDecorator(lowPassStream), -.5f), volume)
        val phaseModulation = PhaseModulation(volumeAdjustedStream, frequency, AnimatedValueImpl(1f))
        return EnvelopeImpl.buildEnvelope(phaseModulation, .05f, .2f, 1f)
    }
}