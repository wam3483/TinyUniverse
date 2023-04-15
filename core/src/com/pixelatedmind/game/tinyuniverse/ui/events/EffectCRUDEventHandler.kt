package com.pixelatedmind.game.tinyuniverse.ui.events

import com.pixelatedmind.game.tinyuniverse.ui.EffectRepository

class EffectCRUDEventHandler(val effectRepository : EffectRepository) {
    fun handleCreateEffectEvent(evt : CreateEffectRequest){
        effectRepository.add(evt.id, evt.effectFactory)
    }

    fun handleDeleteEffectEvent(evt : DeleteEffectRequest){
        effectRepository.remove(evt.id)
    }
}