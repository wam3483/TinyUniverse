package com.pixelatedmind.game.tinyuniverse.ui.events

import com.pixelatedmind.game.tinyuniverse.ui.EnvelopeListViewAdapter

class CreateDeletePiecewiseModelHandler(val envelopeList : EnvelopeListViewAdapter) {
    fun createPiecewiseFunction(request : CreatePiecewiseModelRequest){
        envelopeList.add(request.piecewiseModel)
    }
    fun deletePiecewiseModel(request : DeletePiecewiseModelRequest){
        envelopeList.removeValue(request.model, true)
    }
}