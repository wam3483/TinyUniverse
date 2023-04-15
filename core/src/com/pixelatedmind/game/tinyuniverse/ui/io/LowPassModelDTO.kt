package com.pixelatedmind.game.tinyuniverse.ui.io

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.filter.LowPassSlope
import com.pixelatedmind.game.tinyuniverse.ui.PiecewiseModel
import com.pixelatedmind.game.tinyuniverse.ui.model.NameableId
import com.pixelatedmind.game.tinyuniverse.ui.model.Range
import java.util.*

class LowPassModelDTO : NameableId(UUID.randomUUID().toString(), "") {
    var cutoffFrequency = 1000f

    var cutoffFrequencytStreamRange : Range<Int>? = null
    var cutoffFrequencyStream : NameableId? = null

    var resonance = 1f
    var resonanceStream : NameableId? = null

    var overdrive = 1f
    var overdriveStream : NameableId? = null

    var slope  = LowPassSlope.Slope12
}