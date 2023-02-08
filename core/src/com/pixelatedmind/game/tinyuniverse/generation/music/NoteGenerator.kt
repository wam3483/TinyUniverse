package com.pixelatedmind.game.tinyuniverse.generation.music

interface NoteGenerator : FloatInputStream {
    fun startNote(frequency : Float) : String
    fun stopNote(id : String)
}