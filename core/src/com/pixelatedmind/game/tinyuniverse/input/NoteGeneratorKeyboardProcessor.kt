package com.pixelatedmind.game.tinyuniverse.input

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.pixelatedmind.game.tinyuniverse.generation.music.model.Note
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.NoteGenerator
import com.pixelatedmind.game.tinyuniverse.generation.music.Notes

class NoteGeneratorKeyboardProcessor(private val noteGenerator : NoteGenerator) : InputAdapter() {

    private val keycodeToFrequency = mutableMapOf<Int, Note>()
    private val keycodeToNoteId = mutableMapOf<Int, String>()
    private val notes = Notes()

    private var octave : Int = 4

    init {
        keycodeToFrequency[Input.Keys.A] = Note.C
        keycodeToFrequency[Input.Keys.W] = Note.CSharp
        keycodeToFrequency[Input.Keys.S] = Note.D
        keycodeToFrequency[Input.Keys.E] = Note.DSharp
        keycodeToFrequency[Input.Keys.D] = Note.E
        keycodeToFrequency[Input.Keys.F] = Note.F
        keycodeToFrequency[Input.Keys.T] = Note.FSharp
        keycodeToFrequency[Input.Keys.G] = Note.G
        keycodeToFrequency[Input.Keys.Y] = Note.GSharp
        keycodeToFrequency[Input.Keys.H] = Note.A
        keycodeToFrequency[Input.Keys.U] = Note.ASharp
        keycodeToFrequency[Input.Keys.J] = Note.B
    }

    override fun keyTyped(character: Char): Boolean {
        if(character.isDigit()){
            var numValue = Character.getNumericValue(character)
            if(numValue<10){
                if(numValue == 0){
                    numValue = 10
                }
                octave = numValue
                return true
            }
        }
        return false
    }

    override fun keyDown(keycode: Int): Boolean {
        val note = keycodeToFrequency[keycode]
        if(note != null){
            val frequency = notes.getNote(note,octave)
            val noteId = noteGenerator.startNote(frequency)
            keycodeToNoteId[keycode] = noteId
            println("key down : keycode[$keycode] id=[$noteId]")
            return true
        }
        return super.keyDown(keycode)
    }

    override fun keyUp(keycode: Int): Boolean {
        val id = keycodeToNoteId[keycode]
        println("key up : keycode=[$keycode] id=[$id]")
        if(id!=null) {
            noteGenerator.stopNote(id)
            keycodeToNoteId[keycode] = id
        }
        return keycodeToNoteId.remove(keycode)!= null
    }
}