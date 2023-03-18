package com.pixelatedmind.game.tinyuniverse.generation.music.song

import com.pixelatedmind.game.tinyuniverse.generation.music.song.model.SongModel
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream

class SongStream(songModel : SongModel, val instrumentRepo : InstrumentRepository) : FloatInputStream {

    private val streams = mutableListOf<InstrumentStream>()
    private val timeSignature = TimeSignature(songModel.beatsPerMinute, songModel.timeSignatureBottomNumber)
    val maximumGain : Float
    init{
        var maxGain = Float.MIN_VALUE
        songModel.instrumentStreams.forEach{instrumentStreamModel->
            maxGain = Math.max(maxGain.toDouble(), instrumentStreamModel.gain.toDouble()).toFloat()
            val noteFactory = instrumentRepo.getInstrument(instrumentStreamModel.name)
            streams.add(InstrumentStream(instrumentStreamModel, noteFactory, timeSignature))
        }
        maximumGain = maxGain
    }

    override fun read(timeInSeconds: Float): Float {
        var result = 0f
        streams.forEach{stream->
            val gain = stream.streamModel.gain / maximumGain
            result += stream.read(timeInSeconds) * gain
        }
        result /= streams.size

        return result
    }
}