package com.pixelatedmind.game.tinyuniverse.ui.io

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator
import com.pixelatedmind.game.tinyuniverse.ui.PiecewiseModel
import com.pixelatedmind.game.tinyuniverse.ui.model.LowPassModel
import com.pixelatedmind.game.tinyuniverse.ui.model.NameableId
import com.pixelatedmind.game.tinyuniverse.ui.model.OscillatorModel


class SynthPatchDTO {
    var oscillators = mutableListOf<OscillatorModelDTO>()
    var effects = mutableListOf<NameableId>()
    var envelopes = mutableListOf<PiecewiseModelDTO>()

    fun test(){
        val lowpassModel = LowPassModel("testModelYay")
        lowpassModel.cutoffFrequency=12345f
        effects.add(lowpassModel)
        val mapper = ObjectMapper()
        val jsonDataString = mapper.writeValueAsString(this)
        val testValue = mapper.readValue(jsonDataString, SynthPatchDTO::class.java)
    }
}