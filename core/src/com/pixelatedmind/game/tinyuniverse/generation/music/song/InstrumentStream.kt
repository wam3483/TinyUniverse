package com.pixelatedmind.game.tinyuniverse.generation.music.song

import com.pixelatedmind.game.tinyuniverse.generation.music.EnvelopeFactory
import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.generation.music.Note
import com.pixelatedmind.game.tinyuniverse.generation.music.Notes
import com.pixelatedmind.game.tinyuniverse.generation.music.filter.Envelope
import com.pixelatedmind.game.tinyuniverse.generation.music.song.model.InstrumentStreamModel
import com.pixelatedmind.game.tinyuniverse.generation.music.song.model.NoteTone
import java.util.*

class InstrumentStream(val streamModel : InstrumentStreamModel, val noteFactory : EnvelopeFactory, val timeSignature : TimeSignature) : FloatInputStream {
    private var index = -1

    private var noteLength : Float = 0f
    private var noteStartTime : Float = 0f
    private var openNote : Envelope? = null

    //just because a note is released doesn't mean it's done making sound
    private val releasingNotes = LinkedList<Envelope>()

    init{
        startNewNote()
    }

    private fun startNewNote() {
        ++index
        if(openNote!=null) {
            openNote!!.release()
        }
        if(index < streamModel.stream.size) {
            val currentNote = streamModel.stream[index]
            noteLength = timeSignature.getNoteLengthInSeconds(currentNote.noteLength)
            if(currentNote.tone != NoteTone.Rest) {
                val mappedNote = noteMap[currentNote.tone]!!
                val noteFrequency = noteUtil.getNote(mappedNote, currentNote.octave)
                val newEnvelope = noteFactory.newEnvelope(noteFrequency)
                openNote = newEnvelope
                releasingNotes.add(newEnvelope)
            }
            println("start new note: note=${currentNote.tone} length=${currentNote.noteLength}")
        }
    }

    override fun read(timeInSeconds: Float): Float {
        val deltaTime = timeInSeconds - noteStartTime
        if(deltaTime >= noteLength){
            noteStartTime = timeInSeconds
            startNewNote()
        }

        val finishedNotes = releasingNotes.filter{it.isComplete()}
        releasingNotes.removeAll(finishedNotes.toSet())

        var result = 0f
        releasingNotes.forEach{
            result += it.read(timeInSeconds)
        }
        return result
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