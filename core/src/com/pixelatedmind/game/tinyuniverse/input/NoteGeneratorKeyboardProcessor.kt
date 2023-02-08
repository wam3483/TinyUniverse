package com.pixelatedmind.game.tinyuniverse.input

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.pixelatedmind.game.tinyuniverse.generation.music.NoteGenerator
import com.pixelatedmind.game.tinyuniverse.generation.music.Notes

class NoteGeneratorKeyboardProcessor(private val noteGenerator : NoteGenerator) : InputAdapter() {

    private val keycodeToFrequency = mutableMapOf<Int, Float>()
    private val keycodeToNoteId = mutableMapOf<Int, String>()
    private val notes = Notes()

    init {
        keycodeToFrequency[Input.Keys.Z] = notes.midi[60]
        keycodeToFrequency[Input.Keys.X] = notes.midi[62]
        keycodeToFrequency[Input.Keys.C] = notes.midi[64]
        keycodeToFrequency[Input.Keys.V] = notes.midi[65]
        keycodeToFrequency[Input.Keys.B] = notes.midi[67]
    }

    override fun keyDown(keycode: Int): Boolean {
        val frequency = keycodeToFrequency[keycode]
        if(frequency != null){
            val noteId = noteGenerator.startNote(frequency)
            keycodeToNoteId[keycode] = noteId
        }
        return super.keyDown(keycode)
    }

    override fun keyUp(keycode: Int): Boolean {
        val id = keycodeToNoteId[keycode]
        if(id!=null) {
            noteGenerator.stopNote(id)
            keycodeToNoteId[keycode] = id
        }
        keycodeToNoteId.remove(keycode)
        return super.keyUp(keycode)
    }
}