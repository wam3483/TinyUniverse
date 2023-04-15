package com.pixelatedmind.game.tinyuniverse.ui.io

import com.pixelatedmind.game.tinyuniverse.ui.PiecewiseModel
import com.pixelatedmind.game.tinyuniverse.ui.model.NameableId
import com.pixelatedmind.game.tinyuniverse.ui.model.OscillatorModel

class RuntimeModelContainer(val oscillators : List<OscillatorModel>,
                            val envelopes : List<PiecewiseModel>,
                            val effects : List<NameableId>) {
}