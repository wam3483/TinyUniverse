package com.pixelatedmind.game.tinyuniverse.ui.patch

import com.pixelatedmind.game.tinyuniverse.generation.music.Notes
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect.GainEffect
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect.StartTimeEffect
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect.UnisonEffect
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.Envelope
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.EnvelopeFactory
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.*
import com.pixelatedmind.game.tinyuniverse.ui.EffectRepository
import com.pixelatedmind.game.tinyuniverse.ui.model.OscillatorModel

class OscillatorModelEnvelopeFactory(val oscillatorModel : OscillatorModel,
                                     val waveformStreamFactory: StreamFactory,
                                     val effectRepository : EffectRepository,
                                     val noteUtil : Notes) : EnvelopeFactory {

    private fun getWaveformStream(frequencyInput : FloatInputStream, phase : Float) : FloatInputStream {
        var dutyCycleStream : FloatInputStream?
        if(oscillatorModel.dutyCycleEnv != null){
            dutyCycleStream = PiecewiseStream(oscillatorModel.dutyCycleEnv!!)
        }else{
            dutyCycleStream = ConstantStream(oscillatorModel.dutyCycle)
        }
        return waveformStreamFactory.new(oscillatorModel.baseWaveformId, dutyCycleStream, frequencyInput, phase)
    }

    private fun getAmplitudeStream(stream : FloatInputStream,  releaseListeners : MutableList<()->Unit>, completeListener : ()->Unit) : FloatInputStream {
        if(oscillatorModel.amplitudeEnv==null){
            releaseListeners.add(completeListener)
            return GainEffect(stream, ConstantStream(oscillatorModel.amplitude))
        }else{
            //amplitude envelope is ALWAYS interactable.
            val ampStream = UIInteractableEnvelope(oscillatorModel.amplitudeEnv!!)
            ampStream.addCompleteListener(completeListener)
            releaseListeners.add(ampStream::release)
            return GainEffect(stream, ampStream)
        }
    }

    private fun getSemitoneOffsetStream(frequency : Float, releaseListeners : MutableList<()->Unit>) : FloatInputStream{
        var semitones = noteUtil.getMidiTableIndexFor(frequency)
        var semitoneStream : FloatInputStream
        if(oscillatorModel.semitoneOffsetEnv != null){
            val semitoneFunc = oscillatorModel.semitoneOffsetEnv!!.copy()
            val startSemitoneIndex = semitones + oscillatorModel.semitoneEnvelopeRange!!.min!!
            val endSemitoneIndex = semitones + oscillatorModel.semitoneEnvelopeRange!!.max!!
            semitoneFunc.startY = noteUtil.midi[startSemitoneIndex]
            semitoneFunc.endY = noteUtil.midi[endSemitoneIndex]

            println("frequency range: ${semitoneFunc.startY} to ${semitoneFunc.endY}")

            if(oscillatorModel.semitoneOffsetEnv!!.interactive){
                val releaseableStream = UIInteractableEnvelope(semitoneFunc)
                releaseListeners.add(releaseableStream::release)
                semitoneStream = releaseableStream
            }else {
                semitoneStream = PiecewiseStream(semitoneFunc)
            }
        }
        else {
            semitones += oscillatorModel.semitoneOffset
            if(semitones<0){
                semitones = 0
            }else if(semitones >= noteUtil.midi.size){
                semitones = noteUtil.midi.size - 1
            }
            val semitoneFreq = noteUtil.midi[semitones]
            semitoneStream = ConstantStream(semitoneFreq)
        }
        return semitoneStream
    }

    private fun getCentStream(baseFrequencyStream : FloatInputStream,  releaseListeners : MutableList<()->Unit>) : FloatInputStream{
        val centStream = if(oscillatorModel.fineTuneCentOffsetEnv != null) {
            if (!oscillatorModel.semitoneOffsetEnv!!.interactive) {
                PiecewiseStream(oscillatorModel.semitoneOffsetEnv!!)
            } else {
                val releaseableStream = UIInteractableEnvelope(oscillatorModel.semitoneOffsetEnv!!)
                releaseListeners.add(releaseableStream::release)
                releaseableStream
            }
        }
        else{
            val normalizedCentOffset = oscillatorModel.fineTuneCentOffset / 100f
            ConstantStream(normalizedCentOffset)
        }
        return CentOffsetStream(centStream, baseFrequencyStream, noteUtil)
    }

    private fun getBaseStream(frequency : Float, releaseListeners : MutableList<()->Unit>, phase : Float = 0f) : FloatInputStream{
        val semitoneOffsetStream = getSemitoneOffsetStream(frequency, releaseListeners)
        val centOffsetStream = getCentStream(semitoneOffsetStream, releaseListeners)
        val waveformStream = getWaveformStream(centOffsetStream, phase)
        return waveformStream
    }

    private fun applyEffects(inputStream : FloatInputStream) : FloatInputStream{
        var stream = inputStream
        oscillatorModel.effectIds.forEach{effectId ->
            if(effectId.enabled) {
                val effectFactory = effectRepository.get(effectId.nameId)
                if (effectFactory != null) {
                    stream = effectFactory.new(stream)
                }
            }
        }
        return stream
    }

    override fun newEnvelope(frequency: Float): Envelope {
        val releaseListeners = mutableListOf<()->Unit>()

        val baseStream = if(oscillatorModel.unisonVoices>1){
            UnisonEffect(frequency,
            {
                detunedFrequency ->
                val phaseRandomization = (Math.PI * 2 * (Math.random())).toFloat()
                val waveformStream = getBaseStream(detunedFrequency, releaseListeners, 0f)
                waveformStream
            },
            oscillatorModel.unisonVoices,1f)
        }
        else{
            getBaseStream(frequency, releaseListeners)
        }

        var result : EnvelopeStream? = null
        val completeCallback = {
            result!!.setIsComplete(true)
        }

        val amplitudeAdjustedStream = getAmplitudeStream(baseStream, releaseListeners, completeCallback)
        var stream : FloatInputStream = amplitudeAdjustedStream

        stream = applyEffects(stream)

        stream = StartTimeEffect(stream)
        result = EnvelopeStream(stream)
        //when a release event is triggered all registered streams will be notified
        result.addReleaseListener {
            releaseListeners.forEach{
                it.invoke()
            }
        }
        return result
    }
}