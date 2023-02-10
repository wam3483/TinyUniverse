package com.pixelatedmind.game.tinyuniverse.generation.music.patch

import com.badlogic.gdx.math.Interpolation
import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStreamFactory
import com.pixelatedmind.game.tinyuniverse.generation.music.HighLowPassFilterInputStream
import com.pixelatedmind.game.tinyuniverse.generation.music.WaveformInterpolator
import com.pixelatedmind.game.tinyuniverse.generation.music.filter.HighLowPassFilter
import com.pixelatedmind.game.tinyuniverse.generation.music.filter.PassType
import com.pixelatedmind.game.tinyuniverse.generation.music.waveform.*

class TestPatch : FloatInputStreamFactory {
    override fun newInputStream(frequency: Float): FloatInputStream {
        val sineWave = SineWaveform(frequency*1.5f)
        val triangleWave = TriangleWaveForm( frequency)
        val niceSoundingInterpolation = WaveformInterpolator(sineWave, triangleWave, .9f, Interpolation.linear)

        val pulse = SquareWaveform(frequency, pulseWidth = .5f)
        val lowPass = HighLowPassFilterInputStream(HighLowPassFilter(.2f*frequency,44100, PassType.Low),pulse)
//        return lowPass
        val volumeWave = VolumeModulationWaveformDecorator(lowPass, 2f)
        return volumeWave
//        return niceSoundingInterpolation
    }
}