package com.pixelatedmind.game.tinyuniverse.ui.events

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect.EffectFactory
import com.pixelatedmind.game.tinyuniverse.ui.model.NameableId

class CreateEffectRequest(val id : NameableId, val effectFactory: EffectFactory) {

}