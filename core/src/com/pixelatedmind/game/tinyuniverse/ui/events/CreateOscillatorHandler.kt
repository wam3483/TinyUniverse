package com.pixelatedmind.game.tinyuniverse.ui.events

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.BaseWaveformStreamFactory
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.StreamFactory
import com.pixelatedmind.game.tinyuniverse.ui.EnvelopeRepository
import com.pixelatedmind.game.tinyuniverse.ui.OscilatorPanel
import com.pixelatedmind.game.tinyuniverse.ui.model.OscillatorModel
import com.pixelatedmind.game.tinyuniverse.util.EventBus

class CreateOscillatorHandler(val table : Table, val envelopeRepository : EnvelopeRepository, val waveformStreamFactory : StreamFactory, val eventbus : EventBus) {

    fun handle(request : CreateOscillatorRequest){
        val model = OscillatorModel()
        val oscPanel = OscilatorPanel(model, waveformStreamFactory, envelopeRepository, eventbus)
        table.add(oscPanel).expandX().fillX().padTop(7f)
        oscPanel.height = 100f
        oscPanel.width = 20f
        table.row()
    }
}