package com.pixelatedmind.game.tinyuniverse.generation.music.proc.patternfinder

import com.pixelatedmind.game.tinyuniverse.datastructure.Bitmap
import com.pixelatedmind.game.tinyuniverse.generation.music.proc.AutomataCell


class BitArray(val bitmap : Bitmap, val row : Int) {

    fun getNextSequence(startIndex : Int) : AutomataCell? {
        var x = startIndex
        //scan till we find a true cell or exceed bounds of bitmap
        while(x<bitmap.getWidth() && !bitmap.getValue(x, row)) x++

        //if we exceeded bounds of bitmap there's nothing left to find
        if(x >= bitmap.getWidth()){
            return null
        }

        //we have the start of a sequence, now we scan till it flips false.
        var xEnd = x + 1
        while(xEnd<bitmap.getWidth() && bitmap.getValue(xEnd, row)) xEnd++

        return AutomataCell(x, row, xEnd - x, false)
    }
}