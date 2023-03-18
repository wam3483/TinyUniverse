package com.pixelatedmind.game.tinyuniverse.generation.music.proc.patternfinder

import com.pixelatedmind.game.tinyuniverse.datastructure.Bitmap
import com.pixelatedmind.game.tinyuniverse.generation.music.proc.AutomataCell
import com.pixelatedmind.game.tinyuniverse.generation.music.proc.BandRolePatternFinder

class ChordArpeggioBandRole(val progression : List<List<Int>>, val beatsPerChord : Int): BandRolePatternFinder {
    override fun find(bitmap: Bitmap): List<AutomataCell> {

        val queryHelper = BandRoleQueryHelper(bitmap)
        var x = 0
        val width = bitmap.getWidth()
        val result = mutableListOf<AutomataCell>()
        while(x<width){
            val chordIndex = (x / beatsPerChord) % progression.size
            val chord = progression[chordIndex]
            chord.forEach{chromaticScaleIndex ->
                val cells = getAllCellsInRange(queryHelper, x, chromaticScaleIndex, beatsPerChord)
                result.addAll(cells)
            }
            x+=beatsPerChord
        }
        return result
    }

    private fun getAllCellsInRange(query : BandRoleQueryHelper, x : Int, y : Int, width: Int) : List<AutomataCell>{
        val result = mutableListOf<AutomataCell>()
        var x1 = x
        val right = x + width
        val queryInput = mutableListOf(y)
        while(x1 < right) {
            val cellList = query.queryProgression(x1, queryInput)
            if(cellList == null){
                x1++
            }else {
                val cell = cellList.first()
                result.add(cell)
                x1 += cell.width
            }
        }
        return result
    }
}