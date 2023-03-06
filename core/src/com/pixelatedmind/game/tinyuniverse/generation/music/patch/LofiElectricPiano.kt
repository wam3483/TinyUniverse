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
import com.pixelatedmind.game.tinyuniverse.generation.music.waveform.WeightedInputStreamDecoratorImpl
import java.lang.Math.sqrt

class LofiElectricPiano: EnvelopeFactory {
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

    private fun generateNormalizedIrrationals(count : Int) : List<Float> {
        var rootArg = 2.0
        var i = 0
        val result = mutableListOf<Float>()
        val perfectSquares = listOf(4.0, 9.0, 16.0, 25.0,36.0)
        while(i<count){
            if(perfectSquares.contains(rootArg)){
                rootArg++
            }
            val value = sqrt(rootArg)
            result.add(value.toFloat())
            rootArg++
            i++
        }
        val normalMult = 1f / result.size
        return result.map{it * normalMult}
    }
    private fun unisonEffect(frequency : Float, numStreams : Int, detunePercent : Float): FloatInputStream{
        val perVoiceFrequencyMult = generateNormalizedIrrationals(numStreams+1)
        val detune = mutableListOf<Float>()
        var i =0
        var sum = 0f
        while(i<numStreams){
            val delta = (perVoiceFrequencyMult[i+1] - perVoiceFrequencyMult[i]) * detunePercent
            val detuneValue = Math.pow(2.0, delta.toDouble()).toFloat()
            detune.add(detuneValue)
            sum += detuneValue
            i++
        }
        sum /= numStreams
        sum -= 1
        detune.forEachIndexed{ index, _ ->
            detune[index] -= sum
        }

        val streams = detune.map{
            baseStream(frequency * it)
        }
        val weights = mutableListOf<Float>()
        repeat(numStreams){
            weights.add(1f/numStreams)
        }
        return WeightedInputStreamDecoratorImpl(streams, weights)
    }
    private fun baseStream(frequency : Float) : FloatInputStream{
        val frequencyValue = DetunedNoteValue(frequency, .2f, .25f, Interpolation.exp5, 0, false, notes)
        var stream : FloatInputStream = SineWaveform(frequencyValue)
        stream = VolumeModulationWaveformDecorator(stream, -.5f)
        return stream
    }
    override fun newEnvelope(frequency: Float): Envelope {
        val stream = unisonEffect(frequency, 5,.5f)
        val ampEnvelope = buildAmpEnvelope()
        val result =  AmpEnvelopeStream(ampEnvelope, stream)
        return result
    }
}