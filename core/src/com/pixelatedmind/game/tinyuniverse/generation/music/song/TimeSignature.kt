package com.pixelatedmind.game.tinyuniverse.generation.music.song

import com.pixelatedmind.game.tinyuniverse.generation.music.song.model.NoteLength

class TimeSignature(val bpm : Float, val bottomTimeSignatureNumber : Int) {
    private val noteLengths : Map<NoteLength, Float>
    init{
        noteLengths = getNoteLengthInSeconds()
    }

    fun getBeatsPerSecond() : Float{
        return bpm / 60f
    }

    fun getBeatDurationSeconds() : Float{
        return 1 / getBeatsPerSecond()
    }

    fun getNoteLengthInSeconds(noteBeat : NoteLength) : Float{
        return noteLengths[noteBeat]!!
    }

    private fun getNoteLengthInSeconds() : Map<NoteLength, Float>{
        val noteLength = mutableMapOf<NoteLength, Float>()
        val beatsQuarterNoteReceives = bottomTimeSignatureNumber / 4
        val quarterNoteTime = beatsQuarterNoteReceives * getBeatDurationSeconds()

        noteLength[NoteLength.Whole] = quarterNoteTime * 4f
        noteLength[NoteLength.Half] = quarterNoteTime * 2f
        noteLength[NoteLength.Quarter] = quarterNoteTime
        noteLength[NoteLength.Eigth] = quarterNoteTime / 2f
        noteLength[NoteLength.Sixtenth] = quarterNoteTime / 4f
        noteLength[NoteLength.ThirtySecond] = quarterNoteTime / 8f
        noteLength[NoteLength.SixtyFourth] = quarterNoteTime / 16f

        return noteLength
    }
}