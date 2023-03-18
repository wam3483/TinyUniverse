package com.pixelatedmind.game.tinyuniverse.generation.music.synth.patch

import com.badlogic.gdx.math.Interpolation
import com.pixelatedmind.game.tinyuniverse.generation.math.InterpolatedPiecewiseFunction
import com.pixelatedmind.game.tinyuniverse.generation.music.*
import com.pixelatedmind.game.tinyuniverse.generation.music.filter.*
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.inputs.PitchEnvelope
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform.SineWaveform
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect.GainEffect
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect.MultiplexGainEffect
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect.StartTimeEffect
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.AmpEnvelopeStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.Envelope
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.EnvelopeFactory
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.EnvelopeImpl

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

        val attackFunction =Pair(0f, InterpolatedPiecewiseFunction(attackDuration, attackAmplitude, attackInterpolation))
        val holdFunction   =Pair(attackDuration, InterpolatedPiecewiseFunction(holdDuration, attackAmplitude, attackInterpolation))
        val decayFunction  =Pair(attackDuration+holdDuration, InterpolatedPiecewiseFunction(decayDuration, decayAmplitude, decayInterpolation))
        val sustainFunction=Pair(attackDuration+holdDuration+decayDuration, InterpolatedPiecewiseFunction(sustainDuration, sustainAmplitude, sustainInterpolation))

        val releaseFunction = Pair(0f, InterpolatedPiecewiseFunction(.5f,0f,Interpolation.linear))

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
                Pair(0f, InterpolatedPiecewiseFunction(attackDuration,attackAmplitude,attackInterpolation)),
                Pair(attackDuration, InterpolatedPiecewiseFunction(decayDuration,decayAmplitude,decayInterpolation)),
                Pair(attackDuration+decayDuration, InterpolatedPiecewiseFunction(sustainDuration,sustainAmplitude,sustainInterpolation))
        )
        val releaseFunction = Pair(0f, InterpolatedPiecewiseFunction(1f, 0f, Interpolation.linear))
        val pitchNormEnvelope = EnvelopeImpl(asdFunctions, releaseFunction)
        val pitchEnvelope = PitchEnvelope(pitchNormEnvelope, endPitch,frequency  )
        return pitchEnvelope
    }

    private fun offset2Octaves(frequency :Float, semitoneDifference: Int) : Float{
        val offsetFrequency = notes.midi[notes.getMidiTableIndexFor(frequency) - semitoneDifference]
        return offsetFrequency
    }

    private fun getBaseStream(frequency : Float, streamFrequencyValue : FloatInputStream) : FloatInputStream {
        var stream : FloatInputStream = SineWaveform(streamFrequencyValue)

        val subbass = SubBassInputStream(1f, streamFrequencyValue)

        stream = MultiplexGainEffect(listOf(stream, subbass), listOf(.75f,.25f))

        stream = StartTimeEffect(GainEffect(stream, 0f))
        return stream
    }

    override fun newEnvelope(frequency: Float): Envelope {
        val envelopedPitch = buildPitchEnvelope(frequency)

        var stream = getBaseStream(frequency, envelopedPitch)
        stream = GainEffect(stream, volume)

        val complexAmpEnvelope = buildAmpEnvelope()
        val ampEnvelope =  AmpEnvelopeStream(complexAmpEnvelope, stream)

        ampEnvelope.addReleaseListener {
            envelopedPitch.release()
        }
        return ampEnvelope
    }
}