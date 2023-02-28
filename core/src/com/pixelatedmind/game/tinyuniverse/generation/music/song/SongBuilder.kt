package com.pixelatedmind.game.tinyuniverse.generation.music.song

import com.pixelatedmind.game.tinyuniverse.generation.music.song.model.*

class SongBuilder {
    fun newSong(ryhthm : Map<String, List<Boolean>>) : SongModel {
        val bpm = 120f*1
        val timeSigBottom = 4
        val name = ""
        val noteLength = NoteLength.Quarter
        val noteStreams =
                ryhthm.map{key->
                    val note =
                        if(key.key == "organ") NoteTone.FSharp
                        else NoteTone.D
                    val octave =
                        if(key.key == "organ") 5
                        else 4
                    key.key to key.value.map {
                        if (it) NoteModel(octave, note, noteLength)
                        else NoteModel(octave, NoteTone.Rest, noteLength)
                    }
                }
        val streams = noteStreams.map{
            InstrumentStreamModel(
                    it.first, it.second
            )
        }
        return SongModel(name,bpm,timeSigBottom,streams)
    }
}