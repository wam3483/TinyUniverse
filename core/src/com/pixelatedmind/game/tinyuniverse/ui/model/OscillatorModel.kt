package com.pixelatedmind.game.tinyuniverse.ui.model

import com.pixelatedmind.game.tinyuniverse.ui.PiecewiseModel

class OscillatorModel {
    var baseWaveformId : String = "sine"

    var amplitudeEnv : PiecewiseModel? = null
    var amplitude : Float = 0f

    var semitoneOffset = 0
    var semitoneOffsetEnv : PiecewiseModel? = null

    var fineTuneCentOffset = 0
    var fineTuneCentOffsetEnv : PiecewiseModel? = null

    var unisonVoices = 0 //may need more control around this later.
}