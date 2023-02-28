package com.pixelatedmind.game.tinyuniverse.generation.music.song

import com.pixelatedmind.game.tinyuniverse.generation.music.*
import com.pixelatedmind.game.tinyuniverse.generation.music.filter.Envelope
import com.pixelatedmind.game.tinyuniverse.generation.music.song.model.InstrumentStreamModel
import com.pixelatedmind.game.tinyuniverse.generation.music.song.model.SongModel
import com.pixelatedmind.game.tinyuniverse.generation.music.song.model.NoteTone
import java.util.*

class SongStream(songModel : SongModel, val instrumentRepo : InstrumentRepository) : FloatInputStream {

    private val streams = mutableListOf<InstrumentStream>()
    private val timeSignature = TimeSignature(songModel.beatsPerMinute, songModel.timeSignatureBottomNumber)
    init{
        songModel.instrumentStreams.forEach{instrumentStreamModel->
            val noteFactory = instrumentRepo.getInstrument(instrumentStreamModel.name)
            streams.add(InstrumentStream(instrumentStreamModel, noteFactory, timeSignature))
        }
    }

    override fun read(timeInSeconds: Float): Float {
        var result = 0f
        streams.forEach{stream->
            result += stream.read(timeInSeconds)
        }
        result /= streams.size
        return result
    }
}