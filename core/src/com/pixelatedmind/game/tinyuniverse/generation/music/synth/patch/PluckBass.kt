package com.pixelatedmind.game.tinyuniverse.generation.music.synth.patch

import com.badlogic.gdx.math.Interpolation
import com.pixelatedmind.game.tinyuniverse.generation.math.InterpolatedPiecewiseFunction
import com.pixelatedmind.game.tinyuniverse.generation.music.filter.PassType
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.Envelope
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.EnvelopeFactory
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.EnvelopeImpl
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.filter.HighLowPassFilterInputStream2
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.ConstantStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform.SawtoothWaveform

class PluckBass : EnvelopeFactory {

    fun baseStream(frequency : Float) : FloatInputStream {
        val osc1 = SawtoothWaveform(ConstantStream(frequency))
        return osc1
    }

    fun buildLowPassFrequencyCutoffEnvelope() : Envelope{
        val cutoffStart = 6000f
        val cutoffEnd = 20f
        val cutoffRange = cutoffStart - cutoffEnd

        val attackDuration = .0001f
        val attackAmplitude = cutoffStart
        val attackInterpolation = Interpolation.linear

//        val holdDuration = .170f

        val decayDuration = 2f
        val decayAmplitude = cutoffRange * .25f
        val decayInterpolation = Interpolation.linear

        val sustainDuration = .05f
        val sustainAmplitude = cutoffEnd
        val sustainInterpolation = Interpolation.linear

        val attackFunction =Pair(0f, InterpolatedPiecewiseFunction(attackDuration, attackAmplitude, attackInterpolation))
//        val holdFunction   =Pair(attackDuration, InterpolatedPiecewiseFunction(holdDuration, attackAmplitude, attackInterpolation))
        val decayFunction  =Pair(attackDuration, InterpolatedPiecewiseFunction(decayDuration, decayAmplitude, decayInterpolation))
        val sustainFunction=Pair(attackDuration+decayDuration, InterpolatedPiecewiseFunction(sustainDuration, sustainAmplitude, sustainInterpolation))

        val releaseFunction = Pair(0f, InterpolatedPiecewiseFunction(.5f,0f, Interpolation.linear))

        val asdFunctions = listOf(
                attackFunction,
                decayFunction,
                sustainFunction
        )
        val envelope = EnvelopeImpl(
                asdFunctions,
                releaseFunction)
        return envelope
    }

    fun buildAmplEnvelope(){

    }

    override fun newEnvelope(frequency: Float): Envelope {
        var stream : FloatInputStream = baseStream(frequency)
        val frequencyCutoff = buildLowPassFrequencyCutoffEnvelope()
        stream = HighLowPassFilterInputStream2(frequencyCutoff,ConstantStream(1f),44100, PassType.Low,stream)
        return EnvelopeImpl.buildEnvelope(stream)
    }
}