package com.pixelatedmind.game.tinyuniverse.generation.music.proc.patternfinder

import com.pixelatedmind.game.tinyuniverse.datastructure.Bitmap
import com.pixelatedmind.game.tinyuniverse.generation.music.proc.AutomataCell

class BandRoleQueryHelper(val bitmap : Bitmap) {
    private val bitArrays = mutableListOf<BitArray>()

    init{
        var i = 0
        while(i<bitmap.getHeight()){
            bitArrays.add(BitArray(bitmap, i))
            i++
        }
    }

    /***
     * Loop search means when the end of note progress is found, search will begin at beginning
     * until bitmap is exhausted
     */
    fun queryProgression(startX : Int, notes : List<List<Int>>, loopSearch : Boolean = false) : List<List<AutomataCell>>{
        var x = startX
        var i = 0
        val progression = mutableListOf<List<AutomataCell>>()
        while((i<notes.size || loopSearch) && x < bitmap.getWidth()){
            var index = i
            if(loopSearch){
                index = i % notes.size
            }
            val chord = notes[index]

            val cells = queryProgression(x, chord)

            if(cells != null){
                cells.forEach{
                    x = Math.max(x, it.right())
                }
                progression.add(cells)
                i++
            }
            else {
                x++
            }
        }
        return progression
    }

    fun queryProgression(startX : Int, chord : List<Int>) : List<AutomataCell>?{
        val cells = chord.map{note->
            bitArrays[note].getNextSequence(startX)
        }
        if(cells.any{it==null}) {
            return null
        }

        val nonNullCells = cells.map{it!!}
        if(chord.size>1){
            nonNullCells.forEachIndexed{index, value ->
                var x = index + 1
                while(x<cells.size){
                    if(!value.intersects(nonNullCells[x])){
                        return null
                    }
                    x++
                }
            }
        }

        return nonNullCells
    }
}