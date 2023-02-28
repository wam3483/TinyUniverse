package com.pixelatedmind.game.tinyuniverse.generation.music.song

import com.pixelatedmind.game.tinyuniverse.generation.music.EnvelopeFactory
import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.generation.music.Note
import com.pixelatedmind.game.tinyuniverse.generation.music.Notes

class SynthWar {
    fun generate(){
        val bpm = 116
    }
//    fun metronome(envelopeFactory: EnvelopeFactory, frequency : Float, tickIntervalSecs : Float = .25f, tickDurationSecs : Float = .1f, durationSecs : Float=120f)
//            : FloatInputStream {
//        val songNotes = mutableListOf<SongNote>()
//        var elapsedTime = 0f
//        while(elapsedTime < durationSecs){
//            val note = SongNote(elapsedTime,tickDurationSecs,frequency)
//            songNotes.add(note)
//            elapsedTime += tickIntervalSecs
//        }
//        return SongChannel(songNotes, envelopeFactory)
//    }
//
//    private fun firstSequence(a:Float, d:Float, f:Float, inc:Float) : List<SongNote>{
//        return listOf(
//                buildNote(a,inc),
//                buildNote(d, inc),
//                buildNote(f,inc),
//
//                buildNote(a,inc),
//                buildNote(d, inc),
//                buildNote(f,inc),
//
//                buildNote(a,inc),
//                buildNote(f,inc),
//
//
//                buildNote(a,inc),
//                buildNote(d, inc),
//                buildNote(f,inc),
//
//                buildNote(a,inc),
//                buildNote(d, inc),
//                buildNote(f,inc),
//
//                buildNote(a,inc),
//                buildNote(f,inc),
//
//
//                buildNote(a,inc),
//                buildNote(d, inc),
//                buildNote(f,inc),
//
//                buildNote(a,inc),
//                buildNote(d, inc),
//                buildNote(f,inc),
//
//                buildNote(a,inc),
//                buildNote(f,inc),
//
//
//                buildNote(a,inc),
//                buildNote(d, inc),
//                buildNote(f,inc),
//
//                buildNote(a,inc),
//                buildNote(d, inc),
//                buildNote(f,inc),
//
//                buildNote(a,inc),
//                buildNote(f,inc)
//        )
//    }
//
//    var timestamp = 0f
//    fun synthWars(envelopeFactory: EnvelopeFactory, octave : Int) : FloatInputStream {
//        val notes = Notes()
//        val inc = 2/16f
//        val a = notes.getNote(Note.A, octave)
//        val d = notes.increaseFrequencyBySemitones(a, 3)//notes.getNote(Note.D, octave)
//        val f = notes.increaseFrequencyBySemitones(d, 2)//notes.getNote(Note.C, octave+1)
//
//        val bflat = notes.increaseFrequencyBySemitones(a, 2)
//        val aFlat = notes.increaseFrequencyBySemitones(a, -1)
//        val bSharp = notes.increaseFrequencyBySemitones(a, 2)
//        val cSharp = notes.increaseFrequencyBySemitones(bSharp, 3)
//        val noteSequence = firstSequence(a,d,f,inc).toMutableList()
//
//        noteSequence.add(buildNote(bflat,inc))
//        noteSequence.add(buildNote(d,inc))
//        noteSequence.add(buildNote(f,inc))
//        noteSequence.add(buildNote(bflat,inc))
//        noteSequence.add(buildNote(d,inc))
//        noteSequence.add(buildNote(f,inc))
//        noteSequence.add(buildNote(bflat,inc))
//        noteSequence.add(buildNote(f,inc))
//
//
//        noteSequence.add(buildNote(aFlat,inc))
//        noteSequence.add(buildNote(bSharp,inc))
//        noteSequence.add(buildNote(cSharp,inc))
//        noteSequence.add(buildNote(aFlat,inc))
//        noteSequence.add(buildNote(bSharp,inc))
//        noteSequence.add(buildNote(cSharp,inc))
//
//        return SongChannel(noteSequence, envelopeFactory)
//    }
//
//    private fun buildNote(freq : Float, inc : Float) : SongNote{
//        val result= SongNote(timestamp, .2f, freq)
//        timestamp += inc
//        return result
//    }
}