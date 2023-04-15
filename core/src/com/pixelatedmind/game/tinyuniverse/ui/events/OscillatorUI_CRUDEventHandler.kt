package com.pixelatedmind.game.tinyuniverse.ui.events

import com.pixelatedmind.game.tinyuniverse.ui.EffectRefListViewAdapter
import com.pixelatedmind.game.tinyuniverse.ui.model.OscillatorModel

class OscillatorUI_CRUDEventHandler(val model : OscillatorModel, val effectListViewAdapter: EffectRefListViewAdapter) {
    fun onEffectRemovedFromOscillator(event : RemoveEffectFromOscillatorRequest){
        if(model == event.model){
            effectListViewAdapter.removeValue(event.effectId, false)
        }
    }

    fun onEffectAddedToOscillator(event : AddEffectToOscillatorRequest){
        if(model == event.model){
            effectListViewAdapter.add(event.effectId)
        }
    }
}