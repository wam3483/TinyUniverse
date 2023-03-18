package com.pixelatedmind.game.tinyuniverse.generation.music.proc

import com.pixelatedmind.game.tinyuniverse.generation.music.model.Scale
import com.pixelatedmind.game.tinyuniverse.generation.music.song.model.NoteTone


//there are 2 relevant "octaves." There's the C major octave I key everything on, and the octave a scale has as I map
//notes from a pattern by y position. the higher the y position, the higher the note.
//
//technically octaves are defined by scales but since i've defined everything in absolute values rather than semitones
//i need a mapper to resolve relative intervals into absolute notes. This class does that.
class AutomataScaleMapperImpl : AutomataScaleMapper {

    val height : Int
    val lowestOctave : Int

    private val scale : List<AutomataToneModel>
    val rootNote : NoteTone
    val intervals : List<Int>

    constructor(lowestOctave : Int, height : Int, rootNote : NoteTone, scale : Scale) : this(lowestOctave, height, rootNote, scale.notation.toList())

    constructor(lowestOctave : Int, height : Int, rootNote : NoteTone, scaleIntervals : List<Int>){
        this.height = height
        this.lowestOctave = lowestOctave
        this.rootNote = rootNote
        this.intervals = scaleIntervals

        //map notes using the index of root note as our origin
        // translate by our scale's intervals to derive each note in the scale
        var lastIndex = allNotesOrdered.indexOf(rootNote)
        val semitoneIndices = scaleIntervals.map{
            lastIndex + it
        }

        //I require a note be defined by its letter and octave in the key of C on a chromatic scale.
        //This is because my midi table to look up tuned frequencies for a note is structured in this way.
        //This maps our scale's notes from absolute intervals to an object later used to map onto the midi table
        scale = semitoneIndices.map{
            val octave = it / 12
            val note = allNotesOrdered[it % 12]
            AutomataToneModel(octave, note)
        }
    }

    override fun map(index: Int): AutomataToneModel {
        //bigger values need to map to lower indices in scale
        //because i want scale to ascend from bottom of automata pattern.
        val scaleIndex = index
        val octave = scaleIndex / scale.size + lowestOctave
        val note = scale[scaleIndex % scale.size]
        return AutomataToneModel(note.octave + octave, note.tone)
    }

    companion object{
        val allNotesOrdered = listOf(
                NoteTone.C,
                NoteTone.CSharp,
                NoteTone.D,
                NoteTone.DSharp,
                NoteTone.E,
                NoteTone.F,
                NoteTone.FSharp,
                NoteTone.G,
                NoteTone.GSharp,
                NoteTone.A,
                NoteTone.ASharp,
                NoteTone.B
        )
    }
}