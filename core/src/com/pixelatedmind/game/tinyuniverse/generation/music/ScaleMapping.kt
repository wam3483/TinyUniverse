package com.pixelatedmind.game.tinyuniverse.generation.music

class ScaleMapping {
    private val note = Notes()

    ///Returns midi indicates of the specified scale using specified root note as the scale's root note, within the specified octave.
    fun getMidiIndicesForScale(rootNote : Note, scale : Scale, octave:Int) : List<Int> {
        val semitoneOffsets = scale.notation
        val noteIndex = note.getNoteMidiIndex(rootNote, octave)
        val scaleMidiIndices = mutableListOf<Int>()
        scaleMidiIndices.add(noteIndex)
        semitoneOffsets.forEach {
            semitoneOffset->
                val lastNoteMidiIndex = scaleMidiIndices.get(scaleMidiIndices.size - 1)
                scaleMidiIndices.add(lastNoteMidiIndex + semitoneOffset)
        }
        return scaleMidiIndices
    }
}