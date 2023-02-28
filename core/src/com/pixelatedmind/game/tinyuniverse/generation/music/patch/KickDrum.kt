package com.pixelatedmind.game.tinyuniverse.generation.music.patch

import com.badlogic.gdx.math.Interpolation
import com.pixelatedmind.game.tinyuniverse.generation.music.*
import com.pixelatedmind.game.tinyuniverse.generation.music.filter.*
import com.pixelatedmind.game.tinyuniverse.generation.music.values.ConstantValue
import com.pixelatedmind.game.tinyuniverse.generation.music.values.NormalizedValue
import com.pixelatedmind.game.tinyuniverse.generation.music.values.PitchEnvelope
import com.pixelatedmind.game.tinyuniverse.generation.music.waveform.SineWaveform
import com.pixelatedmind.game.tinyuniverse.generation.music.waveform.VolumeModulationWaveformDecorator
import com.pixelatedmind.game.tinyuniverse.generation.music.waveform.WeightedInputStreamDecoratorImpl

class KickDrum(val volume : Float) : EnvelopeFactory {
    val notes = Notes()

    private fun buildAmpEnvelope() : Envelope {
        val attackDuration = .01f
        val attackAmplitude = 1f
        val attackInterpolation = Interpolation.linear

        val holdDuration = .170f

        val decayDuration = .150f
        val decayAmplitude = 0f
        val decayInterpolation = Interpolation.linear

        val sustainDuration = 1f
        val sustainAmplitude = 0f
        val sustainInterpolation = Interpolation.linear

        val attackFunction =Pair(0f, ASDRInterpolationFunction(attackDuration, attackAmplitude, attackInterpolation))
        val holdFunction   =Pair(attackDuration, ASDRInterpolationFunction(holdDuration, attackAmplitude, attackInterpolation))
        val decayFunction  =Pair(attackDuration+holdDuration, ASDRInterpolationFunction(decayDuration, decayAmplitude, decayInterpolation))
        val sustainFunction=Pair(attackDuration+holdDuration+decayDuration, ASDRInterpolationFunction(sustainDuration, sustainAmplitude, sustainInterpolation))

        val releaseFunction = Pair(0f, ASDRInterpolationFunction(.5f,0f,Interpolation.linear))

        val asdFunctions = listOf(
                attackFunction,
                holdFunction,
                decayFunction,
                sustainFunction
        )
        val envelope = EnvelopeImpl(
                asdFunctions,
            releaseFunction)
        return envelope
    }

    private fun buildPitchEnvelope(frequency: Float) : PitchEnvelope {
        val endPitch = 10f//frequency-frequency

        val attackDuration = .01f
        val attackAmplitude = 1f
        val attackInterpolation = Interpolation.linear

        val decayDuration = .150f/2
        val decayAmplitude = 1f
        val decayInterpolation = Interpolation.exp10In

        val sustainDuration = .5f
        val sustainAmplitude = 0f
        val sustainInterpolation = Interpolation.linear

        val asdFunctions = listOf(
                Pair(0f, ASDRInterpolationFunction(attackDuration,attackAmplitude,attackInterpolation)),
                Pair(attackDuration, ASDRInterpolationFunction(decayDuration,decayAmplitude,decayInterpolation)),
                Pair(attackDuration+decayDuration, ASDRInterpolationFunction(sustainDuration,sustainAmplitude,sustainInterpolation))
        )
        val releaseFunction = Pair(0f, ASDRInterpolationFunction(1f, 0f, Interpolation.linear))
        val pitchNormEnvelope = EnvelopeImpl(asdFunctions, releaseFunction)
        val pitchEnvelope = PitchEnvelope(pitchNormEnvelope, endPitch,frequency  )
        return pitchEnvelope
    }

    private fun offset2Octaves(frequency :Float, semitoneDifference: Int) : Float{
        val offsetFrequency = notes.midi[notes.getMidiTableIndexFor(frequency) - semitoneDifference]
        return offsetFrequency
    }

    private fun getBaseStream(frequency : Float, streamFrequencyValue : NormalizedValue) : FloatInputStream{
        var stream : FloatInputStream = SineWaveform(streamFrequencyValue)

        val subbass = SubBassInputStream(1f, streamFrequencyValue)

        stream = WeightedInputStreamDecoratorImpl(listOf(stream, subbass), listOf(.75f,.25f))

        stream = TimedFloatInputStreamDecorator(VolumeModulationWaveformDecorator(stream, 0f))
        return stream
    }

    override fun newEnvelope(frequency: Float): Envelope {
        val envelopedPitch = buildPitchEnvelope(frequency)

        var stream = getBaseStream(frequency, envelopedPitch)
        stream = VolumeModulationWaveformDecorator(stream, 0f)

        val complexAmpEnvelope = buildAmpEnvelope()
        val ampEnvelope =  AmpEnvelopeStream(complexAmpEnvelope, stream)

        ampEnvelope.addReleaseListener {
            envelopedPitch.release()
        }
        return ampEnvelope
    }
}