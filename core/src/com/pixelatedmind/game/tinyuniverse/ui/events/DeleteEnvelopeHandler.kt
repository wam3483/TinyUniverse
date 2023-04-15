package com.pixelatedmind.game.tinyuniverse.ui.events

import com.pixelatedmind.game.tinyuniverse.ui.EnvelopeListViewAdapter
import com.pixelatedmind.game.tinyuniverse.ui.EnvelopeRepository

class DeleteEnvelopeHandler(val repository: EnvelopeRepository, val envelopeList : EnvelopeListViewAdapter) {
    fun handle(request : DeleteEnvelopeRequest) {
        repository.remove(request.model)
        envelopeList.removeValue(request.model, true)
    }
}