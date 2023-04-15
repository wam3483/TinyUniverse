package com.pixelatedmind.game.tinyuniverse.ui

import com.pixelatedmind.game.tinyuniverse.ui.model.OscillatorModel

class OscillatorRepository {
    private val oscillators = mutableListOf<OscillatorModel>()
    fun add(model : OscillatorModel){
        oscillators.add(model)
    }

    fun clear() {
        oscillators.clear()
    }

    fun remove(model : OscillatorModel){
        oscillators.remove(model)
    }

    fun getOscillators() : List<OscillatorModel>{
        return oscillators
    }
}