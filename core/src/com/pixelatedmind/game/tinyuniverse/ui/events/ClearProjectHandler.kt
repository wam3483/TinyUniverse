package com.pixelatedmind.game.tinyuniverse.ui.events

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.MultiplexEnvelopeFactory
import com.pixelatedmind.game.tinyuniverse.ui.EffectRepository
import com.pixelatedmind.game.tinyuniverse.ui.EnvelopeListViewAdapter
import com.pixelatedmind.game.tinyuniverse.ui.EnvelopeRepository
import com.pixelatedmind.game.tinyuniverse.ui.OscillatorRepository

class ClearProjectHandler(val oscillatorsContainer : Table, val oscillatorRepository : OscillatorRepository,
                          val multiplexEnvelopeFactory : MultiplexEnvelopeFactory,
                          val envelopeRepository : EnvelopeRepository, val envelopeContainer : EnvelopeListViewAdapter,
                          val effectRepository : EffectRepository, val effectTable : Table) {
    fun onClearProjectEvent(evt : ClearProjectRequest){
        multiplexEnvelopeFactory.clear()

        oscillatorsContainer.clear()
        oscillatorRepository.clear()

        envelopeRepository.clear()
        envelopeContainer.clear()

        effectRepository.clear()
        effectTable.clear()
    }
}