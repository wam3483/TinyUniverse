package com.pixelatedmind.game.tinyuniverse.services

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.EnvelopeFactory
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.EnvelopeFactoryImpl
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.EnvelopeImpl
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.ConstantStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform.SineWaveform

class WaveformRepository {
    private val waveformNames = listOf(
            "sine",
            "pulse",
            "saw",
            "triangle"
    )

    fun getWaveformNames() : List<String>{
        return waveformNames
    }

    fun new(waveformName : String) : EnvelopeFactory {
//        if(waveformName == "sine"){
            return EnvelopeFactoryImpl({frequency -> EnvelopeImpl.buildEnvelope(SineWaveform(ConstantStream(frequency),44100)) })
//        }
    }
}