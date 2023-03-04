package com.pixelatedmind.game.tinyuniverse.generation.music.song

import com.pixelatedmind.game.tinyuniverse.generation.music.song.model.*

class SongBuilder {
    fun newSong(ryhthm : Map<String, List<Boolean>>) : SongModel {
        val bpm = 120f*1.2f
        val timeSigBottom = 4
        val name = ""
        val noteLength = NoteLength.Quarter
        val drumMapper = ConstantRhythmNoteStreamMapperImpl(NoteModel(4, NoteTone.D, noteLength))
        val octave = 4
        val instrumentMapper = RhythmNoteStreamMapperImpl(
                listOf(
                    listOf(NoteModel(octave, NoteTone.B, NoteLength.Quarter)),
                    listOf(NoteModel(octave, NoteTone.DSharp, NoteLength.Eigth), NoteModel(octave, NoteTone.G, NoteLength.Eigth), NoteModel(octave, NoteTone.B, NoteLength.Quarter)),
                    listOf(NoteModel(octave, NoteTone.DSharp, NoteLength.Quarter), NoteModel(octave, NoteTone.G, NoteLength.Quarter)),
                    listOf(NoteModel(octave, NoteTone.DSharp, NoteLength.Half))
                ),
                NoteLength.Quarter, 1)
        val noteStreams =
                ryhthm.map{key->
                    val noteMapper =
                        if(key.key == "organ") instrumentMapper
                        else drumMapper
                    Pair(key.key, noteMapper.map(key.value))
                }
        val streams = noteStreams.map{
            InstrumentStreamModel(
                    it.first, it.second
            )
        }
        return SongModel(name,bpm,timeSigBottom,streams)
    }
    interface RhythmNoteStreamMapper{
        fun map(beats : List<Boolean>) : List<NoteModel>
    }
    class RhythmNoteStreamMapperImpl(val sequence : List<List<NoteModel>>, restDuration : NoteLength, val repeatNoteSequence : Int = 1) : RhythmNoteStreamMapper{
        private val restNote : NoteModel = NoteModel(0, NoteTone.Rest, restDuration)
        private var index = 0
        init{

        }
        override fun map(beats: List<Boolean>): List<NoteModel> {
            val result = mutableListOf<NoteModel>()
            beats.forEach{beat ->
                if(beat){
                    val notes = sequence[index]
                    repeat(repeatNoteSequence){
                        result.addAll(notes)
                    }
                    ++index
                    if(index>=sequence.size){
                        index = 0
                    }
                }else{
                    result.add(restNote)
                }
            }
            return result
        }
    }
    class ConstantRhythmNoteStreamMapperImpl (val noteModel :NoteModel) : RhythmNoteStreamMapper{
        private val restNote : NoteModel = NoteModel(0, NoteTone.Rest, noteModel.noteLength)
        override fun map(beats : List<Boolean>) : List<NoteModel>{
            return beats.map{beat->
                if(beat)
                    noteModel
                else
                    restNote
            }
        }
    }
}