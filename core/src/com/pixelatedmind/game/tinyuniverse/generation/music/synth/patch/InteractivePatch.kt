package com.pixelatedmind.game.tinyuniverse.generation.music.synth.patch

import com.badlogic.gdx.math.Interpolation
import com.pixelatedmind.game.tinyuniverse.generation.math.InterpolatedPiecewiseFunction
import com.pixelatedmind.game.tinyuniverse.generation.music.*
import com.pixelatedmind.game.tinyuniverse.generation.music.filter.*
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect.GainEffect
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect.MultiplexGainEffect
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect.StartTimeEffect
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.Envelope
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.EnvelopeFactory
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.EnvelopeImpl
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.filter.HighLowPassFilterInputStream2
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.ConstantStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.SilentInputStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform.SawtoothWaveform
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform.SineWaveform
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform.SquareWaveform
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform.TriangleWaveForm
import com.pixelatedmind.game.tinyuniverse.screen.synthui.Oscillator

class InteractivePatch : EnvelopeFactory {
    var oscillator1 : Oscillator
    var oscillator2 : Oscillator
    var oscillatorMix : Float
    var oscillator2SemitoneOffset : Int = 0
    var volumeOffsetInDecibels = 0f
    var subBass = 0f
    var attack = 0f
    var hold = 0f
    var decay = 0f
    var sustain = 0f
    var release = 0f


    val notes : Notes
    var highlowFilterEnabled = false
    var highlowFilterPassType = PassType.Low

    //high low filter requires a specific freq for cutoff, but i want frequency determined by key presses.
    //so we define a normalized value here to scale requested frequencies by on keypress so cutoff is set correctly
    var highlowFilterCutoffFreq = 0f
    var highlowFilterResonance = 1f

    init{
        notes = Notes()
        oscillator1 = Oscillator.Sine
        oscillator2 = Oscillator.Sine
        oscillatorMix = 0f
    }

    private fun createOscillatorFor(oscillator: Oscillator, frequency : Float) : FloatInputStream {
        return when(oscillator){
            Oscillator.Sine -> SineWaveform(ConstantStream(frequency),44100)
            Oscillator.Saw-> SawtoothWaveform(ConstantStream(frequency), 44100)
            Oscillator.Pulse-> SquareWaveform(ConstantStream(frequency), 44100)
            Oscillator.Triangle-> TriangleWaveForm(ConstantStream(frequency), 44100)
        }
    }

    private fun buildAmpEnvelope() : Envelope {
        val attackDuration = this.attack
        val attackAmplitude = 1f
        val attackInterpolation = Interpolation.linear

        val holdDuration = this.hold

        val decayDuration = this.decay
        val decayAmplitude = 0f
        val decayInterpolation = Interpolation.linear

        val sustainDuration = 1f
        val sustainAmplitude = 0f
        val sustainInterpolation = Interpolation.linear

        val attackFunction =Pair(0f, InterpolatedPiecewiseFunction(attackDuration, attackAmplitude, attackInterpolation))
        val holdFunction   =Pair(attackDuration, InterpolatedPiecewiseFunction(holdDuration, attackAmplitude, attackInterpolation))
        val decayFunction  =Pair(attackDuration+holdDuration, InterpolatedPiecewiseFunction(decayDuration, decayAmplitude, decayInterpolation))
        val sustainFunction=Pair(attackDuration+holdDuration+decayDuration, InterpolatedPiecewiseFunction(sustainDuration, sustainAmplitude, sustainInterpolation))

        val releaseFunction = Pair(0f, InterpolatedPiecewiseFunction(.5f,0f, Interpolation.linear))

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

    private fun createPatch(freq:Float) : FloatInputStream {
        println("Created patch called.")
        val o1 = createOscillatorFor(oscillator1, freq)
        val index = notes.getMidiTableIndexFor(freq)
        var offsetIndex = index + oscillator2SemitoneOffset
        if(offsetIndex >= notes.midi.size){
            offsetIndex = index
        }
        val o2 = createOscillatorFor(oscillator2, notes.midi[offsetIndex])

        var stream : FloatInputStream = MultiplexGainEffect(listOf(o1,o2), listOf(1-oscillatorMix, oscillatorMix))
        if(highlowFilterEnabled) {

            val cutoff = freq * highlowFilterCutoffFreq
            val maxBandwidth = 10f
            val bandwidth = maxBandwidth * highlowFilterResonance
            val centralFrequency = (cutoff-bandwidth/2)
            val resonance = .5f + highlowFilterResonance * 100f//centralFrequency / bandwidth
            println("resonance = "+cutoff+" / "+bandwidth+" = "+resonance)
            stream = HighLowPassFilterInputStream2(ConstantStream(cutoff),ConstantStream(resonance),44100,PassType.Low, stream)//BiQuadFilterInputStream(stream,44100f/2, cutoff, resonance)//highlowFilterResonance)
        }
        val rawOscillatorMix = StartTimeEffect(stream)
        stream = GainEffect(GainEffect(StartTimeEffect(stream), ConstantStream(.25f)), ConstantStream(volumeOffsetInDecibels))
        if(subBass>0f)
        {
            val sineWave = GainEffect(
                    GainEffect(TriangleWaveForm(ConstantStream(freq), 44100), ConstantStream(.25f)), ConstantStream(subBass))
            val additiveStream = MultiplexGainEffect(listOf(sineWave, stream), listOf(.5f, .5f))
            stream = additiveStream
        }
//        val phaseModulation = PhaseModulation(stream, freq, InterpolatedNormalValue(1f))
//        stream = phaseModulation
        return stream
//        return stream
    }

    override fun newEnvelope(frequency: Float): Envelope {
        val stream =  createPatch(frequency)
        return EnvelopeImpl.buildEnvelope(stream, .05f, .2f, 1f)
    }
}