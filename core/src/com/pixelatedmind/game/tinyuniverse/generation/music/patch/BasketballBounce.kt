package com.pixelatedmind.game.tinyuniverse.generation.music.patch

import com.badlogic.gdx.math.Interpolation
import com.pixelatedmind.game.tinyuniverse.generation.music.EnvelopeFactory
import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.generation.music.Notes
import com.pixelatedmind.game.tinyuniverse.generation.music.filter.ASDRInterpolationFunction
import com.pixelatedmind.game.tinyuniverse.generation.music.filter.EnvelopeImpl
import com.pixelatedmind.game.tinyuniverse.generation.music.filter.Envelope
import com.pixelatedmind.game.tinyuniverse.generation.music.values.PitchEnvelope
import com.pixelatedmind.game.tinyuniverse.generation.music.waveform.SineWaveform
import com.pixelatedmind.game.tinyuniverse.generation.music.waveform.VolumeModulationWaveformDecorator

class BasketballBounce(val volume : Float) : EnvelopeFactory {
    val notes = Notes()
    override fun newEnvelope(frequency: Float): Envelope {
        val semiToneIndex = notes.getMidiTableIndexFor(frequency)
        val semiTonesToPitch = 7
        val endPitch = notes.midi[semiToneIndex+semiTonesToPitch]
        val pitchRange = frequency - endPitch

        val pitchAttackSecs = .1f
        val list = mutableListOf(
                Pair(pitchAttackSecs, ASDRInterpolationFunction(pitchAttackSecs, 1f, Interpolation.linear))
        )
        val pitchNormEnv = EnvelopeImpl(list, Pair(1f, ASDRInterpolationFunction(1f, 0f, Interpolation.linear)))
        val pitchEnvelope = PitchEnvelope(pitchNormEnv, endPitch, frequency)

        var stream :FloatInputStream = SineWaveform(pitchEnvelope)
        stream = VolumeModulationWaveformDecorator(stream, volume)
        val ampEnvelope =  EnvelopeImpl.buildEnvelope(stream, .01f, .2f, .8f, Interpolation.linear, Interpolation.pow2)
        ampEnvelope.addReleaseListener {
            pitchEnvelope.release()
        }
        return ampEnvelope
    }
}