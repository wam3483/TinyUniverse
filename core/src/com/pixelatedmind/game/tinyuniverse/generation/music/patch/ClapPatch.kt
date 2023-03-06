package com.pixelatedmind.game.tinyuniverse.generation.music.patch

import com.badlogic.gdx.math.Interpolation
import com.pixelatedmind.game.tinyuniverse.generation.music.EnvelopeFactory
import com.pixelatedmind.game.tinyuniverse.generation.music.filter.*
import com.pixelatedmind.game.tinyuniverse.generation.music.noise.WhiteNoise
import java.util.*

class ClapPatch : EnvelopeFactory {

    private fun buildAmpEnvelope() : Envelope{
        val volumeModSecs = .02f
        var time = 0f
        val envFuncs = mutableListOf<Pair<Float, PiecewiseTimeFunction>>()
        repeat(3){
            val rampUp =Pair(time, ASDRInterpolationFunction(0f, 1f, Interpolation.linear))
            time += volumeModSecs
            val rampDown =Pair(time, ASDRInterpolationFunction(volumeModSecs, 0f, Interpolation.linear))
            envFuncs.add(rampUp)
            envFuncs.add(rampDown)
            time += volumeModSecs
        }
        val releaseFunction = Pair(0f, ASDRInterpolationFunction(.0001f,0f,Interpolation.linear))
        return EnvelopeImpl(envFuncs,releaseFunction)
    }

    override fun newEnvelope(frequency: Float): Envelope {
        val stream = WhiteNoise(Random())
        val ampEnvelope = buildAmpEnvelope()
        return AmpEnvelopeStream(ampEnvelope, stream)
    }
}