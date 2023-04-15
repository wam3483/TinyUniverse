package com.pixelatedmind.game.tinyuniverse.ui

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect.EffectFactory
import com.pixelatedmind.game.tinyuniverse.ui.model.NameableId

class EffectRepository {
    private val modelEffectFactoryMap = mutableMapOf<NameableId, EffectFactory>()

    fun getAllIds() : List<NameableId> {
        return modelEffectFactoryMap.keys.toList()
    }

    fun clear(){
        modelEffectFactoryMap.clear()
    }

    fun get(id : NameableId) : EffectFactory? {
        return modelEffectFactoryMap[id]
    }

    fun add(id : NameableId, factory : EffectFactory){
        modelEffectFactoryMap[id] = factory
    }

    fun remove(id : NameableId) : Boolean {
        return modelEffectFactoryMap.remove(id) != null
    }
}