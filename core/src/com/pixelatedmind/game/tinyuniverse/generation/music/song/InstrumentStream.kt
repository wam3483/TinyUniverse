package com.pixelatedmind.game.tinyuniverse.generation.music.song

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.EnvelopeFactory
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.generation.music.Notes
import com.pixelatedmind.game.tinyuniverse.generation.music.model.Note
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.Envelope
import com.pixelatedmind.game.tinyuniverse.generation.music.song.model.InstrumentStreamModel
import com.pixelatedmind.game.tinyuniverse.generation.music.song.model.NoteModel
import com.pixelatedmind.game.tinyuniverse.generation.music.song.model.NoteTone
import java.util.*

class InstrumentStream(val streamModel : InstrumentStreamModel, val noteFactory : EnvelopeFactory, val timeSignature : TimeSignature) : FloatInputStream {
    //just because a note is released doesn't mean it's done making sound

    private val playedNotes = mutableSetOf<NoteModel>()

    private val releasingNotes = LinkedList<SimulatedNote>()
    val oneBeatSecs : Float

    init{
        oneBeatSecs = timeSignature.getNoteLengthInSeconds(listOf(timeSignature.getOneBeatLength()))
//        startNotes(0)
    }

    private fun startNotes(elapsedBeats : Int) {
//            println("elapsed beats "+elapsedBeats)
        val currentNotes = streamModel.stream.filter{it.startBeat == elapsedBeats && !playedNotes.contains(it)}
        playedNotes.addAll(currentNotes)
        currentNotes.forEach{currentNote->
            if(currentNote.tone != NoteTone.Rest) {
                val noteLength = timeSignature.getNoteLengthInSeconds(currentNote.noteLength)
                val startTime = oneBeatSecs * currentNote.startBeat
                val mappedNote = noteMap[currentNote.tone]!!
                val noteFrequency = noteUtil.getNote(mappedNote, currentNote.octave)
                val newEnvelope = noteFactory.newEnvelope(noteFrequency)

                val simulatedNote = SimulatedNote(startTime, noteLength, newEnvelope)
                releasingNotes.add(simulatedNote)
                println("start new note ${noteFactory}: beat=${currentNote.startBeat} note=${currentNote.tone} octave=${currentNote.octave} length=${currentNote.noteLength}")
            }
        }
    }

    var maxSimultaneousNotes = 1
    override fun read(timeInSeconds: Float): Float {
        val beatsElapsed = (timeInSeconds / oneBeatSecs).toInt()
        startNotes(beatsElapsed)

        releasingNotes.forEach{env->
            val deltaSecs = timeInSeconds - env.startTime
            if(deltaSecs >= env.noteLengthSecs){
                env.envelope.release()
            }
        }
        val finishedNotes = releasingNotes.filter{it.envelope.isComplete()}
        releasingNotes.removeAll(finishedNotes.toSet())

        var result = 0f
        releasingNotes.forEach{
            result += it.envelope.read(timeInSeconds)
        }
        maxSimultaneousNotes = Math.max(maxSimultaneousNotes, releasingNotes.size)
        if(releasingNotes.size>0) {
            return result / maxSimultaneousNotes
        }
        return 0f
    }

    companion object{
        private val noteMap = mapOf(
                NoteTone.A to Note.A,
                NoteTone.ASharp to Note.ASharp,
                NoteTone.B to Note.B,
                NoteTone.C to Note.C,
                NoteTone.CSharp to Note.CSharp,
                NoteTone.D to Note.D,
                NoteTone.DSharp to Note.DSharp,
                NoteTone.E to Note.E,
                NoteTone.F to Note.F,
                NoteTone.FSharp to Note.FSharp,
                NoteTone.G to Note.G,
                NoteTone.GSharp to Note.GSharp
        )
        private val noteUtil = Notes()
    }
}
class SimulatedNote(val startTime : Float, val noteLengthSecs : Float, val envelope : Envelope){
}