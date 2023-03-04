package com.pixelatedmind.game.tinyuniverse.generation.cellautomata

import com.badlogic.gdx.math.MathUtils
import com.pixelatedmind.game.tinyuniverse.extensions.bits.toBooleanList

class CellularAutomataBuilder {
    //the bitflags for nextstate with each value calculated by translating values from current state into 3 bit numbers
    private var ruleNumber : Int = 30
    //initial state
    private var initialAutomataState = mutableListOf<Boolean>()
    private var height : Int = 40

    fun setHeight(height : Int) : CellularAutomataBuilder {
        this.height = height
        return this
    }

    fun setInitialState(state : List<Boolean>) : CellularAutomataBuilder{
        this.initialAutomataState.clear()
        this.initialAutomataState.addAll(state)
        return this
    }

    fun setRule(ruleNumber : Int) : CellularAutomataBuilder{
        this.ruleNumber = ruleNumber
        return this
    }

    fun validate(){
        if(initialAutomataState.size <3) {
            throw IllegalStateException("Initial state must have at least 3 elements")
        }
    }
    fun build(){
        val patternGen = AutomataPatternGenerator3N(ruleNumber)
    }
}