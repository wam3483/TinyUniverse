package com.pixelatedmind.game.tinyuniverse.ui.events

import com.pixelatedmind.game.tinyuniverse.ui.model.EffectRef
import com.pixelatedmind.game.tinyuniverse.ui.model.OscillatorModel

class OscillatorCRUDEventHandler(val model : OscillatorModel) {
    fun onEffectRemovedFromOscillator(event : RemoveEffectFromOscillatorRequest){
        if(model == event.model){
            val removeIds = model.effectIds.remove(event.effectId)
        }
    }
    fun onEffectAddedToOscillator(event : AddEffectToOscillatorRequest){
        if(model == event.model){
            model.effectIds.add(event.effectId)
        }
    }
}