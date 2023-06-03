package com.pixelatedmind.game.tinyuniverse.ui.events

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect.ReverbEffectFactory
import com.pixelatedmind.game.tinyuniverse.ui.EnvelopeRepository
import com.pixelatedmind.game.tinyuniverse.ui.effect.ReverbEffectWidget
import com.pixelatedmind.game.tinyuniverse.ui.model.LowPassModel
import com.pixelatedmind.game.tinyuniverse.ui.model.ReverbModel
import com.pixelatedmind.game.tinyuniverse.util.EventBus

class CreateReverbEffectRequest(val model : ReverbModel? = null){
}

class CreateReverbEffectHandler(val eventBus : EventBus, val effectsTable : Table,
                                private val envelopeRepository: EnvelopeRepository,
                                private val skin : Skin) {
    fun onCreateReverbEffectRequest(event : CreateReverbEffectRequest){
        val model =
                if(event.model == null){
                    ReverbModel("Low Pass Effect", 1f, .2f)
                }else{
                    event.model
                }
        val widget = ReverbEffectWidget(model,envelopeRepository, skin, eventBus)
        effectsTable.add(widget)
        effectsTable.row()
        val factory = ReverbEffectFactory(model)
        val createEffectRequest = CreateEffectRequest(model, factory)
        eventBus.post(createEffectRequest)
    }
}