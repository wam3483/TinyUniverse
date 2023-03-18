package com.pixelatedmind.game.tinyuniverse.generation.music.proc

import com.pixelatedmind.game.tinyuniverse.generation.music.song.model.NoteLength
import com.pixelatedmind.game.tinyuniverse.generation.music.song.model.NoteModel
import com.pixelatedmind.game.tinyuniverse.generation.music.song.model.NoteTone

class PatternNoteSequenceMapper(val toneMapper : AutomataScaleMapper) {
    fun map(pattern : List<AutomataCell>) : List<NoteModel> {
        var lastCell : AutomataCell? = null
        val noteStream = mutableListOf<NoteModel>()
        pattern.forEach{cell ->
            val noteModel = if(cell.isRest) {
                NoteModel(0,NoteTone.Rest, cell.xIndex, List(cell.width){NoteLength.Quarter})
            } else {
                val noteTone = toneMapper.map(cell.yIndex)
                NoteModel(noteTone.octave,noteTone.tone, cell.xIndex, List(cell.width){NoteLength.Quarter})
            }
            noteStream.add(noteModel)
            lastCell = cell
        }
        return noteStream.sortedBy{it.startBeat}
    }
}