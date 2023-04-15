package com.pixelatedmind.game.tinyuniverse.ui.events

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.pixelatedmind.game.tinyuniverse.ui.EnvelopeListViewAdapter
import com.pixelatedmind.game.tinyuniverse.ui.EnvelopeRepository
import com.pixelatedmind.game.tinyuniverse.ui.PiecewiseModel

class CreateEnvelopeHandler(val envelopeRepository : EnvelopeRepository, val envelopeList : EnvelopeListViewAdapter) {
    fun onCreateEnvelopeRequest(request : CreateEnvelopeRequest){
        val model = if(request.model==null) {
            val newEnvelope = PiecewiseModel()
            newEnvelope.name = getName()
            newEnvelope.durationSecs = 1f
            newEnvelope.repeatForever = true
            newEnvelope.add(PiecewiseModel.Piece(Vector2(0f, 0f), "linear", Interpolation.linear))
            newEnvelope.add(PiecewiseModel.Piece(Vector2(1f, 1f), "linear", Interpolation.linear))
            newEnvelope
        }else{
            request.model!!
        }
        envelopeRepository.add(model)
        envelopeList.add(model)
    }
    private fun getName() : String {
        var index = 1
        var name = "new Envelope 1"
        while(!envelopeRepository.isModelIdAvailable(name)){
            index++
            name = "new Envelope $index"
        }
        return name
    }
}