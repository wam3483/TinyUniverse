package com.pixelatedmind.game.tinyuniverse.generation.music.synth.patch

import com.badlogic.gdx.math.Interpolation
import com.pixelatedmind.game.tinyuniverse.generation.math.InterpolatedPiecewiseFunction
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.EnvelopeFactory
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.generation.music.Notes
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect.UnisonEffect
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.inputs.DetunedNoteValue
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform.SineWaveform
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect.GainEffect
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.AmpEnvelopeStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.Envelope
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.EnvelopeImpl

class ElectricPiano: EnvelopeFactory {
    val notes = Notes()

    private fun buildAmpEnvelope() : Envelope {
        val attackDuration = .015f
        val attackAmplitude = 1f
        val attackInterpolation = Interpolation.linear

        val holdDuration = 0f

        val decayDuration = 2f
        val decayAmplitude = .6f
        val decayInterpolation = Interpolation.circleIn

        val sustainDuration = 1f
        val sustainAmplitude = 0f
        val sustainInterpolation = Interpolation.linear

        val releaseDuration = .1f

        val attackFunction =Pair(0f, InterpolatedPiecewiseFunction(attackDuration, attackAmplitude, attackInterpolation))
        val holdFunction   =Pair(attackDuration, InterpolatedPiecewiseFunction(holdDuration, attackAmplitude, attackInterpolation))
        val decayFunction  =Pair(attackDuration+holdDuration, InterpolatedPiecewiseFunction(decayDuration, decayAmplitude, decayInterpolation))
        val sustainFunction=Pair(attackDuration+holdDuration+decayDuration, InterpolatedPiecewiseFunction(sustainDuration, sustainAmplitude, sustainInterpolation))

        val releaseFunction = Pair(releaseDuration, InterpolatedPiecewiseFunction(releaseDuration,0f,Interpolation.linear))

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

    private fun baseStream(frequency : Float) : FloatInputStream {
        val frequencyValue = DetunedNoteValue(frequency, .2f, .25f, Interpolation.exp5, 0, false, notes)
        var stream : FloatInputStream = SineWaveform(frequencyValue)
        stream = GainEffect(stream, 0f)
        return stream
    }

    override fun newEnvelope(frequency: Float): Envelope {
        var stream : FloatInputStream = UnisonEffect(frequency, this::baseStream,5,.5f)
        stream = GainEffect(stream, -.2f)
        val ampEnvelope = buildAmpEnvelope()
        val result =  AmpEnvelopeStream(ampEnvelope, stream)
        return result
    }
}