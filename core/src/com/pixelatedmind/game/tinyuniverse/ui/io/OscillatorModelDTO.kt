package com.pixelatedmind.game.tinyuniverse.ui.io

import com.pixelatedmind.game.tinyuniverse.ui.PiecewiseModel
import com.pixelatedmind.game.tinyuniverse.ui.model.EffectRef
import com.pixelatedmind.game.tinyuniverse.ui.model.NameableId
import com.pixelatedmind.game.tinyuniverse.ui.model.Range

class OscillatorModelDTO {
    val effectIds = mutableListOf<EffectRef>()

    var enabled : Boolean = true
    var baseWaveformId : String = "sine"

    var amplitudeEnv : NameableId? = null
    var amplitude : Float = 1f

    var dutyCycleEnv : NameableId? = null
    var dutyCycle : Float = .5f

    var semitoneOffset = 0
    var semitoneEnvelopeRange : Range<Int>? = null
    var semitoneOffsetEnv : NameableId? = null

    var fineTuneCentOffset = 0
    var fineTuneCentRange : Range<Int>? = null
    var fineTuneCentOffsetEnv : NameableId? = null

    var unisonVoices = 0 //may need more control around this later.
}