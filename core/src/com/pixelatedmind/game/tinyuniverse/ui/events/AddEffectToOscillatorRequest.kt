package com.pixelatedmind.game.tinyuniverse.ui.events

import com.pixelatedmind.game.tinyuniverse.ui.model.EffectRef
import com.pixelatedmind.game.tinyuniverse.ui.model.OscillatorModel

class AddEffectToOscillatorRequest(val model : OscillatorModel, val effectId: EffectRef) {
}