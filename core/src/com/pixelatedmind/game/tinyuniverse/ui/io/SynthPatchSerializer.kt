package com.pixelatedmind.game.tinyuniverse.ui.io

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.pixelatedmind.game.tinyuniverse.ui.PiecewiseModel
import com.pixelatedmind.game.tinyuniverse.ui.events.CreateLowPassEffectRequest
import com.pixelatedmind.game.tinyuniverse.ui.model.EffectRef
import com.pixelatedmind.game.tinyuniverse.ui.model.LowPassModel
import com.pixelatedmind.game.tinyuniverse.ui.model.NameableId
import com.pixelatedmind.game.tinyuniverse.ui.model.OscillatorModel
import java.rmi.UnexpectedException

class SynthPatchSerializer {
    fun deserialize(modelString : String) : RuntimeModelContainer{
        val mapper = ObjectMapper()
        val model = mapper.readValue(modelString, SynthPatchDTO::class.java)

        val envelopes = model.envelopes.map{mapModelEnvelopeToRuntime(it)}
        val effects = model.effects.map{mapModelEffectToRuntime(it, envelopes)}

        val oscillators = model.oscillators.map{oscillatorModel->
            val newModel = OscillatorModel()

            newModel.baseWaveformId = oscillatorModel.baseWaveformId
            newModel.unisonVoices = oscillatorModel.unisonVoices
            newModel.enabled = oscillatorModel.enabled
            newModel.effectIds.addAll(oscillatorModel.effectIds)

            newModel.amplitude = oscillatorModel.amplitude
            if(oscillatorModel.amplitudeEnv != null) {
                newModel.amplitudeEnv = envelopes.firstOrNull{it.id == oscillatorModel.amplitudeEnv!!.id}
            }

            newModel.dutyCycle = oscillatorModel.dutyCycle
            if(oscillatorModel.dutyCycleEnv != null) {
                newModel.dutyCycleEnv = envelopes.firstOrNull{it.id == oscillatorModel.dutyCycleEnv!!.id}
            }

            newModel.semitoneOffset = oscillatorModel.semitoneOffset
            newModel.semitoneEnvelopeRange = oscillatorModel.semitoneEnvelopeRange
            if(oscillatorModel.semitoneOffsetEnv != null) {
                newModel.semitoneOffsetEnv = envelopes.firstOrNull{it.id == oscillatorModel.semitoneOffsetEnv!!.id}
            }

            newModel.fineTuneCentOffset = oscillatorModel.fineTuneCentOffset
            newModel.fineTuneCentRange = oscillatorModel.fineTuneCentRange
            if(oscillatorModel.fineTuneCentOffsetEnv != null) {
                newModel.fineTuneCentOffsetEnv = envelopes.firstOrNull{it.id == oscillatorModel.fineTuneCentOffsetEnv!!.id}
            }
            newModel
        }
        val result= RuntimeModelContainer(oscillators, envelopes, effects)
        return result
    }

    private fun mapModelEffectToRuntime(effect : NameableId, envelopes : List<PiecewiseModel>) : NameableId {
        if (effect is LowPassModelDTO) {
            val lpf = effect
            val lowpassModel = LowPassModel(lpf.name)
            lowpassModel.id = lpf.id
            lowpassModel.slope = lpf.slope

            lowpassModel.cutoffFrequency = lpf.cutoffFrequency
            lowpassModel.cutoffFrequencytStreamRange = lpf.cutoffFrequencytStreamRange
            if(lpf.cutoffFrequencyStream!=null){
                lowpassModel.cutoffFrequencyStream = envelopes.firstOrNull{it.id == lpf.cutoffFrequencyStream!!.id}
            }

            lowpassModel.overdrive = lpf.overdrive
            if(lpf.overdriveStream!=null) {
                lowpassModel.overdriveStream = envelopes.firstOrNull{it.id == lpf.overdriveStream!!.id}
            }

            lowpassModel.resonance = lpf.resonance
            if(lpf.resonanceStream!=null){
                lowpassModel.resonanceStream = envelopes.firstOrNull{it.id == lpf.resonanceStream!!.id}
            }
            return lowpassModel
        } else {
            throw UnexpectedException("Cannot handle loading this type of effect")
        }
    }

    private fun mapModelEnvelopeToRuntime(model : PiecewiseModelDTO) : PiecewiseModel{
        val result = PiecewiseModel()
        result.startY = model.startY
        result.endY = model.endY
        result.repeatForever = model.repeatForever
        result.interactive = model.interactive
        result.timesToRepeat = model.timesToRepeat
        result.id = model.id
        result.durationSecs = model.durationSecs
        result.name = model.name
        model.pieces.forEach{
            result.add(mapModelPieceToRuntime(it))
        }
        return result
    }

    private fun mapModelPieceToRuntime(model : PieceModelDTO) : PiecewiseModel.Piece{
        val result = PiecewiseModel.Piece(Vector2(model.x,model.y),model.interpolationName, Interpolation.linear)
        return result
    }

    fun serialize(model : SynthPatchDTO) : String {
        val mapper = ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
        val jsonDataString = mapper.writeValueAsString(model)
        return jsonDataString
    }

    fun mapRuntimeToDTO(oscillatorModels : List<OscillatorModel>, envelopes : List<PiecewiseModel>, effects : List<NameableId>) : SynthPatchDTO{
        val model = SynthPatchDTO()
        model.oscillators.addAll(oscillatorModels.map(this::mapRuntimeOscillatorModel))
        model.envelopes.addAll(envelopes.map(this::mapRuntimePiecewiseModel))
        model.effects.addAll(effects.map{mapRuntimeEffectToModel(it)})
        return model
    }

    private fun mapRuntimeEffectToModel(effectId : NameableId) : NameableId{
        if(effectId is LowPassModel){
            val model = LowPassModelDTO()
            model.name = effectId.name
            model.id = effectId.id
            model.slope = effectId.slope

            model.cutoffFrequency = effectId.cutoffFrequency
            model.cutoffFrequencytStreamRange = effectId.cutoffFrequencytStreamRange
            if(effectId.cutoffFrequencyStream!=null){
                model.cutoffFrequencyStream = NameableId(effectId.cutoffFrequencyStream!!.id, effectId.cutoffFrequencyStream!!.name)
            }

            model.resonance = effectId.resonance
            if(effectId.resonanceStream!=null){
                model.resonanceStream = NameableId(effectId.resonanceStream!!.id, effectId.resonanceStream!!.name)
            }

            model.overdrive = effectId.overdrive
            if(effectId.overdriveStream!=null){
                model.overdriveStream = NameableId(effectId.overdriveStream!!.id,effectId.overdriveStream!!.name)
            }
            return model
        }else{
            throw IllegalArgumentException("Unable to serialize effect name=[${effectId.name}] id=[${effectId.id}]")
        }
    }

    private fun mapRuntimePiecewiseFunction(model : PiecewiseModel.Piece) : PieceModelDTO{
        val result = PieceModelDTO()
        result.x = model.start.x
        result.y = model.start.y
        result.interpolationName = model.interpolationName
        return result
    }

    private fun mapRuntimePiecewiseModel(model : PiecewiseModel) : PiecewiseModelDTO{
        val result = PiecewiseModelDTO()
        result.id = model.id
        result.name = model.name
        result.durationSecs = model.durationSecs
        result.startY = model.startY
        result.endY = model.endY
        result.interactive = model.interactive
        result.repeatForever = model.repeatForever
        result.timesToRepeat = model.timesToRepeat
        result.pieces.addAll(model.getPieces().map(this::mapRuntimePiecewiseFunction))
        return result
    }

    private fun mapRuntimeOscillatorModel(runtimeModel : OscillatorModel) : OscillatorModelDTO {
        val oscillatorModel = OscillatorModelDTO()
        oscillatorModel.baseWaveformId = runtimeModel.baseWaveformId
        val simpleEffectIds = runtimeModel.effectIds.map{ EffectRef(NameableId(it.nameId.id, it.nameId.name),it.enabled) }
        oscillatorModel.effectIds.addAll(simpleEffectIds)
        oscillatorModel.enabled = runtimeModel.enabled

        oscillatorModel.amplitude = runtimeModel.amplitude
        if(runtimeModel.amplitudeEnv!=null) {
            oscillatorModel.amplitudeEnv = NameableId(runtimeModel.amplitudeEnv!!.id, runtimeModel.amplitudeEnv!!.name)
        }

        oscillatorModel.dutyCycle = runtimeModel.dutyCycle
        if(oscillatorModel.dutyCycleEnv!=null){
            oscillatorModel.dutyCycleEnv = NameableId(runtimeModel.dutyCycleEnv!!.id, runtimeModel.dutyCycleEnv!!.name)
        }

        oscillatorModel.semitoneOffset = runtimeModel.semitoneOffset
        oscillatorModel.semitoneEnvelopeRange = runtimeModel.semitoneEnvelopeRange
        if(runtimeModel.semitoneOffsetEnv!=null){
            oscillatorModel.semitoneOffsetEnv = NameableId(runtimeModel.semitoneOffsetEnv!!.id, runtimeModel.semitoneOffsetEnv!!.name)
        }

        oscillatorModel.fineTuneCentOffset = runtimeModel.fineTuneCentOffset
        oscillatorModel.fineTuneCentRange = runtimeModel.fineTuneCentRange
        if(runtimeModel.fineTuneCentOffsetEnv!=null) {
            oscillatorModel.fineTuneCentOffsetEnv = NameableId(runtimeModel.fineTuneCentOffsetEnv!!.id, runtimeModel.fineTuneCentOffsetEnv!!.name)
        }

        oscillatorModel.unisonVoices = runtimeModel.unisonVoices
        return oscillatorModel
    }
}