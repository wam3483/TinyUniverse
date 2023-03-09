package com.pixelatedmind.game.tinyuniverse.generation.music.proc

import com.pixelatedmind.game.tinyuniverse.datastructure.Bitmap

interface BandRolePatternFinder {
    fun find(bitmap : Bitmap) : List<AutomataCell>
}