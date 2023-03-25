package com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform.SawtoothWaveform
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform.SineWaveform
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform.SquareWaveform
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform.TriangleWaveForm

class BaseWaveformStreamFactory : StreamFactory {
    val sineId = "sine"
    val sawId = "saw"
    val squareId = "square"
    val triangleId = "triangle"
    fun getBaseWaveformIds() : List<String>{
        return listOf(sineId, sawId, squareId, triangleId)
    }
    override fun new(streamId: String): FloatInputStream {
        if(streamId == sineId){
            return SineWaveform(ConstantStream(1f))
        }else if(streamId == sawId){
            return SawtoothWaveform(ConstantStream(2f))
        }
        else if(streamId == squareId){
            return SquareWaveform(ConstantStream(1f))
        }
        else if(streamId == triangleId){
            return TriangleWaveForm(ConstantStream(1f))
        }
        else{
            throw NotImplementedError("unknown stream id: [$streamId]")
        }
    }
}