package com.pixelatedmind.game.tinyuniverse.ui.model

import com.pixelatedmind.game.tinyuniverse.ui.PiecewiseModel

class OscillatorModel {
    val effectIds = mutableListOf<EffectRef>()

    var enabled : Boolean = true
    var baseWaveformId : String = "sine"

    var amplitudeEnv : PiecewiseModel? = null
    var amplitude : Float = 1f

    var dutyCycleEnv : PiecewiseModel? = null
    var dutyCycle : Float = .5f

    var semitoneOffset = 0
    var semitoneEnvelopeRange : Range<Int>? = null
    var semitoneOffsetEnv : PiecewiseModel? = null

    var fineTuneCentOffset = 0
    var fineTuneCentRange : Range<Int>? = null
    var fineTuneCentOffsetEnv : PiecewiseModel? = null

    var unisonVoices = 0 //may need more control around this later.
}

class EffectRef(val nameId : NameableId, var enabled : Boolean){
    constructor() : this(NameableId("",""),false){}
}

class Range<T>(var min : T?, var max : T?){
    constructor() : this(null, null)
    {}
}