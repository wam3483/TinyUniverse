package com.pixelatedmind.game.tinyuniverse.generation.music.synth.patch

import com.badlogic.gdx.math.Interpolation
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.EnvelopeFactory
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.generation.music.Notes
import com.pixelatedmind.game.tinyuniverse.generation.math.InterpolatedPiecewiseFunction
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.EnvelopeImpl
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.Envelope
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.inputs.PitchEnvelope
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform.SineWaveform
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect.GainEffect

class BasketballBounce(val volume : Float) : EnvelopeFactory {
    val notes = Notes()
    override fun newEnvelope(frequency: Float): Envelope {
        val semiToneIndex = notes.getMidiTableIndexFor(frequency)
        val semiTonesToPitch = 7
        val endPitch = notes.midi[semiToneIndex+semiTonesToPitch]
        val pitchRange = frequency - endPitch

        val pitchAttackSecs = .1f
        val list = mutableListOf(
                Pair(pitchAttackSecs, InterpolatedPiecewiseFunction(pitchAttackSecs, 1f, Interpolation.linear))
        )
        val pitchNormEnv = EnvelopeImpl(list, Pair(1f, InterpolatedPiecewiseFunction(1f, 0f, Interpolation.linear)))
        val pitchEnvelope = PitchEnvelope(pitchNormEnv, endPitch, frequency)

        var stream : FloatInputStream = SineWaveform(pitchEnvelope)
        stream = GainEffect(stream, volume)
        val ampEnvelope =  EnvelopeImpl.buildEnvelope(stream, .01f, .2f, .8f, Interpolation.linear, Interpolation.pow2)
        ampEnvelope.addReleaseListener {
            pitchEnvelope.release()
        }
        return ampEnvelope
    }
}