package com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform.SawtoothWaveform
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform.SineWaveform
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform.SquareWaveform
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform.TriangleWaveForm

class BaseWaveformStreamFactory(val samplingRate : Int) : StreamFactory {
    val sineId = "sine"
    val sawId = "saw"
    val squareId = "square"
    val triangleId = "triangle"

    override fun getWaveformIds() : List<String>{
        return listOf(sineId, sawId, squareId, triangleId)
    }

    override fun new(streamId: String, frequencyStream : FloatInputStream): FloatInputStream {
        if(streamId == sineId){
            return SineWaveform(frequencyStream, samplingRate)
        }else if(streamId == sawId){
            return SawtoothWaveform(frequencyStream, samplingRate)
        }
        else if(streamId == squareId){
            return SquareWaveform(frequencyStream, samplingRate)
        }
        else if(streamId == triangleId){
            return TriangleWaveForm(frequencyStream, samplingRate)
        }
        else{
            throw NotImplementedError("unknown stream id: [$streamId]")
        }
    }
}