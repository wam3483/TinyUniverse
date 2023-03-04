package com.pixelatedmind.game.tinyuniverse.generation.music.patch

import com.badlogic.gdx.math.Interpolation
import com.pixelatedmind.game.tinyuniverse.generation.music.EnvelopeFactory
import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.generation.music.Notes
import com.pixelatedmind.game.tinyuniverse.generation.music.filter.AmpEnvelopeStream
import com.pixelatedmind.game.tinyuniverse.generation.music.filter.Envelope
import com.pixelatedmind.game.tinyuniverse.generation.music.filter.EnvelopeImpl
import com.pixelatedmind.game.tinyuniverse.generation.music.values.ConstantValue
import com.pixelatedmind.game.tinyuniverse.generation.music.values.InterpolatedNormalValue
import com.pixelatedmind.game.tinyuniverse.generation.music.values.NormalizedValueStream
import com.pixelatedmind.game.tinyuniverse.generation.music.waveform.SquareWaveform
import com.pixelatedmind.game.tinyuniverse.generation.music.waveform.VolumeModulationWaveformDecorator
import com.pixelatedmind.game.tinyuniverse.generation.music.waveform.WeightedInputStreamDecoratorImpl

class DramaticSquare(val volume : Float=1f) : EnvelopeFactory {
    val notes = Notes()
    override fun newEnvelope(frequency: Float): Envelope {
        val osc2Freq = notes.increaseFrequencyBySemitones(frequency, 12)

        val pulseWidth = InterpolatedNormalValue(.5f, .1f, .9f)
        val pulseWidth2 = InterpolatedNormalValue(.2f, .1f, .9f)

        val pitchValue = InterpolatedNormalValue(.01f, frequency, osc2Freq, Interpolation.linear, true)

        val osc1 = SquareWaveform(ConstantValue(frequency), pulseWidth=pulseWidth)
        val osc2 = SquareWaveform(ConstantValue(osc2Freq))//, pulseWidth=pulseWidth2)
        var stream : FloatInputStream = WeightedInputStreamDecoratorImpl(listOf(osc1, osc2), listOf(.8f,.2f))
        stream = VolumeModulationWaveformDecorator(stream, volume)
        return EnvelopeImpl.buildEnvelope(stream,.05f,.5f,1f, Interpolation.linear,Interpolation.exp5)
    }
}