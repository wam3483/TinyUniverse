package com.pixelatedmind.game.tinyuniverse.ui.events

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect.LowPassEffectFactory
import com.pixelatedmind.game.tinyuniverse.ui.EnvelopeRepository
import com.pixelatedmind.game.tinyuniverse.ui.effect.LowPassEffectWidget
import com.pixelatedmind.game.tinyuniverse.ui.model.LowPassModel
import com.pixelatedmind.game.tinyuniverse.util.EventBus

class CreateLowPassEffectRequest(val model : LowPassModel? = null) {
}

class CreateLowPassEffectHandler(val eventBus : EventBus, val effectsTable : Table,
                                 private val envelopeRepository: EnvelopeRepository,
                                 private val skin : Skin) {
    fun handleCreateLowPassEffect(event : CreateLowPassEffectRequest){
        val lowPassModel =
            if(event.model == null){
                LowPassModel("Low Pass Effect")
            }else{
                event.model
            }
        val lowpPassEffectPanel = LowPassEffectWidget(lowPassModel, envelopeRepository,skin, eventBus)
        effectsTable.add(lowpPassEffectPanel)
        effectsTable.row()
        val factory = LowPassEffectFactory(lowPassModel)
        eventBus.post(CreateEffectRequest(lowPassModel, factory))
    }
}