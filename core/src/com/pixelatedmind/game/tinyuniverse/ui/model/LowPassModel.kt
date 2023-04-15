package com.pixelatedmind.game.tinyuniverse.ui.model

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.filter.LowPassSlope
import com.pixelatedmind.game.tinyuniverse.ui.PiecewiseModel
import java.util.*

class LowPassModel(name : String) : NameableId(UUID.randomUUID().toString(), name) {
    constructor() : this("") {
    }
    var cutoffFrequency = 1000f

    var cutoffFrequencytStreamRange : Range<Int>? = null
    var cutoffFrequencyStream : PiecewiseModel? = null

    var resonance = 1f
    var resonanceStream : PiecewiseModel? = null

    var overdrive = 1f
    var overdriveStream : PiecewiseModel? = null

    var slope  = LowPassSlope.Slope12
}