package com.pixelatedmind.game.tinyuniverse.generation.music.synth.patch

import com.badlogic.gdx.math.Interpolation
import com.pixelatedmind.game.tinyuniverse.generation.math.InterpolatedPiecewiseFunction
import com.pixelatedmind.game.tinyuniverse.generation.math.PiecewiseTimeFunction
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect.GainEffect
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.EnvelopeFactory
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.AmpEnvelopeStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.Envelope
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.EnvelopeImpl
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.filter.EqualizerFilter
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.filter.EqualizerStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.ConstantStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.WhiteNoiseStream
import java.util.*

class ClapPatch(private val sampleRate : Float) : EnvelopeFactory {

    private fun buildAmpEnvelope() : Envelope {
        val volumeModSecs = .02f
        var time = 0f
        val envFuncs = mutableListOf<Pair<Float, PiecewiseTimeFunction>>()
        repeat(3){
            val rampUp =Pair(time, InterpolatedPiecewiseFunction(0f, 1f, Interpolation.linear))
            time += volumeModSecs
            val rampDown =Pair(time, InterpolatedPiecewiseFunction(volumeModSecs, 0f, Interpolation.linear))
            envFuncs.add(rampUp)
            envFuncs.add(rampDown)
            time += volumeModSecs
        }
        val releaseFunction = Pair(time, InterpolatedPiecewiseFunction(.0001f,0f,Interpolation.linear))
        return EnvelopeImpl(envFuncs,releaseFunction)
    }

    private fun buildEqualizerFilter(stream : FloatInputStream) : FloatInputStream{
        val frequencies = floatArrayOf(32f, 64f, 125f, 250f, 500f, 1000f, 2000f, 4000f, 8000f, 16000f)
        val filter = EqualizerFilter.emphasizeMidRange()
        val equalizerStream = EqualizerStream(stream, filter)
        return equalizerStream
    }

    override fun newEnvelope(frequency: Float): Envelope {
        var stream : FloatInputStream = WhiteNoiseStream(Random())
        stream = buildEqualizerFilter(stream)
        stream = GainEffect(stream, ConstantStream(.25f))
        val ampEnvelope = buildAmpEnvelope()
        return AmpEnvelopeStream(ampEnvelope, stream)
    }
}