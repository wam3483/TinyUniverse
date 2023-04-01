package com.pixelatedmind.game.tinyuniverse.ui.events

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pixelatedmind.game.tinyuniverse.generation.music.Notes
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.EnvelopeFactory
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.MultiplexEnvelopeFactory
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.BaseWaveformStreamFactory
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.StreamFactory
import com.pixelatedmind.game.tinyuniverse.ui.EnvelopeRepository
import com.pixelatedmind.game.tinyuniverse.ui.OscilatorPanel
import com.pixelatedmind.game.tinyuniverse.ui.model.OscillatorModel
import com.pixelatedmind.game.tinyuniverse.ui.patch.OscillatorModelEnvelopeFactory
import com.pixelatedmind.game.tinyuniverse.util.EventBus

class CreateOscillatorHandler(val multiplexEnvelopeFactory : MultiplexEnvelopeFactory, val table : Table, val envelopeRepository : EnvelopeRepository,
                              val waveformStreamFactory : StreamFactory, val eventbus : EventBus) {
    val notes = Notes()
    private val modelFactoryMap = mutableMapOf<OscillatorModel, Pair<EnvelopeFactory, Table>>()

    fun onCreateOscillatorRequest(request : CreateOscillatorRequest){
        val model = OscillatorModel()
        val factory = OscillatorModelEnvelopeFactory(model, waveformStreamFactory, notes)
        multiplexEnvelopeFactory.addFactory(factory)

        val oscPanel = OscilatorPanel(model, waveformStreamFactory, envelopeRepository, eventbus)
        table.add(oscPanel).expandX().fillX().padTop(7f)
        table.row()

        modelFactoryMap[model] = Pair(factory, oscPanel)
    }

    fun onDeleteOscillatorRequest(request : DeleteOscillatorRequest){
        val data = modelFactoryMap[request.model]
        if(data!=null) {
            data.second.remove()
            multiplexEnvelopeFactory.removeFactory(data.first)
            modelFactoryMap.remove(request.model)
        }
    }
}