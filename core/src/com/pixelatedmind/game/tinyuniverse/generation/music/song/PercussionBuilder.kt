package com.pixelatedmind.game.tinyuniverse.generation.music.song

import com.pixelatedmind.game.tinyuniverse.generation.music.song.model.*

class PercussionBuilder {
    fun newSong(ryhthm : Map<String, List<Boolean>>) : SongModel {
        val bpm = 120
        val timeSigBottom = 4
        val name = ""
        val noteLength = NoteLength.Quarter
        val drumMapper = ConstantRhythmNoteStreamMapperImpl(NoteModel(4, NoteTone.D, listOf(noteLength)))
        val octave = 6
        val instrumentMapper = RhythmNoteStreamMapperImpl(
                listOf(
                    listOf(NoteModel(octave, NoteTone.B, listOf(NoteLength.Quarter))),
                    listOf(NoteModel(octave, NoteTone.DSharp, listOf(NoteLength.Eigth)),
                            NoteModel(octave, NoteTone.G, listOf(NoteLength.Eigth)),
                            NoteModel(octave, NoteTone.B, listOf(NoteLength.Quarter))),
                    listOf(NoteModel(octave, NoteTone.DSharp, listOf(NoteLength.Quarter)),
                            NoteModel(octave, NoteTone.G, listOf(NoteLength.Quarter))),
                    listOf(NoteModel(octave, NoteTone.DSharp, listOf(NoteLength.Half)))
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
        private val restNote : NoteModel = NoteModel(0, NoteTone.Rest, listOf(restDuration))
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