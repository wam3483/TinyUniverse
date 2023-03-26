package com.pixelatedmind.game.tinyuniverse.ui.events

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.pixelatedmind.game.tinyuniverse.ui.EnvelopeRepository
import com.pixelatedmind.game.tinyuniverse.ui.PiecewiseModel

class CreateEnvelopeHandler(val envelopeRepository : EnvelopeRepository) {
    fun handle(request : CreateEnvelopeRequest){
        val model = PiecewiseModel()
        model.name = getName()
        model.durationSecs = 1f
        model.repeatForever = true
        model.add(PiecewiseModel.Piece(Vector2(0f,0f),"linear", Interpolation.linear))
        model.add(PiecewiseModel.Piece(Vector2(1f,1f),"linear", Interpolation.linear))
        envelopeRepository.add(model)
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