package com.pixelatedmind.game.tinyuniverse.generation.music.patch

import com.badlogic.gdx.math.Interpolation
import com.pixelatedmind.game.tinyuniverse.generation.music.EnvelopeFactory
import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.generation.music.Notes
import com.pixelatedmind.game.tinyuniverse.generation.music.filter.*
import com.pixelatedmind.game.tinyuniverse.generation.music.values.AnimatedValueImpl
import com.pixelatedmind.game.tinyuniverse.generation.music.values.ConstantValue
import com.pixelatedmind.game.tinyuniverse.generation.music.values.DetunedNoteValue
import com.pixelatedmind.game.tinyuniverse.generation.music.waveform.SineWaveform
import com.pixelatedmind.game.tinyuniverse.generation.music.waveform.VolumeModulationWaveformDecorator

class LofiElectricPiano: EnvelopeFactory {
    val notes = Notes()

    private fun buildAmpEnvelope() : Envelope {
        val attackDuration = .015f
        val attackAmplitude = 1f
        val attackInterpolation = Interpolation.linear

        val holdDuration = 0f

        val decayDuration = 2f
        val decayAmplitude = .6f
        val decayInterpolation = Interpolation.linear

        val sustainDuration = 1f
        val sustainAmplitude = 0f
        val sustainInterpolation = Interpolation.linear

        val releaseDuration = .1f

        val attackFunction =Pair(0f, ASDRInterpolationFunction(attackDuration, attackAmplitude, attackInterpolation))
        val holdFunction   =Pair(attackDuration, ASDRInterpolationFunction(holdDuration, attackAmplitude, attackInterpolation))
        val decayFunction  =Pair(attackDuration+holdDuration, ASDRInterpolationFunction(decayDuration, decayAmplitude, decayInterpolation))
        val sustainFunction=Pair(attackDuration+holdDuration+decayDuration, ASDRInterpolationFunction(sustainDuration, sustainAmplitude, sustainInterpolation))

        val releaseFunction = Pair(releaseDuration, ASDRInterpolationFunction(releaseDuration,0f,Interpolation.linear))

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

    override fun newEnvelope(frequency: Float): Envelope {
        val frequencyValue = DetunedNoteValue(frequency, .2f,1f, Interpolation.linear, -1, notes)
        var stream : FloatInputStream = SineWaveform(frequencyValue)
        stream = VolumeModulationWaveformDecorator(stream, -.5f)

        val ampEnvelope = buildAmpEnvelope()
        val result =  AmpEnvelopeStream(ampEnvelope, stream)
        return result
    }
}