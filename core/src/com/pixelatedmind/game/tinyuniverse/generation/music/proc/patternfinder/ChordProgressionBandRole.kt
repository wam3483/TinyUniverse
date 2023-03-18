package com.pixelatedmind.game.tinyuniverse.generation.music.proc.patternfinder

import com.pixelatedmind.game.tinyuniverse.datastructure.Bitmap
import com.pixelatedmind.game.tinyuniverse.generation.music.proc.AutomataCell
import com.pixelatedmind.game.tinyuniverse.generation.music.proc.BandRolePatternFinder

class ChordProgressionBandRole(val progression : List<List<Int>>) : BandRolePatternFinder {

    override fun find(bitmap: Bitmap): List<AutomataCell> {
        val queryHelper = BandRoleQueryHelper(bitmap)
        val result = queryHelper.queryProgression(0, progression, true)
        return result.flatten()
    }
}