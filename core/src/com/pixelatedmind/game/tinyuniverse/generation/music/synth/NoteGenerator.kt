package com.pixelatedmind.game.tinyuniverse.generation.music.synth

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream

interface NoteGenerator : FloatInputStream {
    fun startNote(frequency : Float) : String
    fun stopNote(id : String)
}