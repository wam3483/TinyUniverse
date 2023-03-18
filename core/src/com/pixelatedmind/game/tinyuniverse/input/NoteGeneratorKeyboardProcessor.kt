package com.pixelatedmind.game.tinyuniverse.input

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.pixelatedmind.game.tinyuniverse.generation.music.model.Note
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.NoteGenerator
import com.pixelatedmind.game.tinyuniverse.generation.music.Notes

class NoteGeneratorKeyboardProcessor(private val noteGenerator : NoteGenerator) : InputAdapter() {

    private val keycodeToFrequency = mutableMapOf<Int, Float>()
    private val keycodeToNoteId = mutableMapOf<Int, String>()
    private val notes = Notes()

    fun setOctave(octave : Int){
        keycodeToFrequency[Input.Keys.Q] = notes.getNote(Note.C,octave)
        keycodeToFrequency[Input.Keys.W] = notes.getNote(Note.CSharp,octave)
        keycodeToFrequency[Input.Keys.E] = notes.getNote(Note.D,octave)
        keycodeToFrequency[Input.Keys.R] = notes.getNote(Note.DSharp,octave)
        keycodeToFrequency[Input.Keys.T] = notes.getNote(Note.E,octave)
        keycodeToFrequency[Input.Keys.Y] = notes.getNote(Note.F,octave)
        keycodeToFrequency[Input.Keys.U] = notes.getNote(Note.FSharp,octave)
        keycodeToFrequency[Input.Keys.I] = notes.getNote(Note.G,octave)
        keycodeToFrequency[Input.Keys.O] = notes.getNote(Note.GSharp,octave)
        keycodeToFrequency[Input.Keys.P] = notes.getNote(Note.A,octave)
        keycodeToFrequency[Input.Keys.LEFT_BRACKET] = notes.getNote(Note.ASharp,octave)
        keycodeToFrequency[Input.Keys.RIGHT_BRACKET] = notes.getNote(Note.B,octave)
    }

    init {
        setOctave(4)
    }

    override fun keyTyped(character: Char): Boolean {
        if(character.isDigit()){
            var numValue = Character.getNumericValue(character)
            if(numValue<10){
                if(numValue == 0){
                    numValue = 10
                }
                setOctave(numValue)
                return true
            }
        }
        return super.keyTyped(character)
    }

    override fun keyDown(keycode: Int): Boolean {
        val frequency = keycodeToFrequency[keycode]
        if(frequency != null){
            val noteId = noteGenerator.startNote(frequency)
            keycodeToNoteId[keycode] = noteId
            return true
        }
        return super.keyDown(keycode)
    }

    override fun keyUp(keycode: Int): Boolean {
        val id = keycodeToNoteId[keycode]
        if(id!=null) {
            noteGenerator.stopNote(id)
            keycodeToNoteId[keycode] = id
        }
        return keycodeToNoteId.remove(keycode)!= null
    }
}