package com.pixelatedmind.game.tinyuniverse.generation.music.proc

import com.pixelatedmind.game.tinyuniverse.generation.music.Scale
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
        var lastIndex = allNotesOrdered.indexOf(rootNote)
        this.height = height
        this.lowestOctave = lowestOctave
        val semitoneIndices = scaleIntervals.map{
            lastIndex += it
            lastIndex
        }
        scale = semitoneIndices.map{
            val octave = it / 12
            val note = allNotesOrdered[it % 12]
            AutomataToneModel(octave, note)
        }
        this.rootNote = rootNote
        this.intervals = scaleIntervals
    }

    override fun map(index: Int): AutomataToneModel {
        //bigger values need to map to lower indices in scale
        //because i want scale to ascend from bottom of automata pattern.
        val scaleIndex = height - index
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