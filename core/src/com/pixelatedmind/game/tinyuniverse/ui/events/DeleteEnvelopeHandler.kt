package com.pixelatedmind.game.tinyuniverse.ui.events

import com.pixelatedmind.game.tinyuniverse.ui.EnvelopeRepository

class DeleteEnvelopeHandler(val repository: EnvelopeRepository) {
    fun handle(request : DeleteEnvelopeRequest) {
        repository.remove(request.model)
    }
}