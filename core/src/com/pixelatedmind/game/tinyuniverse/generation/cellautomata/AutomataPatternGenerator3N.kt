package com.pixelatedmind.game.tinyuniverse.generation.cellautomata

import com.pixelatedmind.game.tinyuniverse.extensions.bits.getBit
import com.pixelatedmind.game.tinyuniverse.extensions.bits.setBit

class AutomataPatternGenerator3N(outputBits : Int) {
    private val patterns : MutableList<Boolean> = mutableListOf()

    init{
        //unpack outputPattern
        //each bit is an output for a specific pattern. the pattern to match on is the
        //binary representation of a bit's index, so rule 30 for instance:
        //30 in decimal = 00011110
        // bit 0 = 000 -> 0
        // bit 1 = 001 -> 1
        // bit 2 = 010 -> 1
        // bit 3 = 011 -> 1
        // bit 4 = 100 -> 1
        // bit 5 = 101 -> 0 etc.
        //all this is really doing is unpacking the first 8 bits of an int into a boolean array.
        var outputPatternBitIndex = 0
        while(outputPatternBitIndex < 8){
            val state = outputBits.getBit(outputPatternBitIndex)
            patterns.add(state)
            ++outputPatternBitIndex
        }
    }

    fun generatePattern(currentState : List<Boolean>, nextStateOutput : MutableList<Boolean> = mutableListOf()) : List<Boolean>{
        var index = 0
        while(index< currentState.size){
            val bitPattern = getAutomataPatternIndex(currentState, index)
            nextStateOutput.add(patterns[bitPattern])
            ++index
        }
        return nextStateOutput
    }

    private fun getAutomataPatternIndex(neighbors: List<Boolean>, index : Int) : Int{
        var automataPatternIndex = 0

        var i = 1
        var bitIndex = 0
        while(i>-2){
            val indexValue = index + i
            val bit = getValueAt(neighbors, indexValue)
            automataPatternIndex = automataPatternIndex.setBit(bitIndex, bit)
            i--
            bitIndex++
        }

        return automataPatternIndex
    }

    private fun getValueAt(neighbors: List<Boolean>, index : Int) : Boolean {
        var cyclicIndex = index % neighbors.size
        if(index < 0){
            cyclicIndex += neighbors.size - 1
        }
        return neighbors[cyclicIndex]
    }
}