package com.pixelatedmind.game.tinyuniverse.generation.music.patch

import com.badlogic.gdx.math.Interpolation
import com.pixelatedmind.game.tinyuniverse.generation.music.*
import com.pixelatedmind.game.tinyuniverse.generation.music.filter.*
import com.pixelatedmind.game.tinyuniverse.generation.music.values.ConstantValue
import com.pixelatedmind.game.tinyuniverse.generation.music.values.InterpolatedNormalValue
import com.pixelatedmind.game.tinyuniverse.generation.music.values.NormalizedValueStream
import com.pixelatedmind.game.tinyuniverse.generation.music.waveform.*
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
    //high low filter requires a specific freq for cutoff, but i want frequency determined by key presses.
    //so we define a normalized value here to scale requested frequencies by on keypress so cutoff is set correctly
    var highlowFilterCutoffFreq = 0f
    var highlowFilterResonance = 1f
    val highlowFilter : HighLowPassFilterInputStream

    init{
        notes = Notes()
        oscillator1 = Oscillator.Sine
        oscillator2 = Oscillator.Sine
        oscillatorMix = 0f
        highlowFilter = HighLowPassFilterInputStream(100f,1f,44100, PassType.Low, SilentInputStream())
    }

    private fun createOscillatorFor(oscillator: Oscillator, frequency : Float) : FloatInputStream{
        return when(oscillator){
            Oscillator.Sine -> SineWaveform(ConstantValue(frequency))
            Oscillator.Saw->SawtoothWaveform(ConstantValue(frequency))
            Oscillator.Pulse->SquareWaveform(ConstantValue(frequency),pulseWidth = NormalizedValueStream(SineWaveform(ConstantValue(.05f)),.05f, .95f))
            Oscillator.Triangle->TriangleWaveForm(ConstantValue(frequency))
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

        val attackFunction =Pair(0f, ASDRInterpolationFunction(attackDuration, attackAmplitude, attackInterpolation))
        val holdFunction   =Pair(attackDuration, ASDRInterpolationFunction(holdDuration, attackAmplitude, attackInterpolation))
        val decayFunction  =Pair(attackDuration+holdDuration, ASDRInterpolationFunction(decayDuration, decayAmplitude, decayInterpolation))
        val sustainFunction=Pair(attackDuration+holdDuration+decayDuration, ASDRInterpolationFunction(sustainDuration, sustainAmplitude, sustainInterpolation))

        val releaseFunction = Pair(0f, ASDRInterpolationFunction(.5f,0f, Interpolation.linear))

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

    private fun createPatch(freq:Float) : FloatInputStream{
        println("Created patch called.")
        val o1 = createOscillatorFor(oscillator1, freq)
        val index = notes.getMidiTableIndexFor(freq)
        var offsetIndex = index + oscillator2SemitoneOffset
        if(offsetIndex >= notes.midi.size){
            offsetIndex = index
        }
        val o2 = createOscillatorFor(oscillator2, notes.midi[offsetIndex])

        var stream : FloatInputStream = WeightedInputStreamDecoratorImpl(listOf(o1,o2), listOf(1-oscillatorMix, oscillatorMix))
        if(highlowFilterEnabled) {

            val cutoff = freq * highlowFilterCutoffFreq
            val maxBandwidth = 10f
            val bandwidth = maxBandwidth * highlowFilterResonance
            val centralFrequency = (cutoff-bandwidth/2)
            val resonance = centralFrequency / bandwidth
            println("resonance = "+cutoff+" / "+bandwidth+" = "+resonance)
            stream = BiQuadFilterInputStream(stream,44100f/2, cutoff, resonance)//highlowFilterResonance)
//            stream = VolumeModulationWaveformDecorator(stream,1f)
//            val cutoffMult = if(highlowFilterCutoffFreq >= 1) .99f else highlowFilterCutoffFreq
//            highlowFilter.setFrequency(freq / 2 * cutoffMult)
//            highlowFilter.stream = stream
//            highlowFilter.setResonance(resonance = this.highlowFilterResonance)
//            stream = VolumeModulationWaveformDecorator(highlowFilter, 2f)
        }
        val rawOscillatorMix = TimedFloatInputStreamDecorator(stream)
        stream = VolumeModulationWaveformDecorator(VolumeModulationWaveformDecorator(TimedFloatInputStreamDecorator(stream), -.5f), volumeOffsetInDecibels)
        if(subBass>0f)
        {
            val sineWave = VolumeModulationWaveformDecorator(
                    VolumeModulationWaveformDecorator(TriangleWaveForm(ConstantValue(freq)), -.5f), subBass)
            val additiveStream = WeightedInputStreamDecoratorImpl(listOf(sineWave, stream), listOf(.5f, .5f))
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