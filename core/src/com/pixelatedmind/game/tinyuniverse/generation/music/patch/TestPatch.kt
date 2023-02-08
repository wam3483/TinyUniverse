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

        return HighLowPassFilterInputStream(HighLowPassFilter(.96f*frequency,44100, PassType.High),sineWave)
//        return niceSoundingInterpolation
    }
}